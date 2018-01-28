package mdvpr;

public class Depot {

    private int id;
    private int xpos;
    private int ypos;

    private int maximumDurationOfRoute;
    private int maximumLoadOfAVehicle;

    public Depot(int id, int maximumDurationOfRoute, int maximumLoadOfAVehicle){
        this.id = id;
        this.maximumDurationOfRoute = maximumDurationOfRoute;
        this.maximumLoadOfAVehicle = maximumLoadOfAVehicle;

    }

    public void setPos(int x, int y){
        this.xpos = x;
        this.ypos = y;
    }


    public String toString(){
        return "Depot: " + id +
                "\nMax Dur Route: " + this.maximumDurationOfRoute +
                "\nMax Load Vehicle: " + this.maximumLoadOfAVehicle;

    }





}
