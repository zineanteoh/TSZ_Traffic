/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tsz_traffic;

import java.util.ArrayList;

/**
 *
 * @author Zi Nean Teoh
 */
public class Main {

    static final double INCREMENT = 0.1;
    static int carCounter = 1;
    static int populateGap = 15;
    static int interrupted = 0;

    public static void main(String[] args) {
        runSimulation();
    }

    public static void runSimulation() {
        // how long simulation will run for
        double endTime = 20;

        // Road of interest: 
        int[] roadLength = {300, 250};
        // (startingState, updateTime, lastUpdatedTime) 
        Light[] lightArray = {new Light(0, 4, 0), new Light(0, 4, 2)};

        // create an arraylist of road segments called segments
        ArrayList<Road> roadArray = new ArrayList<>();

        // Populate each road segment with cars to start our simulation
        for (int i = 0; i < roadLength.length; i++) {
            roadArray.add(new Road(roadLength[roadLength.length - i - 1], lightArray[roadLength.length - i - 1]));
            roadArray.set(i, populate(roadArray.get(i)));
        }
        
        // Start the loop!! 
        for (double time = 0; time < endTime; time += INCREMENT) {
            // loop through each segment
            for (int i = 0; i < roadArray.size(); i++) {
                // update the light of this segment
                if (i == 0) {
                    // the first segment doesn't need density check. so call simple update
                    roadArray.get(0).simpleUpdate(time);
                } else {
                    // check density of the lane in front and that current time meets CHECK condition (can be interrupted)
                    if (roadArray.get(i).getLights().isGreen() && roadArray.get(i - 1).densityCheck() && roadArray.get(i).checkCondition(time)) { 
                        // interrupt this traffic light! 
                        roadArray.get(i).getLights().interrupt();
                        System.out.println("TRAFFIC LIGHT INTERRUPTED");
                        interrupted++;
                    } else {
                        roadArray.get(i).updateLight(time); // otherwise update based on status quo
                    }
                } 
                

                if (roadArray.get(i).getLights().isGreen()) {
                    // green light = all cars goCars at full speed
                    roadArray.get(i).goCars(INCREMENT);
                } else {
                    // red light = all cars close in...
                    roadArray.get(i).closeInCars(INCREMENT);
                }

                // migrate car between road segments
                if (roadArray.get(i).carExit()) {
                    // get the last car from this segment
                    Car lastCar = roadArray.get(i).getFrontCar();
                    // remove the last car and set it to a new road
                    Road updatedRoad = roadArray.get(i).removeCar(lastCar);
                    // update the road onto segments arraylist
                    roadArray.set(i, updatedRoad);
                    if (i != 0) { // add that removed car to next road segment 
                        Road updatedNextRoad = roadArray.get(i - 1).addCar(lastCar);
                        roadArray.set(i - 1, updatedNextRoad);
                    }
                }

                // print road
                printRoad(roadArray, time);
            }

            // populate car at the last segment whenever possible (simulate a bad traffic congestion)
            roadArray.set(roadArray.size() - 1, populateRoad(roadArray.get(roadArray.size() - 1)));
        }
        
        System.out.println("\nNumber of times interrupted: " + interrupted);
    }
    
    public static Road populate(Road segment) {
        int lengthOfSegment = segment.getLength();
        // populate index i of segments with random cars
        for (int y = lengthOfSegment - populateGap; y > populateGap; y -= populateGap) { // populating with (populateGap) gaps 
            segment.populateCar(new Car(carCounter++, y, 13 + (int) (Math.random() * 5), 30 + (int) (Math.random() * 15)));
        }
        return segment;
    }
    
    public static Road populateRoad(Road segment) {
        // if there is space, populate car
        while (segment.carArray.get(segment.carArray.size() - 1).getY() > populateGap) {
            // add this car at the back of the last car
            segment.addCar(new Car(carCounter++, segment.carArray.get(segment.carArray.size() - 1).getY() - populateGap, 13 + (int) (Math.random() * 5), 50 + (int) (Math.random() * 15)));
        }
        return segment;
    }
    
    public static void printRoad(ArrayList<Road> segments, double time) {
        String output = "";
        String tempString;
        String inOutFluxString = "";
        for (int i = 0; i < segments.size(); i++) {
            // print traffic light state first
            output = segments.get(i).getLights().printState() + output;
            tempString = "";
            for (int j = 0; j < segments.get(i).carArray.size(); j++) {
                tempString = segments.get(i).carArray.get(j).getCarNumber() + "_" + tempString;
            }
            output = String.format("%" + (segments.get(i).ROAD_LENGTH / 3) + "s", tempString) + output;
            inOutFluxString = String.format("%-" + (segments.get(i).ROAD_LENGTH / 3) + "s", ("In/Out: " + segments.get(i).getInflux(segments, i) + "/" + segments.get(i).getOutflux())) + inOutFluxString;
        }
        System.out.println("");
        System.out.println("Time: " + Double.parseDouble(String.format("%.1f", time)));
        System.out.println(output);
        System.out.println(inOutFluxString);
        System.out.println("");
    }
    
}
