package mdvpr;

public class Customer {

    private int id;
    private int x;
    private int y;
    private int serviceDuration;
    private int demand;

    public Customer(int id, int x, int y, int serviceDuration, int demand) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.serviceDuration = serviceDuration;
        this.demand = demand;
    }
}
