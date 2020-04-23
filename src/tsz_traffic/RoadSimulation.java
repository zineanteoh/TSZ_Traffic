/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tsz_traffic;

import java.util.ArrayList;

public class RoadSimulation extends Thread{
    
    private static int POPULATE_GAP = 15; // Populate a car every 15 feet 
    public int carCounter;
    final int DIRECTION;
    
    public RoadSimulation(int[] roadData, Light[] lightData, int direction) {
        this.DIRECTION = direction;
        this.carCounter = 1;
        ArrayList<Road> roadArray = new ArrayList<>();                                          // Create a road array
        roadArray = populateRoadArray(roadArray, roadData, lightData, this.DIRECTION);          // Populate the road array based on road and light data

    }
    
    public void run() {
        
    }
    
    private ArrayList<Road> populateRoadArray(ArrayList<Road> roadArray, int[] roadData, Light[] lightData, int direction) {
        // Create road objects based on roadData and lightData, then add to roadArray
        for (int i = roadData.length - 1; i >= 0; i--) {
            Road tempRoad = new Road(roadData[i], lightData[i], direction);     // Store road data and light data onto a temp Road variable
            roadArray.add(tempRoad);                                            // Add the temp Road into roadArray
        }
        
        // Pack all the road segments with cars to simulate a congestion
        for(int i = 0; i < roadArray.size(); i++) {
            roadArray.set(i, populate(roadArray.get(i), direction));
        }
        
        
        return roadArray;
        // Note: roadArray is ordered in a way that index 0 connotes right-most/down-most road segment
    }
    
    private Road populate(Road roadSegment, int direction) {
        // Populate cars all the way from the end to the beginning of road segment
        int segmentLength = roadSegment.getLength();
        for (int coordinate = segmentLength - POPULATE_GAP; coordinate > POPULATE_GAP; coordinate -= POPULATE_GAP) {
            Car tempCar = new Car(this.carCounter++, coordinate); 
            roadSegment.populateCar(tempCar); 
        }
        
        return roadSegment;
    }
}
