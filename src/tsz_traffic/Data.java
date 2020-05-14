
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Data extends Thread {

    ArrayList<Thread> horizontalThreads;
    ArrayList<Thread> verticalThreads;
    ResourceLock lock;
    int crossroadCount;
    public String[] fileNames;

    public Data(ResourceLock lock, ArrayList<Thread> horizontalThreads, ArrayList<Thread> verticalThreads, int crossroadCount) {
        this.lock = lock;
        this.horizontalThreads = horizontalThreads;
        this.verticalThreads = verticalThreads;
        System.out.println("Creating Data Thread...");
        this.crossroadCount = crossroadCount;
        this.fileNames = new String[]{"horizontalPassed.dat", "horizontalOutflux.dat",
            "verticalPassed1.dat", "verticalPassed2.dat", "verticalPassed3.dat",
            "verticalOutflux1.dat", "verticalOutflux2.dat", "verticalOutflux3.dat"};

        // FileWriter
        try {
            for (int i = 0; i < fileNames.length; i++) {
                File myFile = new File(fileNames[i]);
                if (myFile.createNewFile()) {
                    System.out.println("File created: " + myFile.getName());
                } else {
                    System.out.println("File " + myFile.getName() + " already exists.");
                }
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
                // Clear all files
                FileWriter writer = null;
                for (int i = 0; i < this.fileNames.length; i++) {
                    writer = new FileWriter(fileNames[i], false);
                    writer.close();
                }

                for (double time = 0; time <= Main.simulationTime; time += Crossroad.TIME_INCREMENT) {
                    while (this.lock.flag != this.crossroadCount * 2 + 1) {
                        this.lock.wait();
                    }

                    time = Math.round(time * 10) / 10.0; // Round time to 1 dp
                    System.out.println("DATA Flag " + this.lock.flag + " running...");

                    // horizontalPassed.dat
                    writer = new FileWriter(fileNames[0], true);
                    writer.write(getHorizontalPassed() + " ");
                    writer.write("\n");
                    writer.close();

                    // horizontalOutflux.dat
                    writer = new FileWriter(fileNames[1], true);
                    writer.write(getHorizontalOutflux(this.crossroadCount - 1)+ " ");
                    writer.write("\n");
                    writer.close();

                    // verticalPassed1, 2, 3.dat
                    for (int i = 0; i < 3; i++) {
                        writer = new FileWriter(fileNames[i + 2], true);
                        writer.write(getVerticalPassed(i) + " ");
                        writer.write("\n");
                        writer.close();
                    }

                    // verticalOutflux1, 2, 3.dat
                    for (int i = 0; i < 3; i++) {
                        writer = new FileWriter(fileNames[i + 5], true);
                        writer.write(getVerticalOutflux(i) + " ");
                        writer.write("\n");
                        writer.close();
                    }

                    this.lock.flag = 1;
                    this.lock.notifyAll();
                }
            }
        } catch (Exception e) {
            System.out.printf("Exception Thread Data: %s", e);
        }
    }

    public synchronized int getHorizontalPassed() {
        int total = 0;
        for (int i = 0; i < this.crossroadCount; i++) {
            total += ((RoadSimulation)this.horizontalThreads.get(i)).getTotalCarPassed();
        }
        return total;
    }

    public synchronized int getVerticalPassed(int index) {
        return ((RoadSimulation) this.verticalThreads.get(index)).getTotalCarPassed();

    }

    public synchronized int getHorizontalInflux(int index) {//influx of segment with index 0 and outflux of segment with index 1 for the horizontal direction
        return ((RoadSimulation) this.horizontalThreads.get(index)).roadArray.get(0).getInflux(((RoadSimulation) this.horizontalThreads.get(index)).roadArray, 0);
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
