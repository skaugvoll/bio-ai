package mdvpr;

public class Customer {

    private int id;
    private int xpos;
    private int ypos;
    private int serviceDuration;
    private int demand;
    private boolean satisfied;

    public Customer(int id, int xpos, int ypos, int serviceDuration, int demand) {
        this.id = id;
        this.xpos = xpos;
        this.ypos= ypos;
        this.serviceDuration = serviceDuration;
        this.demand = demand;
    }

    public int getXpos() {
        return xpos;
    }

    public int getYpos() {
        return ypos;
    }

    public boolean isSatisfied() {
        return this.satisfied;
    }

    public void setSatisfaction(boolean satisfaction) {
        this.satisfied = satisfaction;
    }
}
