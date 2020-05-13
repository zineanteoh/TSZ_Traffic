
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

        // FileWriter
        try {
            File myFile = new File("trafficData.csv");
            if (myFile.createNewFile()) {
                System.out.println("File created: " + myFile.getName());
            } else {
                System.out.println("File " + myFile.getName() + " already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error has occured.");
        }
    }

    public void run() {
        try {
            synchronized (lock) {
                // Clear trafficData.csv file
                FileWriter preWriter = new FileWriter("trafficData.csv", false);
                preWriter.close();

                for (double time = 0; time <= Main.simulationTime; time += Crossroad.TIME_INCREMENT) {
                    while (this.lock.flag != this.crossroadCount * 2 + 1) {
                        this.lock.wait();
                    }

                    time = Math.round(time * 10) / 10.0; // Round time to 1 dp
                    System.out.println("DATA Flag " + this.lock.flag + " running...");

                    FileWriter writer = new FileWriter("trafficData.csv", true); //append everytime we call data thread
                    writer.write(getTotalPassedCars() + "\t");
                    writer.write(getHorizontalInflux(0) + "\t");
                    writer.write(getHorizontalOutflux(0) + "\t");
                    //influx and outflux for vertical
                    writer.write(getVerticalInflux(0) + "\t");
                    writer.write(getVerticalOutflux(0) + "\t");
                    //average time for a car to pass the two segments (horizontal/vertical)
                    //thinking about how to achieve it now......
                    writer.close();

                    this.lock.flag = 1;
                    this.lock.notifyAll();
                }
            }
        } catch (Exception e) {
            System.out.printf("Exception Thread Data: %s", e.getMessage());
        }
    }
    
    public synchronized int getTotalPassedCars() {
        return ((RoadSimulation)this.horizontalThreads.get(this.crossroadCount-1)).getFrontMostCar()-1 + ((RoadSimulation)this.verticalThreads.get(this.crossroadCount-1)).getFrontMostCar()-1;
    }

    public synchronized int getHorizontalInflux(int index) {//influx of segment with index 0 and outflux of segment with index 1 for the horizontal direction
        return ((RoadSimulation)this.horizontalThreads.get(index)).roadArray.get(0).getInflux(((RoadSimulation) this.horizontalThreads.get(index)).roadArray, 0);
    }

    public synchronized int getVerticalInflux(int index) {//influx of segment with index 0 and outflux of segment with index 1 for the vertical direction
        return ((RoadSimulation) this.verticalThreads.get(index)).roadArray.get(0).getInflux(((RoadSimulation) this.verticalThreads.get(index)).roadArray, 0);
    }

    public synchronized int getHorizontalOutflux(int index) {//outflux of segment with index 0 aka rate of exiting the segment in the horizontal direction
        return ((RoadSimulation) this.horizontalThreads.get(index)).roadArray.get(0).getOutflux();
    }

    public synchronized int getVerticalOutflux(int index) {//outflux of segment with index 0 aka rate of exiting the segment in the vertical direction
        return ((RoadSimulation) this.verticalThreads.get(index)).roadArray.get(0).getOutflux();
    }

}
