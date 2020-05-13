
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
            File myFile = new File("horizontalPassed.dat");
            if (myFile.createNewFile()) {
                System.out.println("File created: " + myFile.getName());
            } else {
                System.out.println("File " + myFile.getName() + " already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error has occured.");
        }
        
        try {
            File myFile = new File("verticalPassed1.dat");
            if (myFile.createNewFile()) {
                System.out.println("File created: " + myFile.getName());
            } else {
                System.out.println("File " + myFile.getName() + " already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error has occured.");
        }
        
        try {
            File myFile = new File("verticalPassed2.dat");
            if (myFile.createNewFile()) {
                System.out.println("File created: " + myFile.getName());
            } else {
                System.out.println("File " + myFile.getName() + " already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error has occured.");
        }
        
        try {
            File myFile = new File("verticalPassed3.dat");
            if (myFile.createNewFile()) {
                System.out.println("File created: " + myFile.getName());
            } else {
                System.out.println("File " + myFile.getName() + " already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error has occured.");
        }
        
        try {
            File myFile = new File("horizontalOutflux.dat");
            if (myFile.createNewFile()) {
                System.out.println("File created: " + myFile.getName());
            } else {
                System.out.println("File " + myFile.getName() + " already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error has occured.");
        }
        
        try {
            File myFile = new File("verticalOutflux1.dat");
            if (myFile.createNewFile()) {
                System.out.println("File created: " + myFile.getName());
            } else {
                System.out.println("File " + myFile.getName() + " already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error has occured.");
        }
        
        try {
            File myFile = new File("verticalOutflux2.dat");
            if (myFile.createNewFile()) {
                System.out.println("File created: " + myFile.getName());
            } else {
                System.out.println("File " + myFile.getName() + " already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error has occured.");
        }
        
        try {
            File myFile = new File("verticalOutflux3.dat");
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
                // Clear file
                FileWriter preWriter = new FileWriter("horizontalOutflux.dat", false);
                preWriter.close();

                for (double time = 0; time <= Main.simulationTime; time += Crossroad.TIME_INCREMENT) {
                    while (this.lock.flag != this.crossroadCount * 2 + 1) {
                        this.lock.wait();
                    }

                    time = Math.round(time * 10) / 10.0; // Round time to 1 dp
                    System.out.println("DATA Flag " + this.lock.flag + " running...");

<<<<<<< Updated upstream
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
=======
                    FileWriter writer1 = new FileWriter("horizontalPassed.dat", true); //append everytime we call data thread
                    writer1.write(getHorizontalPassed() + " ");
                    writer1.write("\n");
                    writer1.close();
                    
                    FileWriter writer2 = new FileWriter("verticalPassed1.dat", true); //append everytime we call data thread
                    writer2.write(getVerticalPassed(0) + " ");
                    writer2.write("\n");
                    writer2.close();
                    
                    FileWriter writer3 = new FileWriter("verticalPassed2.dat", true); //append everytime we call data thread
                    writer3.write(getVerticalPassed(1) + " ");
                    writer3.write("\n");
                    writer3.close();
                    
                    FileWriter writer4 = new FileWriter("verticalPassed3.dat", true); //append everytime we call data thread
                    writer4.write(getVerticalPassed(2) + " ");
                    writer4.write("\n");
                    writer4.close();
                    
                    FileWriter writer5 = new FileWriter("horizontalOutflux.dat", true); //append everytime we call data thread
                    writer5.write(getHorizontalOutflux(0) + " ");
                    writer5.write("\n");
                    writer5.close();
                    
                    FileWriter writer6 = new FileWriter("verticalOutflux1.dat", true); //append everytime we call data thread
                    writer6.write(getVerticalOutflux(0) + " ");
                    writer6.write("\n");
                    writer6.close();
                    
                    FileWriter writer7 = new FileWriter("verticalOutflux2.dat", true); //append everytime we call data thread
                    writer7.write(getVerticalOutflux(1) + " ");
                    writer7.write("\n");
                    writer7.close();
                    
                    FileWriter writer8 = new FileWriter("verticalOutflux3.dat", true); //append everytime we call data thread
                    writer8.write(getVerticalOutflux(2) + " ");
                    writer8.write("\n");
                    writer8.close();
>>>>>>> Stashed changes

                    this.lock.flag = 1;
                    this.lock.notifyAll();
                }
            }
        } catch (Exception e) {
            System.out.printf("Exception Thread Data: %s", e);
        }
    }
    
    public synchronized int getHorizontalPassed() {
        return ((RoadSimulation)this.horizontalThreads.get(this.crossroadCount-1)).getFrontMostCar()-1;
    }
    
    public synchronized int getVerticalPassed(int index) {
        return ((RoadSimulation)this.verticalThreads.get(index)).getFrontMostCar()-1;
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
