
/* 
[Selena]
Tasks: 
    Retrieve data (line 26 & 29) either by
        1) Printing out
        2) Writing into a .csv file
    Need to create and write file (data.csv)
    Need to display the necessary data for data comparison
    Need to display the data in an organised manner (should definitely include time, influx, outflux)
*/

package tsz_traffic;

public class Data extends Thread {
    public Data() {
        System.out.println("Creating Data Thread...");
    }
    
    public void run() {
        for (double time = 0; time < Main.simulationTime ; time += Crossroad.TIME_INCREMENT) {
            time = Math.round(time * 10) / 10.0; // Round time to 1 dp
            System.out.println("Running Data Thread" + "\tTime: " + time);
            
            // Retrieve data from the two road segments of horizontalThread
            // TODO...
            
            // REPEAT for verticalThread
            // TODO...
            
            
            // Make this thread go to sleep to allow other threads to run
            try {Thread.sleep(Crossroad.SLEEP_TIME);} catch (InterruptedException e) {System.out.println("ThreadInterrupted");}
        }
    }
}
