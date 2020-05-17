package tsz_traffic;

import java.util.ArrayList;

public class RoadSimulation extends Thread {

    private static int POPULATE_GAP = 20; // Distance between each populated car in feet 
    public static double width = 1; // Width of precaution
    public int carCounter;
    final int DIRECTION;
    public RoadSimulation oppositeRoad;
    public RoadSimulation rightRoad;
    public ArrayList<Road> roadArray;
    public static ResourceLock lock;
    private double roadWidth;
    public int crossroadID;
    public int totalCarPassed;

    public RoadSimulation(int[] roadData, Light[] lightData, int direction, ResourceLock lock, double roadWidth, int crossroadID) {
        this.lock = lock;
        this.DIRECTION = direction;
        this.carCounter = 1;
        this.roadArray = new ArrayList<Road>();                                                             // Create a road array
        this.roadArray = populateRoadArray(this.roadArray, roadData, lightData, this.DIRECTION);        // Populate the road array based on road and light data
        this.oppositeRoad = null;
        this.rightRoad = null;
        this.roadWidth = roadWidth;
        this.crossroadID = crossroadID;
        this.totalCarPassed = 0;
        System.out.println("Creating Thread... CR-ID: " + this.crossroadID + "; Direction: " + this.DIRECTION + "...");
    }

    public void run() {
        // Run simulation
        try {
            synchronized (this.lock) {
                for (double time = 0; time <= Main.simulationTime; time += Crossroad.TIME_INCREMENT) {
                    while (this.lock.flag != this.crossroadID * 2 - this.DIRECTION) {
                        this.lock.wait();
                    }

                    time = Math.round(time * 10) / 10.0; // round time to 1 dp
//                    System.out.println("Flag " + this.lock.flag + " running");

                    // Loop through road segments and execute 3 actions
                    for (int roadIndex = 0; roadIndex < this.roadArray.size(); roadIndex++) {
                        // Update the light of roadArray[index]
                        updateLightOfRoad(time, roadIndex, this.roadArray);

                        // Update the cars of roadArray[index]
                        updateCarOfRoad(roadIndex, this.roadArray);

                        // Update RoadArrays now that cars have moved
                        updateRoadArray(roadIndex, this.roadArray, this.rightRoad);
                    }

                    // Continue adding cars into the last segment of this road direction to simulate a congestion
                    // ONLY Applies to FIRST crossroad OR Vertical lane!
                    if (this.DIRECTION == Road.VERTICAL || this.crossroadID == 1) {
                        makeTrafficWorse(this.roadArray);
                    }
                    this.lock.flag++;
                    this.lock.notifyAll(); // Wakes up all threads that are waiting on this object's monitor
                }
            }
        } catch (NullPointerException e) {
            System.out.printf("Exception Thread %d: %s%n", this.lock.flag, e);
        } catch (InterruptedException e) {
            System.out.printf("Interrupted Exception at Thread %d: %s%n", this.lock.flag, e);
        }
    }

    private synchronized void updateLightOfRoad(double time, int index, ArrayList<Road> roadArray) {
        if (index == 0) {
            // Index = 0 means that this road is the first segment 
            // No density check required because there are no roads infront
            // Call simple update time instead
            roadArray.get(index).simpleUpdate(time);
        } else {
            // Check conditions to see if traffic light could be interrupted
            if (!roadArray.get(index).checkCondition(time) || !roadArray.get(index).getLights().isGreen()) {
                // One or more of the conditions are false. Call simple update time instead
                roadArray.get(index).simpleUpdate(time);
                return;
            }

            // Apply densityCheck() on the road segment behind roadArray[index]
            if (roadArray.get(index - 1).densityCheck() && Crossroad.interrupt) {
                // All 3 conditions have been met. Proceed to interrupting light
                roadArray.get(index).getLights().interrupt();
                System.out.printf("Traffic Light of Thread %s (road index %d, direction %d) has been Interrupted!%n", (this.lock.flag), index, this.DIRECTION);
                return;
            }

            // DensityCheck() returns false. Call simple update time instead
            roadArray.get(index).simpleUpdate(time);
        }
    }

