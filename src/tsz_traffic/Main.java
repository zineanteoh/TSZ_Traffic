
/* Simulation premises:
    Cars do not turn at crossroad
    Cars are constantly being populated on: 
    - The Left side of horizontal road 
    - Top side of vertical road 
    ...
    ...
*/

package tsz_traffic;

public class Main {
    
    public static double simulationTime;
    
    public static void main(String[] args) throws InterruptedException {
        
        // Simulation duration, in seconds
        simulationTime = 500;
        // Implement Solution or not (false = status quo)
        boolean implementSolution = false;
        

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
        int[] horizontalRoad = {700, 850};    // left to right
        int[] verticalRoad = {370, 520};       // up to down
        double roadWidth = 20; // The middle [  ] is a 20ftx20ft crossroad
        
        
        /* Light data (Takes in 3 inputs)
            1st 
                Starting Light State: Light.GREEN or Light.RED
            2nd 
                Duration of state, in seconds
            3rd
                Time left until state changes, in seconds    */
        Light[] horizontalLight = new Light[2];
        horizontalLight[0] = new Light(Light.GREEN, 30, 0);   // traffic light for end of left segment 
        horizontalLight[1] = new Light(Light.GREEN, 30, 15);   // traffic light for end of right segment
        Light[] verticalLight = new Light[2];
        verticalLight[0] = new Light(Light.GREEN, 30, 0);      // traffic light for end of top segment
        verticalLight[1] = new Light(Light.GREEN, 30, 15);      // traffic light for end of bottom segment
        
        // Create an object that controls all the crossroads
        Crossroad cr = new Crossroad(simulationTime, roadWidth, implementSolution);
        
        // Add crossroad #1
        cr.addCrossroad(horizontalRoad, horizontalLight, verticalRoad, verticalLight);
        // Add crossroad #2 (to the right of cr#1)
        horizontalRoad = new int[]{850, 700};
        cr.addCrossroad(horizontalRoad, horizontalLight, verticalRoad, verticalLight);
        // Add crossroad #3 (to the right of cr#2)
        horizontalRoad = new int[]{700, 600};
        cr.addCrossroad(horizontalRoad, horizontalLight, verticalRoad, verticalLight);
        
        // Simulate the crossroad
        cr.runSimulation();
    }
}
