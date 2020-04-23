
/* Note:
    Data lists are ordered in a way that corresponds to the real world
    But elements in object arrays are ordered in the opposite way (index 0 connotes right-most / down-most object)
    Cars coordinate Y = 0 connotes the part of the road segment that is furthest from traffic light
        So when (car.y + car.length) > roadSegment.length, car is outside this road segment
    Car.Y is the butt of the car, while (Car.Y + Car.length) is the head of the car

   Tasks: 
    [Selena] Science behind traffic lights (How to sync our 4 traffic lights?)
    [Tommy] Implement randomization of car properties that happens within car class 
    [Tommy] Randomized initialization and utilization of car acceleration
    [Zi] Trigger a congestion at the crossroad. And then test our solution
    
    *** Our solution is essentially making traffic lights smarter ***
*/

package tsz_traffic;

import java.util.ArrayList;

public class Crossroad {
    
    public final static double TIME_INCREMENT = 0.1; // Duration of each loop in seconds
    public final static int SLEEP_TIME = 100; // Duration of Thread.sleep() at the end of each loop in milliseconds
    public Thread horizontalThread;
    public Thread verticalThread;
    public Thread dataThread;
    public static double endTime;
    public static ArrayList<Crossroad> crossroads;
    
    public Crossroad(double endTime) {
        // Create an empty ArrayList of Crossroads
        this.crossroads = new ArrayList<Crossroad>();
        
        // END_TIME stores how long simulation lasts in seconds
        this.endTime = endTime;
    }
    
    public Crossroad(int[] horizontalRoad, Light[] horizontalLight, int[] verticalRoad, Light[] verticalLight) {
        // Create three threads, one for horizontal direction, one for vertical, one for data
        horizontalThread = new RoadSimulation(horizontalRoad, horizontalLight, Road.HORIZONTAL);
        verticalThread = new RoadSimulation(verticalRoad, verticalLight, Road.VERTICAL);
        dataThread = new Data();
        
        // Link horizontal and vertical threads (by setting oppositeRoad to each other) 
        ((RoadSimulation) horizontalThread).setOppositeRoad(((RoadSimulation)verticalThread));
        ((RoadSimulation) verticalThread).setOppositeRoad(((RoadSimulation)horizontalThread));
    }
    
    public void addCrossroad(int[] horizontalRoad, Light[] horizontalLight, int[] verticalRoad, Light[] verticalLight) {
        this.crossroads.add(new Crossroad(horizontalRoad, horizontalLight, verticalRoad, verticalLight));
    }
    
    public void runSimulation() throws InterruptedException {
        // Start simulation. 
        for (Crossroad cr : crossroads) {
            cr.horizontalThread.start();
            try {Thread.sleep(10);} catch (InterruptedException e) {System.out.println("ThreadInterrupted");}
            cr.verticalThread.start();
            try {Thread.sleep(10);} catch (InterruptedException e) {System.out.println("ThreadInterrupted");}
            cr.dataThread.start();
            try {Thread.sleep(10);} catch (InterruptedException e) {System.out.println("ThreadInterrupted");}
        }
    }
}
