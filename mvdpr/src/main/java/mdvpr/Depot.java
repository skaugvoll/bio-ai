package mdvpr;

import java.util.ArrayList;

public class Depot {

    private int id;
    private int xpos;
    private int ypos;

    private int maximumDurationOfRoute;
    private int maximumLoadOfAVehicle;
    private int numVehicles;
    private int currentRouteIndex = 0;
    private ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();


    public Depot(int id, int maximumDurationOfRoute, int maximumLoadOfAVehicle, int numVehicles){
        this.id = id;
        this.maximumDurationOfRoute = maximumDurationOfRoute;
        this.maximumLoadOfAVehicle = maximumLoadOfAVehicle;
        this.numVehicles = numVehicles;
    }

    public void setPos(int x, int y){
        this.xpos = x;
        this.ypos = y;
        makeVehicles();
    }

    private void makeVehicles() {
        for(int i = 0; i < numVehicles; i++){
            vehicles.add(new Vehicle(xpos, ypos, maximumDurationOfRoute, maximumLoadOfAVehicle, this));
        }
    }

    public int getXpos() {
        return xpos;
    }

    public int getYpos() {
        return ypos;
    }

    public ArrayList<Vehicle> getVehicles(){
        return this.vehicles;
    }

    public String toString(){
        return "Depot: " + id +
                "\nMax Dur Route: " + this.maximumDurationOfRoute +
                "\nMax Load Vehicle: " + this.maximumLoadOfAVehicle;

    }


    public Vehicle getVehicle(int v) {
        return this.vehicles.get(v);
    }

    public int getCurrentRouteIndex() {
        return currentRouteIndex;
    }

    public void increaseCurrentRouteIndex() {
        currentRouteIndex++;
        if(currentRouteIndex == numVehicles){
            currentRouteIndex = 0;
        }
    }

    public int getId(){
        return this.id;
    }

}
