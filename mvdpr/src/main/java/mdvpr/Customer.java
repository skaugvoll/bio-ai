package mdvpr;

public class Customer {

    private int id;
    private int xpos;
    private int ypos;
    private int serviceDuration;
    private int demand;
    private boolean satisfied;
    private boolean scheduled;

    public Customer(int id, int xpos, int ypos, int serviceDuration, int demand) {
        this.id = id;
        this.xpos = xpos;
        this.ypos= ypos;
        this.serviceDuration = serviceDuration;
        this.demand = demand;
        this.scheduled = false;
        this.satisfied = false;
    }

    public int getXpos() {
        return xpos;
    }

    public int getYpos() {
        return ypos;
    }

    public boolean isScheduled() {
        return this.scheduled;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }

    public int getId() {
        return id;
    }

    public String toString(){
        return String.valueOf(this.id);
    }

    public int getDemand(){
        return this.demand;
    }

}
