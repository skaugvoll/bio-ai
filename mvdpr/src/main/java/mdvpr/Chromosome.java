package mdvpr;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Chromosome {
    private ArrayList<Vehicle> cars;


    public Chromosome(ArrayList<Vehicle> cars){
        this.cars = cars;

    }

    public String toString(){
        return this.cars.toString();
    }

    public ArrayList<Vehicle> getCars() {
        return cars;
    }
}
