package mdvpr;

public class Vehicle {

    private int xPos;
    private int yPos;

    private int maxDuration;
    private int maxLoad;


    public Vehicle(int xPos, int yPos, int maxDuration, int maxLoad) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.maxDuration = maxDuration;
        this.maxLoad = maxLoad;
    }
}
