package mdvpr;

import java.util.ArrayList;

public class Vehicle {

    private int xPos;
    private int yPos;

    private int maxDuration;
    private int currentDuration = 0;
    private int maxLoad;
    private ArrayList<Customer> path;
    private Depot belongsToDepot;



    public Vehicle(int xPos, int yPos, int maxDuration, int maxLoad, Depot depot) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.maxDuration = maxDuration;
        this.maxLoad = maxLoad;
        this.path = new ArrayList<>();
        this.belongsToDepot = depot;
    }

    public int getMaxDuration() {
        return this.maxDuration;
    }

    public int getCurrentDuration() {
        return this.currentDuration;
    }

    public void addCustomer(Customer c){
        this.path.add(c);
    }

    public ArrayList<Customer> getPath() {
        return path;
    }

    public void resetCurrentDuration() {
        this.currentDuration = 0;
    }



    public String toString(){
        return "Vechicle: " +xPos +","+yPos + " :: " + path;
    }
}
