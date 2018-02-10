package mdvpr;

import java.util.ArrayList;

public class Vehicle {

    private int xPos;
    private int yPos;

    private int maxDuration;
    private double currentDuration = 0;
    private int maxLoad;
    private int currentLoad = 0;
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

    public int getXPos() {return this.xPos;}
    public int getYPos() {return this.yPos;}


    public int getMaxDuration() {
        return this.maxDuration;
    }

    public int getMaxLoad() {
        return this.maxLoad;
    }

    public double getCurrentDuration() {
        return this.currentDuration;
    }

    public int getCurrentLoad() {
        return currentLoad;
    }

    public void addCustomer(Customer c){
        this.xPos = c.getXpos();
        this.yPos = c.getYpos();
        this.path.add(c);
        this.setCurrentDuration();

    }

    public ArrayList<Customer> getPath() {
        return path;
    }

    public void resetCurrentDuration() {
        this.currentDuration = 0;
    }

    public void clearPath() {
        this.path.clear();
    }

    public void setxyPos(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public void setCurrentDuration() {
        this.currentDuration = 0;
        this.currentLoad = 0;

        int lastX = this.belongsToDepot.getXpos();
        int lastY = this.belongsToDepot.getYpos();

        for(Customer c : this.path){
            currentLoad += c.getDemand();

            int currentX = c.getXpos();
            int currentY = c.getYpos();

            this.currentDuration += this.getEuclideanDistance(lastX, lastY, currentX, currentY);
            lastX = currentX;
            lastY = currentY;
        }
        // now we need to go back home again
        this.currentDuration += this.getEuclideanDistance(lastX, lastY, this.belongsToDepot.getXpos(), this.belongsToDepot.getYpos());

    }

    public double getEuclideanDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
    }

//    public String toString(){
//        return "Vechicle: " +xPos +","+yPos + " :: " + path;
//    }

    public Depot getDepo() {
        return belongsToDepot;
    }
}
