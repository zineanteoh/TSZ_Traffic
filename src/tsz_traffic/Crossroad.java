
/* Note:
    Data lists are ordered in a way that corresponds to the real world
    But elements in object arrays are ordered in the opposite way (index 0 connotes right-most / down-most object)
    Cars coordinate Y = 0 connotes the part of the road segment that is furthest from traffic light
        So when (car.y + car.length) > roadSegment.length, car is outside this road segment

    [Tommy] Implement randomization of car properties that happens within car class 
    [Selena] Science behind traffic lights (How to sync our 4 traffic lights?)
    [Tommy] Randomized initialization and utilization of car acceleration

    
    *** Our solution is essentially making traffic lights smarter ***
*/

package tsz_traffic;

import java.util.ArrayList;

public class Crossroad {
    
    private static int POPULATE_GAP = 15; // Populate a car every 15 feet 
    public int horizontalCarCounter;
    public int verticalCarCounter;
    
    public Crossroad(int[] horizontalRoad, Light[] horizontalLight, int[] verticalRoad, Light[] verticalLight) {
        this.horizontalCarCounter = this.verticalCarCounter = 1;            // Initialize car counters for both directions
        
        // Horizontal direction Road
        ArrayList<Road> horizontalRoadArray = new ArrayList<>();                                                            // Create a horizontal road array
        horizontalRoadArray = populateRoadArray(horizontalRoadArray, horizontalRoad, horizontalLight, Road.HORIZONTAL);     // Populate the road array based on road and light data
        
        
        
        // Veritcal direction Road
        ArrayList<Road> verticalRoadArray = new ArrayList<>();                                                              // Create a vertical road array
        verticalRoadArray = populateRoadArray(verticalRoadArray, verticalRoad, verticalLight, Road.VERTICAL);               // Populate the road array based on road and light data
        
    }
    
    public void run(double endTime) {
        // Start simulation
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
            Car tempCar;
            if (direction == Road.HORIZONTAL) {
                tempCar = new Car(this.horizontalCarCounter++, coordinate); 
            } else {
                tempCar = new Car(this.verticalCarCounter++, coordinate); 
            }
            roadSegment.populateCar(tempCar); 
        }
        
        return roadSegment;
    }
}
