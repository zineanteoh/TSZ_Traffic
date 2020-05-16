
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
    private double y;
    Car frontCar;
    final static double GAP_SPACE = 2;
    final int carNumber;

    public Car(int index, double y) {
        // randomize all other properties of car (length, topSpeed, accelerator)
        this(index, y, 13 + (int) (Math.random() * (5)), 30 + (int) (Math.random() * (15)), (65 + (int) (Math.random() * 15)) / 100);
        // car length ranges from 13 to (13+4=17) feet
        // car topSpeed ranges from 30 to (30+14=44) feet
    }

    public Car(int index, double y, double length, double topSpeed, double accelerator) {
        this.carNumber = index;
        this.y = y;
        this.LENGTH = length;
        this.topSpeed = topSpeed; // in ft per second
        this.currentSpeed = topSpeed / 2;
        this.frontCar = null;
        this.accelerator = accelerator;
    }

    public synchronized void go(double increment, ArrayList<Car> cars, boolean blocked, Road segment) { // increment is in seconds (e.g. 0.1 seconds)
        // increment by accelerator until it reaches top speed    
        if (this.equals(cars.get(0))) {
            // this car is at the front. checkTime for blockage caused by opposing lane
            if (blocked) {
                if (this.y + this.LENGTH + this.currentSpeed * increment < segment.ROAD_LENGTH) {
                    this.decelerate(increment);
                } else {
                    this.y += this.currentSpeed * increment;
                }
            } else {
                this.accelerate(increment);
            }
        } else {
            if (this.frontCar != null && this.frontCar.y < this.y) {
                
            }
            // this car has a car in front... let's check for possible collision
            this.frontCar = cars.get(cars.indexOf(this) - 1);
            if (noPossibleCollision(increment)) {
                // GOOD TO GO
                this.accelerate(increment);
            } else {
                // close in!
                this.closeIn(increment, cars, segment);
            }
        }
    }

    public synchronized void closeIn(double increment, ArrayList<Car> cars, Road segment) { // this is called when traffic light is red
        
        double frontObjectYCoordinate;
        if (this.equals(cars.get(0))) { // if this car is the first car in the segment...
            frontObjectYCoordinate = segment.ROAD_LENGTH; // front 'object' is simply the end of segment
        } else { // if this car has some car in front
            this.frontCar = cars.get(cars.indexOf(this) - 1);
            frontObjectYCoordinate = this.frontCar.getY() + this.frontCar.currentSpeed * increment; // front 'object' is the car in front
        }

        if (frontObjectYCoordinate - (this.y + this.LENGTH + this.currentSpeed * increment) <= GAP_SPACE) {
            // if this car (travelling at current topSpeed) will pass the object in front + gap,
            // stop car at exactly GAP_SPACE feet away from the object 
            this.y = frontObjectYCoordinate - GAP_SPACE - this.LENGTH;
        } else {
            this.decelerate(increment);
        }
    }
    
    public synchronized void accelerate(double increment) {
        if (this.currentSpeed + this.accelerator < this.topSpeed) {
            // speeeeed
            this.currentSpeed += this.accelerator;
            this.y += this.currentSpeed * increment;
        } else {
            this.currentSpeed = this.topSpeed;
        }
    }

    public synchronized void decelerate(double increment) {
        // check if decelerating will prevent collision with front car first
        if (this.frontCar == null) {
            // no front car to compare. decelerate normally
            if (this.currentSpeed - this.accelerator > 0) {
                this.currentSpeed -= this.accelerator;
                this.y += this.currentSpeed * increment;
            }
            return;
        }
        if ((this.currentSpeed - this.accelerator > 0) && (this.frontCar.y + this.frontCar.currentSpeed * increment - (this.y + this.LENGTH + this.currentSpeed * increment) < 2)) {
            this.currentSpeed -= this.accelerator;
            this.y += this.currentSpeed * increment;
        } else {
            // either collision might take place or currentSpeed will fall below 50% of topSpeed
            if (this.currentSpeed < this.topSpeed / 2) {
                this.currentSpeed = this.topSpeed / 2;
            }
            if (this.frontCar.y + this.frontCar.currentSpeed * increment - (this.y + this.LENGTH + this.currentSpeed * increment) <= 2) {
                // collision might happen. decelerate to the speed at which collision can be prevented
                double idealSpeed = this.frontCar.y + this.frontCar.currentSpeed * increment - 2 - this.y - this.LENGTH;
                idealSpeed /= increment;
                if (idealSpeed > 0) {
                    this.currentSpeed = idealSpeed;
                } else {
                    System.out.println("Something bad has occured during simulation");
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
