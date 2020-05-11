
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

public class Data extends Thread {

    ResourceLock lock;
    Thread horizontalThread;
    Thread verticalThread;
    public Data(ResourceLock lock, Thread horizontalThread, Thread verticalThread) {
        this.lock = lock;
        System.out.println("Creating Data Thread...");
        try{
            File myFile = new File("trafficData.csv");
            if(myFile.createNewFile()){
                System.out.println("File created: " + myFile.getName());
            }
            else{
                System.out.println("File " + myFile.getName() + " already exists.");
            }
        }
        catch(IOException ex){
            System.out.println("An error has occured.");
        }
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
                    
                    //number of cars that have passed
                    int totalPassedCars;
                    totalPassedCars = ((RoadSimulation)this.horizontalThread).getFrontMostCar()-1 + ((RoadSimulation)this.verticalThread).getFrontMostCar()-1;
                    FileWriter writer = new FileWriter("trafficData.csv", true); //append everytime we call data thread
                    writer.write(totalPassedCars + "\t");

                    //influx and outflux for horizontal
                    writer.write(getHorizontalInflux() + "\t");
                    writer.write(getHorizontalOutflux() + "\t");
                    //influx and outflux for vertical
                    writer.write(getVerticalInflux() + "\t");
                    writer.write(getVerticalOutflux() + "\t");
                    //average time for a car to pass the two segments (horizontal/vertical)
                    //thinking about how to achieve it now......
                    writer.close();
                    
                    lock.flag = 0;
                    lock.notifyAll();
                }
            }
        } catch (Exception e) {
            System.out.printf("Exception Thread Data: %s", e.getMessage());
        }
    }
    
    public int getHorizontalInflux(){//influx of segment with index 0 and outflux of segment with index 1 for the horizontal direction
        return ((RoadSimulation)horizontalThread).roadArray.get(0).getInflux(((RoadSimulation)horizontalThread).roadArray, 0);
    }
    
    public int getVerticalInflux(){//influx of segment with index 0 and outflux of segment with index 1 for the vertical direction
        return ((RoadSimulation)verticalThread).roadArray.get(0).getInflux(((RoadSimulation)verticalThread).roadArray, 0);
    }
    
    public int getHorizontalOutflux(){//outflux of segment with index 0 aka rate of exiting the segment in the horizontal direction
        return ((RoadSimulation)horizontalThread).roadArray.get(0).getOutflux();
    }
    
    public int getVerticalOutflux(){//outflux of segment with index 0 aka rate of exiting the segment in the vertical direction
        return ((RoadSimulation)verticalThread).roadArray.get(0).getOutflux();
    }

}
