
/*
[Tommy]
Tasks: 
    Implement randomization of car properties that happens within car class 
    Randomized initialization and utilization of car acceleration
 */
package tsz_traffic;

import java.util.ArrayList;

public class Car {

    final double LENGTH;
    public double topSpeed;
    public double currentSpeed;
    public double accelerator;
    public double decelerator;
    private double y;
    Car frontCar;
    final static double GAP_SPACE = 2;
    final int carNumber;

    public Car(int index, double y) {
        // randomize all other properties of car (length, topSpeed, accelerator, decelerator)
        this(index, y, 
                13 + (int) (Math.random() * (5)),           // length
                30 + (int) (Math.random() * (15)),          // top speed
                (75 + (int) (Math.random() * 15)) / 100.0);   // accelerator
        // car length ranges from 13 to 17 ft
        // car topSpeed ranges from 30 to 44 ft/sec
        // car accelerator ranges from 0.75 to 0.89 ft/sec^2
        // car decelerator is dependent on accelerator
    }

    public Car(int index, double y, double length, double topSpeed, double accelerator) {
        this.carNumber = index;
        this.y = y;
        this.LENGTH = length;
        this.topSpeed = topSpeed; // in ft per second
        this.currentSpeed = topSpeed;
        this.frontCar = null;
        this.accelerator = accelerator;
        this.decelerator = Double.parseDouble(String.format("%.2f",accelerator - 0.2));
    }

    public synchronized void go(double increment, ArrayList<Car> cars, boolean blocked, Road segment) { // increment is in seconds (e.g. 0.1 seconds)
        // increment by accelerator until it reaches top speed    
        if (this.equals(cars.get(0))) {
            // this car is at the front. checkTime for blockage caused by opposing lane
            if (blocked) {
                // Stop.
                this.currentSpeed = 0;
//                this.decelerate(increment, segment);
            } else {
                this.accelerate(increment);
            }
        } else {
            // this car has a car in front... let's check for possible collision
            this.frontCar = cars.get(cars.indexOf(this) - 1);
            // if front car has stopped, this car stops too
            if (this.frontCar.currentSpeed == 0 || (this.frontCar.y - (this.y + this.LENGTH) < GAP_SPACE)) {
                this.currentSpeed = 0;
                return;
            }
            if (noPossibleCollision(increment)) {
                // GOOD TO GO
                this.accelerate(increment);
            } else {
                // possible collision... close in!
                this.closeIn(increment, cars, segment);
            }
        }
    }

    public synchronized void closeIn(double increment, ArrayList<Car> cars, Road segment) { // this is called when traffic light is red
        // get coordinate of front object
        double frontObjectYCoordinate;
        if (this.equals(cars.get(0))) { 
            // if this car is the first car in the segment
            frontObjectYCoordinate = segment.ROAD_LENGTH; // front 'object' is simply the end of segment
        } else { 
            // if this car has some car in front
            this.frontCar = cars.get(cars.indexOf(this) - 1);
            frontObjectYCoordinate = this.frontCar.getY() + this.frontCar.currentSpeed * increment; // front 'object' is the car in front
        }

        if (frontObjectYCoordinate - (this.y + this.LENGTH + this.currentSpeed * increment) <= GAP_SPACE) {
            // if this car (travelling at current topSpeed) will pass the object in front + gap,
            // stop car at exactly GAP_SPACE feet away from the object 
            this.y = frontObjectYCoordinate - GAP_SPACE - this.LENGTH;
        } else {
            this.decelerate(increment, segment);
        }
    }

    public synchronized void accelerate(double increment) {
        if (this.currentSpeed + this.accelerator < this.topSpeed) {
            // speeeeed
            this.currentSpeed += this.accelerator;
            this.y += this.currentSpeed * increment;
        } else {
            this.currentSpeed = this.topSpeed;
            this.y += this.currentSpeed * increment;
        }
    }

    public synchronized void decelerate(double increment, Road segment) {
        if (this.frontCar == null) {
            // if frontCar == null, this car is blocked by the crossroad. need to slow down / stop
            if (this.y + this.currentSpeed * increment + GAP_SPACE < segment.ROAD_LENGTH && this.currentSpeed - this.decelerator > 0) {
                // if this car is still a little far from crossroad && this car can decelerate
                this.currentSpeed -= this.decelerator;
                this.y += this.currentSpeed * increment;
            } else {
                // this car is pretty damn close to the crossroad. stop it at this instant! 
                this.currentSpeed = 0;
            }
        } else {
            // Goal: check front car speed and distance to decelerate accordingly
            if (this.frontCar.currentSpeed == 0 || (this.frontCar.y - (this.y + this.LENGTH) < GAP_SPACE)) {
                // front car has stopped within distance. stop this car too
                this.currentSpeed = 0;
            } else {
                // Goal: slow down this car based on front car speed and distance
                if (this.frontCar.y + this.frontCar.currentSpeed * increment - (this.y + this.LENGTH + this.currentSpeed * increment) <= GAP_SPACE) {
                    // a collision might happen. decelerate to the speed at which collision can be prevented
                    double idealSpeed = this.frontCar.y + this.frontCar.currentSpeed * increment - (GAP_SPACE + this.y + this.LENGTH);
                    idealSpeed /= increment;
                    if (idealSpeed > 0) {
                        this.currentSpeed = idealSpeed;
                        this.y += this.currentSpeed * increment;
                    } else {
                        this.currentSpeed = 0;
                    }
                } else {
                    // front car and this car is within safe distance, decelerate normally
                    this.currentSpeed -= this.decelerator;
                    this.y += this.currentSpeed * increment;
                }
            }
        }
    }

    public synchronized boolean noPossibleCollision(double increment) {
        double thisCarInTheFuture = this.y + this.LENGTH + this.currentSpeed * increment;
        double frontCarInTheFuture = this.frontCar.y + this.frontCar.currentSpeed * increment;
        if (frontCarInTheFuture - thisCarInTheFuture > 2) {
            return true;
        } else {
            return false;
        }
    }

    public synchronized double getY() {
        return this.y;
    }

    public synchronized void resetY() {
        this.y = 0;
    }

    public synchronized double getLength() {
        return this.LENGTH;
    }

    public synchronized String getCarNumber() {
        return Integer.toString(this.carNumber);
    }
}
