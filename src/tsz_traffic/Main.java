
/* Simulation premises:
    Cars do not turn at crossroad
    Cars are constantly being populated on: 
    - The Left side of horizontal road 
    - Top side of vertical road 
    ...
    ...
 */
package tsz_traffic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Main {
    // Simulation duration, in seconds
    public static double simulationTime = 240;
    // Implement Solution or not (false = status quo)
    public static boolean implementSolution = false;
    // Number of trials
    public static int trials = 25;
     // Time when interrupt is allowed to kick in
    public static double checkTime = 0.97;
    // Constant for influx outflux comparison
    public static double densityCheckConstant = 1.10;

    public static void main(String[] args) throws InterruptedException, IOException {
        // get Files
        String[] totalFiles = new String[]{"totalHorizontalPassed.dat", "totalHorizontalOutflux.dat",
            "totalVerticalPassed1.dat", "totalVerticalPassed2.dat", "totalVerticalPassed3.dat",
            "totalVerticalOutflux1.dat", "totalVerticalOutflux2.dat", "totalVerticalOutflux3.dat"};

        String[] avgFiles = new String[]{"avgHorizontalPassed.dat", "avgHorizontalOutflux.dat",
            "avgVerticalPassed1.dat", "avgVerticalPassed2.dat", "avgVerticalPassed3.dat",
            "avgVerticalOutflux1.dat", "avgVerticalOutflux2.dat", "avgVerticalOutflux3.dat"};

        String[] miniFiles = new String[]{"horizontalPassed.dat", "horizontalOutflux.dat",
            "verticalPassed1.dat", "verticalPassed2.dat", "verticalPassed3.dat",
            "verticalOutflux1.dat", "verticalOutflux2.dat", "verticalOutflux3.dat"};

        try {
            for (int i = 0; i < totalFiles.length; i++) {
                File myFile = new File(totalFiles[i]);
                if (myFile.createNewFile()) {
                    System.out.println("File created: " + myFile.getName());
                } else {
                    System.out.println("File " + myFile.getName() + " already exists.");
                }
            }
        } catch (IOException e) {
            System.out.println("An error as occured. ");
        }

        try {
            // Clear all avg files
            FileWriter writer = null;
            for (int i = 0; i < avgFiles.length; i++) {
                writer = new FileWriter(avgFiles[i], false);
                writer.close();
            }
            // Clear all totalFiles
            writer = null;
            for (int i = 0; i < totalFiles.length; i++) {
                writer = new FileWriter(totalFiles[i], false);
                writer.close();
            }
        } catch (Exception e) {
            System.out.println("Error writing files.");
        }

        // start collecting trial data
        for (int i = 0; i < trials; i++) {
            run();
            // read the mini files, add to avg file
            for (int j = 0; j < miniFiles.length; j++) {
                try {
                    synchronized (Crossroad.lock) {
                        while (Crossroad.lock.flag != -1) {
                            Crossroad.lock.wait();
                        }
                        if (i == 0) {
                            // total file is empty.
                            // read miniFile 
                            BufferedReader readMini = new BufferedReader(new FileReader(miniFiles[j]));
                            String lineMini;
                            OutputStreamWriter writeTotal = new OutputStreamWriter(new FileOutputStream(totalFiles[j]));
                            while ((lineMini = readMini.readLine()) != null) {
                                // write to avgFile
//                                System.out.println("Main Reading " + lineMini);
                                writeTotal.write(lineMini);
                                writeTotal.write("\n");
                            }
                            readMini.close();
                            writeTotal.close();
                            continue;
                        }

                        // total file is not empty
                        // read mini file
                        BufferedReader readMini = new BufferedReader(new FileReader(miniFiles[j]));
                        BufferedReader readTotal = new BufferedReader(new FileReader(totalFiles[j]));
                        String lineMini, lineTotal;
                        StringBuilder stringBuilder = new StringBuilder();
                        while (((lineMini = readMini.readLine()) != null) && ((lineTotal = readTotal.readLine()) != null)) {
                            int ans = Integer.parseInt(lineTotal.trim()) + Integer.parseInt(lineMini.trim());
//                            System.out.println("This is ans " + ans );
                            stringBuilder.append(ans);
                            stringBuilder.append('\n');
                        }
                        readMini.close();
                        readTotal.close();

                        // write to total files
                        BufferedWriter writeTotal = new BufferedWriter(new FileWriter(totalFiles[j]));
                        writeTotal.write(stringBuilder.toString());
                        writeTotal.close();

                        Crossroad.lock.notifyAll();
                    }
                } catch (Exception e) {
                    System.out.println("Problem reading file" + e);
                }
            }
        }

        // Compute average for avgFiles
        for (int i = 0; i < totalFiles.length; i++) {
            try {
                synchronized (Crossroad.lock) {
                    while (Crossroad.lock.flag != -1) {
                        Crossroad.lock.wait();
                    }
                    // Read avg files, compute avg, store in inputBuffer
                    BufferedReader readTotal = new BufferedReader(new FileReader(totalFiles[i]));
                    BufferedWriter writeAvg = new BufferedWriter(new FileWriter(avgFiles[i]));
                    String lineTotal;
                    while ((lineTotal = readTotal.readLine()) != null) {
                        // remove trailing spaces
                        String ans = Double.toString(Double.parseDouble(lineTotal.trim()) / trials);
                        writeAvg.write(ans);
                        writeAvg.newLine();
                    }
                    readTotal.close();
                    writeAvg.close();

                    Crossroad.lock.notifyAll();
                }
            } catch (Exception e) {
                System.out.println("Problem writing avg files." + e);
            }
        }

    }

    public static void run() throws InterruptedException {
        /* Simulating crossroads at SanLiTun: 
                        ||                           ||                           ||
                        ||     <-----370ft----->     ||     <-----520ft----->     ||
                        ||                           ||                           ||
            ===700ft===[  ]===850ft=== | ===850ft===[  ]===700ft=== | ===700ft===[  ]===600ft===
                        ||                           ||                           ||
                        ||     <-----520ft----->     ||     <-----520ft----->     ||
                        ||                           ||                           ||  
            <--------CrossRoad1-------> <---------CrossRoad2-------> <--------CrossRoad3-------->
        Clarification: Two RoadSegments make up a Road   */
        double roadWidth = 20; // The middle [  ] is a 20ftx20ft crossroad
        int[] horizontalRoad = {700, 850};    // left to right
        int[] verticalRoad = {370, 520};       // up to down

        // Create an object that controls all the crossroads
        Crossroad cr = new Crossroad(simulationTime, roadWidth, implementSolution);

        /* Light data (Takes in 3 inputs)
            1st 
                Starting Light State: Light.GREEN or Light.RED
            2nd 
                Duration of state, in seconds
            3rd
                Time left until state changes, in seconds    */
        Light[] horizontalLight1 = new Light[2];
        Light[] verticalLight1 = new Light[2];

        // Add crossroad #1
        horizontalLight1[0] = new Light(Light.RED, 30, 0);   // traffic light for end of left segment 
        horizontalLight1[1] = new Light(Light.RED, 30, 0);   // traffic light for end of right segment
        verticalLight1[0] = new Light(Light.GREEN, 30, 0);      // traffic light for end of top segment
        verticalLight1[1] = new Light(Light.GREEN, 30, 0);      // traffic light for end of bottom segment
        cr.addCrossroad(horizontalRoad, horizontalLight1, verticalRoad, verticalLight1);

        // Add crossroad #2 (to the right of cr#1)
        Light[] horizontalLight2 = new Light[2];
        Light[] verticalLight2 = new Light[2];
        int[] horizontalRoad2 = new int[]{850, 700};
        horizontalLight2[0] = new Light(Light.RED, 30, 0);   // traffic light for end of left segment 
        horizontalLight2[1] = new Light(Light.RED, 30, 0);   // traffic light for end of right segment
        verticalLight2[0] = new Light(Light.GREEN, 30, 0);      // traffic light for end of top segment
        verticalLight2[1] = new Light(Light.GREEN, 30, 0);      // traffic light for end of bottom segment
        cr.addCrossroad(horizontalRoad2, horizontalLight2, verticalRoad, verticalLight2);

        // Add crossroad #3 (to the right of cr#2)
        Light[] horizontalLight3 = new Light[2];
        Light[] verticalLight3 = new Light[2];
        int[] horizontalRoad3 = new int[]{700, 600};
        horizontalLight3[0] = new Light(Light.RED, 30, 0);   // traffic light for end of left segment 
        horizontalLight3[1] = new Light(Light.RED, 30, 0);   // traffic light for end of right segment
        verticalLight3[0] = new Light(Light.GREEN, 30, 0);      // traffic light for end of top segment
        verticalLight3[1] = new Light(Light.GREEN, 30, 0);      // traffic light for end of bottom segment
        cr.addCrossroad(horizontalRoad3, horizontalLight3, verticalRoad, verticalLight3);

        // Simulate the crossroad
        cr.runSimulation();
    }
}
