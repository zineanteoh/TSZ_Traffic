
package tsz_traffic;

import java.util.ArrayList;

public class RoadSimulation extends Thread{
    
    private static int POPULATE_GAP = 15; // Populate a car every 15 feet 
    public int carCounter;
    final int DIRECTION;
    public RoadSimulation oppositeRoad;
    
    public RoadSimulation(int[] roadData, Light[] lightData, int direction) {
        this.DIRECTION = direction;
        this.carCounter = 1;
        ArrayList<Road> roadArray = new ArrayList<>();                                          // Create a road array
        roadArray = populateRoadArray(roadArray, roadData, lightData, this.DIRECTION);          // Populate the road array based on road and light data
        this.oppositeRoad = null;
    }
    
    public void setOppositeRoad(RoadSimulation road) {
        this.oppositeRoad = road;
    }
    
    public void run() {
        // Run simulation
        for (double time = 0; time < TempMain.simulationTime ; time += Crossroad.TIME_INCREMENT) {
            time = Math.round(time * 10) / 10.0; // round time to 1 dp
            // System.out.println("Running Thread " + (DIRECTION + 1) + "\tTime: " + time);
            
            
            
            
            
            
            // Make this thread go to sleep to allow other threads to run
            try {Thread.sleep(Crossroad.SLEEP_TIME);} catch (InterruptedException e) {System.out.println("ThreadInterrupted");}
        }
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
