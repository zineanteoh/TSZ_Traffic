/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tsz_traffic;

/**
 *
 * @author Zi Nean Teoh
 */
public class Data extends Thread {
    public Data() {
//        System.out.println("Creating Data Thread...");
    }
    
    public void run() {
        for (double time = 0; time < TempMain.simulationTime ; time += Crossroad.TIME_INCREMENT) {
            time = Math.round(time * 10) / 10.0; // Round time to 1 dp
//            System.out.println("Running Data Thread" + "\tTime: " + time);
            
            
            
            
            
            
            
            // Make this thread go to sleep to allow other threads to run
            try {Thread.sleep(Crossroad.SLEEP_TIME);} catch (InterruptedException e) {System.out.println("ThreadInterrupted");}
        }
    }
}