    private synchronized void updateCarOfRoad(int index, ArrayList<Road> roadArray) {
//        // Removed
//        // Check the front road (index 0) for blocked crossroad
//        if (index == 0 && !roadArray.get(index).carArray.isEmpty()) {
//            // Check the y coordinate of the last car
//            double lastCarY = roadArray.get(index).getLastCar().getY();
//            if (lastCarY < this.roadWidth * this.width) {
//                // opposing lane is blocked
//                this.oppositeRoad.roadArray.get(1).setBlocked(true);
//            } else {
//                // opposing lane is not blocked
//                this.oppositeRoad.roadArray.get(1).setBlocked(false);
//            }
//        }

        // If Green light, get cars to go
        // If Red light, get cars to slow down and close in
        if (roadArray.get(index).getLights().isGreen()) {
            roadArray.get(index).goCars(Crossroad.TIME_INCREMENT);
        } else {
            roadArray.get(index).closeInCars(Crossroad.TIME_INCREMENT);
        }
    }

    private synchronized void updateRoadArray(int index, ArrayList<Road> roadArray, RoadSimulation rr) {
        // Check to see if any cars have 'exited' this road segment (its relative coordinate exceeded the road length)
        if (roadArray.get(index).carExit()) {
            this.totalCarPassed++;
            // Get the front most car from this road segment
            Car frontCar = roadArray.get(index).getFrontCar();
            // Remove this car object from this road segment
            roadArray.get(index).removeCar(frontCar);
            // Add this car object to the road segment infront if there is one available (when index > 0)
            if (index > 0) {
                // Set this car's y coordinate to 0
                frontCar.resetY();
                roadArray.get(index - 1).addCar(frontCar);
            } else {
                if (this.DIRECTION == Road.HORIZONTAL && rr != null) {
                    // Add to the left horizontal segment of the next crossroad
                    frontCar.resetY();
                    rr.roadArray.get(0).addCar(frontCar);
                }
            }
        }
    }

    private synchronized ArrayList<Road> populateRoadArray(ArrayList<Road> roadArray, int[] roadData, Light[] lightData, int direction) {
        // Create road objects based on roadData and lightData, then add to roadArray
        for (int i = roadData.length - 1; i >= 0; i--) {
            Road tempRoad = new Road(roadData[i], lightData[i], direction);     // Store road data and light data onto a temp Road variable
            roadArray.add(tempRoad);                                            // Add the temp Road into roadArray
        }

        // Pack all the road segments with cars to simulate a congestion
        for (int i = 0; i < roadArray.size(); i++) {
            roadArray.set(i, populate(roadArray.get(i)));
        }

        return roadArray;
        // Note: roadArray is ordered in a way that index 0 connotes right-most/down-most road segment
    }

    private synchronized Road populate(Road roadSegment) {
        // Populate cars all the way from the end to the beginning of road segment
        int segmentLength = roadSegment.getLength();
        for (int coordinate = segmentLength - POPULATE_GAP; coordinate > POPULATE_GAP; coordinate -= POPULATE_GAP) {
            Car tempCar = new Car(this.carCounter++, coordinate);
            roadSegment.populateCar(tempCar);
        }
        return roadSegment;
    }

    private synchronized void makeTrafficWorse(ArrayList<Road> roadArray) {
        int lastRoadIndex = roadArray.size() - 1;
        int lastCarIndex = roadArray.get(lastRoadIndex).carArray.size() - 1;

        // While the last car (of the last road segment) has given enough space for a new car, add a new car
        double lastCarCoordinate = roadArray.get(lastRoadIndex).carArray.get(lastCarIndex).getY();
        if (lastCarCoordinate > POPULATE_GAP) {
            Car tempCar = new Car(this.carCounter++, lastCarCoordinate + (POPULATE_GAP));
            roadArray.get(lastRoadIndex).addCar(tempCar);
        }
    }

    // For linking road segments within a crossroad
    public synchronized void setOppositeRoad(RoadSimulation road) {
        this.oppositeRoad = road;
    }

    // For linking crossroads
    public synchronized void setRightRoad(RoadSimulation road) {
        this.rightRoad = road;
    }

    public synchronized int getTotalCarPassed() {
        return this.totalCarPassed;
    }

}
