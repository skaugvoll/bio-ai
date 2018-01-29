package mdvpr;

import java.util.ArrayList;

public class GA {

    private Plot plotter;
    private ArrayList<Depot> depots = new ArrayList<Depot>();
    private ArrayList<Customer> customers = new ArrayList<Customer>();

    public GA() {
        this.plotter = new Plot();
        DataGenerator data = new DataGenerator("p01");
        this.depots = data.getDepots();
        this.customers = data.getCustomers();

        plotter.plotDepots(depots);
        plotter.plotCustomers(customers);
    }

    public static void main(String[] args) {
        GA ga = new GA();


    }
}
