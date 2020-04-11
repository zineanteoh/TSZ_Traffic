/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tsz_traffic;

import java.util.ArrayList;
/**
 *
 * @author Zi Nean Teoh
 */
public class Car {
    
    final double LENGTH;
    public double y, speed, acceleration, reactionTime;
    Car frontCar;
    final static double GAP_SPACE = 2;
    final int carNumber;
    
    public Car(int index, double y, double length, double speed) {
        this.carNumber = index;
        this.y = y;
        this.LENGTH = length;
        this.speed = speed; // in ft per second
        this.frontCar = null;
    }
    
    public void go(double increment, ArrayList<Car> cars) { // increment is in seconds (e.g. 0.1 seconds)
        if (this.equals(cars.get(0))) {
            // this car is at the front. so just go!
            this.y += this.speed * increment;
        } else {
            // this car has a car in front... let's check for possible collision
            this.frontCar = cars.get(cars.indexOf(this) - 1);
            if (noPossibleCollision()) {
                this.y += this.speed * increment; // y coordinate of car after 0.1 seconds given its current speed
            } else {
                this.y += this.frontCar.speed * increment;
            }
        }
    }
    
    public void closeIn(double increment, ArrayList<Car> cars, Road segment) { // this is called when traffic light is red
        double frontObjectYCoordinate;
        if (this.equals(cars.get(0))) { // if this car is the first car in the segment...
            frontObjectYCoordinate = segment.ROAD_LENGTH; // front 'object' is simply the end of segment
        } else { // if this car has some car in front
            this.frontCar = cars.get(cars.indexOf(this) - 1);
            frontObjectYCoordinate = this.frontCar.getY() + this.frontCar.speed * increment; // front 'object' is the car in front
        }
        
        if ( frontObjectYCoordinate - (this.y + this.LENGTH + this.speed * increment)  <= GAP_SPACE) {
            // if this car (travelling at current speed) will pass the object in front + gap,
            // stop car at exactly GAP_SPACE feet away from the object 
            this.y = frontObjectYCoordinate - GAP_SPACE - this.LENGTH;
        } else {
            this.y += this.speed * increment;
        }
    }
    
    public boolean noPossibleCollision() {
        double thisCarAfter1S = this.y + this.LENGTH + this.speed;
        if (this.frontCar.y - thisCarAfter1S > 2) {
            return true;
        } else {
            return false;
        }
    }
    
    public double getY() {
        return this.y;
    }
    
    public double getLength() {
        return this.LENGTH;
    }
    
    public String getCarNumber() {
        return Integer.toString(this.carNumber);
    }
}
