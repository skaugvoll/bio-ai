package mdvpr;

import java.util.ArrayList;

public class Depot {

    private int id;
    private int xpos;
    private int ypos;

    private int maximumDurationOfRoute;
    private int maximumLoadOfAVehicle;
    private int numVehicles;

    private ArrayList<Vehcile> vehicles = new ArrayList<Vehcile>();


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
            vehicles.add(new Vehcile(xpos, ypos, maximumDurationOfRoute, maximumLoadOfAVehicle));
        }
    }

    public int getXpos() {
        return xpos;
    }

    public int getYpos() {
        return ypos;
    }

    public String toString(){
        return "Depot: " + id +
                "\nMax Dur Route: " + this.maximumDurationOfRoute +
                "\nMax Load Vehicle: " + this.maximumLoadOfAVehicle;

    }





}
