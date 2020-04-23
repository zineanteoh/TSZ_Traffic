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
public class Light {
    
    public final static int GREEN = 1;
    public final static int RED = 0;
    final static String[] STATE = {"red", "green"};
    private String state;
    double updateTime;
    double lastUpdatedTime;
    boolean interrupted;
    static final String ANSI_RESET = "\u001B[0m";
    static final String ANSI_RED = "\u001B[31m";
    static final String ANSI_GREEN = "\u001B[32m";

    public Light(int i, double updateTime, double lastUpdatedTime) {
        this.state = STATE[i];
        this.updateTime = updateTime;
        this.lastUpdatedTime = lastUpdatedTime;
        this.interrupted = false;
    }
    
    public void update(double time) { // status quo function
        time = Double.parseDouble(String.format("%.1f", time)); // Can be removed since time is rounded to 1dp at the beginning of each loop
        if ((time - this.lastUpdatedTime) % this.updateTime == 0) {
            if (this.interrupted) {
                this.interrupted = false;
            } else {
                this.changeState(time);
            }
        }
    }

    public void changeState(double time) {
        if (this.state.equals(STATE[0])) {
            this.state = STATE[1];
        } else {
            this.state = STATE[0];
        }
        this.lastUpdatedTime = time;
    }
    
    public void interrupt() {
        this.state = STATE[0];
        this.interrupted = true;
    }
    
    public boolean isGreen() {
        return this.state.equals("green");
    }

    public String printState() {
        if (this.isGreen()) {
            return ANSI_GREEN + this.state.substring(0, 1).toUpperCase() + ANSI_RESET;
        } else {
            return ANSI_RED + this.state.substring(0, 1).toUpperCase() + ANSI_RESET;
        }
    }
    
}
