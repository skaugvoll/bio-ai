package mdvpr;

import java.util.ArrayList;

public class Chromosome {
    private ArrayList<Vehicle> cars;
    private double fitness = -1;


    public Chromosome(ArrayList<Vehicle> cars){
        this.cars = cars;

    }


    public void setFitness(double score){
        this.fitness = score;
    }

    public double getFitness(){ return this.fitness; }


    public ArrayList<Vehicle> getCars() {
        return cars;
    }


    public String toString(){
        return this.cars.toString();
    }
}
