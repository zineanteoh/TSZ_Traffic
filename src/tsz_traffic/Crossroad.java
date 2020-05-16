
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
    public ArrayList<Thread> horizontalThreads;
    public ArrayList<Thread> verticalThreads;
    public Thread dataThread;
    public static ResourceLock lock;
    public static double endTime;
    public double roadWidth;
    public int crossroadCount = 0;
    public static boolean interrupt;

    public Crossroad(double endTime, double roadWidth, boolean interrupt) {
        // END_TIME stores how long simulation lasts in seconds
        this.endTime = endTime;

        // Stores the width of each road, in feet
        this.roadWidth = roadWidth;
        
        // Interrupt (true) or status quo (false)
        this.interrupt = interrupt;

        // Stores number of crossroads
        this.crossroadCount = 0;

        // ResourceLock for synchronizing threads
        this.lock = new ResourceLock();

        // Thread arrays
        this.horizontalThreads = new ArrayList<Thread>();
        this.verticalThreads = new ArrayList<Thread>();

    }

    public void addCrossroad(int[] horizontalRoad, Light[] horizontalLight, int[] verticalRoad, Light[] verticalLight) {
        // Create three threads, one for horizontal direction, one for vertical, one for data

        this.horizontalThreads.add(new RoadSimulation(horizontalRoad, horizontalLight, Road.HORIZONTAL, lock, this.roadWidth, crossroadCount + 1));
        this.verticalThreads.add(new RoadSimulation(verticalRoad, verticalLight, Road.VERTICAL, lock, this.roadWidth, crossroadCount + 1));

        // Link horizontal and vertical threads (by setting oppositeRoad to each other) 
        ((RoadSimulation) this.horizontalThreads.get(this.crossroadCount)).setOppositeRoad(((RoadSimulation) verticalThreads.get(this.crossroadCount)));
        ((RoadSimulation) this.verticalThreads.get(this.crossroadCount)).setOppositeRoad(((RoadSimulation) horizontalThreads.get(this.crossroadCount)));

        this.crossroadCount++;
    }

    public void runSimulation() throws InterruptedException {
        // Link the horizontal crossroads
        for (int i = 0; i < this.crossroadCount - 1; i++) {
            ((RoadSimulation) this.horizontalThreads.get(i)).setRightRoad((RoadSimulation) this.horizontalThreads.get(i + 1));
        }

        // Create a data thread
        this.dataThread = new Data(this.lock, this.horizontalThreads, this.verticalThreads, this.crossroadCount);

        // Start simulation. 
        for (int i = 0; i < this.crossroadCount; i++) {
            this.horizontalThreads.get(i).start();
        }
        for (int i = 0; i < this.crossroadCount; i++) {
            this.verticalThreads.get(i).start();
        }
        this.dataThread.start();
    }

}
