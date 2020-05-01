
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

public class Data extends Thread {

    ResourceLock lock;
    public Data(ResourceLock lock) {
        this.lock = lock;
        System.out.println("Creating Data Thread...");
    }

    public void run() {
        try {
            synchronized (lock) {
                for (double time = 0; time <= Main.simulationTime; time += Crossroad.TIME_INCREMENT) {
                    while (lock.flag != 2) {
                        lock.wait();
                    }
                    
                    time = Math.round(time * 10) / 10.0; // Round time to 1 dp
                    System.out.println("Running Data Thread" + "\tTime: " + time);

                    // Retrieve data from the two road segments of horizontalThread
                    // TODO...
                    // REPEAT for verticalThread
                    // TODO...
                    
                    lock.flag = 0;
                    lock.notifyAll();
                }
            }
        } catch (Exception e) {
            System.out.printf("Exception Thread Data: %s", e.getMessage());
        }
    }
}
