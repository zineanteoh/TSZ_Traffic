
/* 
[Selena]
Tasks: 
    Retrieve data (line 33 & 35) either by
        1) Printing out
        2) Writing into a .csv file
    Need to create and write file (data.csv)
    Need to display the necessary data for data comparison
    Need to display the data in an organised manner (should definitely include time, influx, outflux)
 */
package tsz_traffic;

import java.util.ArrayList;

public class Data extends Thread {
    
    ArrayList<Thread> horizontalThreads;
    ArrayList<Thread> verticalThreads;
    ResourceLock lock;
    int crossroadCount;
    public Data(ResourceLock lock, ArrayList<Thread> horizontalThreads, ArrayList<Thread> verticalThreads, int crossroadCount) {
        this.lock = lock;
        this.horizontalThreads = horizontalThreads;
        this.verticalThreads = verticalThreads;
        System.out.println("Creating Data Thread...");
        this.crossroadCount = crossroadCount;
    }

    public void run() {
        try {
            synchronized (lock) {
                for (double time = 0; time <= Main.simulationTime; time += Crossroad.TIME_INCREMENT) {
                    while (this.lock.flag != this.crossroadCount * 2 + 1) {
                        this.lock.wait();
                    }
                    
                    time = Math.round(time * 10) / 10.0; // Round time to 1 dp
                    System.out.println("DATA Flag " + this.lock.flag + " running...");
//                    System.out.println("Running Data Thread" + "\tTime: " + time);
                    
                    // Retrieve data from the two road segments of horizontalThreads
                    // TODO...
                    // REPEAT for verticalThreads
                    // TODO...
                    
                    this.lock.flag = 1;
                    this.lock.notifyAll();
                }
            }
        } catch (Exception e) {
            System.out.printf("Exception Thread Data: %s", e.getMessage());
        }
    }
    
    public int getCount() {
        return this.crossroadCount;
    }
}
