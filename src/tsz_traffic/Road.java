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
public class Road {
    public ArrayList<Car> carArray;
    final int ROAD_LENGTH;
    public Light light;
    public int outflux = 0, influx = 0;
    final double CHECK = 0.8;
    
    public Road(int roadLength, Light light) {
        this.ROAD_LENGTH = roadLength; // length of segment + the crossroad behind
        this.carArray = new ArrayList<Car>();
        this.light = light;
    }
    
    public void goCars(double increment) {
        // but we need to take into account of the delay caused by reaction time and interaction between cars
        for (Car c : this.carArray) {
            c.go(increment, this.carArray);
        }
    }

    public void closeInCars(double increment) {
        for (Car c : this.carArray) {
            c.closeIn(increment, this.carArray, this);
        }
    }

    public boolean carExit() {
        if (this.carArray.size() == 0) {
            return false;
        }
        Car frontMostCar = this.carArray.get(0);
        if (frontMostCar.getY() + frontMostCar.getLength() >= this.ROAD_LENGTH) {
            return true;
        } else {
            return false;
        }
    }

    public void updateLight(double time) {
        this.light.update(time);
    }
    
    public boolean checkCondition(double time) {
        time = Double.parseDouble(String.format("%.1f", time));
        return ((time - this.getLights().lastUpdatedTime) >= CHECK * this.getLights().updateTime);
    }
    
    public void simpleUpdate(double time) { 
        this.light.update(time);
    }

    public void populateCar(Car car) {
        this.carArray.add(car);
    }

    public Road addCar(Car car) {
        this.carArray.add(car);
        return this;
    }

    public Road removeCar(Car lastCar) {
        this.carArray.remove(lastCar);
        return this;
    }

    public int getOutflux() {
        this.outflux = 0; 
        if (this.getLights().isGreen()) {
            for (int i = 0; i < this.carArray.size(); i++) {
                if (i == 0) {
                    if (((this.ROAD_LENGTH - this.carArray.get(i).y) / this.carArray.get(i).speed) < 5) {
                        this.outflux++;
                    }
                } else {
                    if (this.carArray.get(i).speed > this.carArray.get(i - 1).speed) {
                        if (((this.ROAD_LENGTH - this.carArray.get(i).y) / this.carArray.get(i - 1).speed) < 5) {
                            this.outflux++;
                        }
                    } else {
                        if (((this.ROAD_LENGTH - this.carArray.get(i).y) / this.carArray.get(i).speed) < 5) {
                            this.outflux++;
                        }
                    }
                }
            }
        } 
        return this.outflux;
    }

    public int getInflux(ArrayList<Road> segments, int index) {
        if (index == segments.size() - 1) {
            return 100;
        }
        this.influx = segments.get(index + 1).getOutflux();
        return this.influx;
    }

    public boolean densityCheck() {
        if (this.carArray.size() > (this.ROAD_LENGTH / 19) && this.influx >= (this.outflux * 1.25)) {//25% larger because tiny spikes may exist
            return true;
        } else {
            return false;
        }
    }

    public Car getFrontCar() {
        return this.carArray.get(0);
    }

    public int getLength() {
        return this.ROAD_LENGTH;
    }

    public Light getLights() {
        return this.light;
    }

    public ArrayList<Car> getCarArray() {
        return this.carArray;
    }
    
}
