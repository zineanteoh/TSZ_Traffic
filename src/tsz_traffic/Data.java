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
            
            // Retrieve data from the two road segments of verticalThread
            
            
            
            // Make this thread go to sleep to allow other threads to run
            try {Thread.sleep(Crossroad.SLEEP_TIME);} catch (InterruptedException e) {System.out.println("ThreadInterrupted");}
        }
    }
}
