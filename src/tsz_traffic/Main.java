
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
    
    public Main() {
        
        // Simulation duration in seconds
        simulationTime = 2;
        
        
        /* Length of road of the crossroad at SanLiTun
                        ||
                        || <- 370ft 
                        ||
            ===700ft===[  ]===1700ft===
                        ||
                        || <- 520ft
                        ||                  
        Clarification: Two RoadSegments make up a Road   */
        int[] horizontalRoad = {700, 850};    // left to right
        int[] verticalRoad = {370, 520};       // up to down
        
        
        /* Light data (Takes in 3 inputs)
            1st 
                Starting Light State: Light.GREEN or Light.RED
            2nd 
                Duration of state in seconds
            3rd
                Time left until state changes in seconds    */
        Light[] horizontalLight = new Light[2];
        horizontalLight[0] = new Light(Light.GREEN, 4, 0);   // traffic light for end of left segment 
        horizontalLight[1] = new Light(Light.GREEN, 4, 2);   // traffic light for end of right segment
        Light[] verticalLight = new Light[2];
        verticalLight[0] = new Light(Light.RED, 4, 0);      // traffic light for end of top segment
        verticalLight[1] = new Light(Light.RED, 4, 2);      // traffic light for end of bottom segment
        
        
        // Create a crossroad
        Crossroad cr = new Crossroad(simulationTime);
        // Add a crossroad
        cr.addCrossroad(horizontalRoad, horizontalLight, verticalRoad, verticalLight);
        
        
        // Simulate the crossroad
        cr.runSimulation();
        
        
    }
}
