
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
    
    public Crossroad(int[] horizontalRoad, Light[] horizontalLight, int[] verticalRoad, Light[] verticalLight) {
        
        Thread horizontal = new RoadSimulation(horizontalRoad, horizontalLight, Road.HORIZONTAL);
        Thread vertical = new RoadSimulation(verticalRoad, verticalLight, Road.VERTICAL);
        
    }
    
    public void run(double endTime) {
        // Start simulation. 
        
    }
}
