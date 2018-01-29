package mdvpr;

import java.util.ArrayList;

public class GA {

    private Plot plotter;
    private ArrayList<Depot> depots;
    private ArrayList<Customer> customers;

    public GA() {
        this.plotter = new Plot();
        DataGenerator data = new DataGenerator("p01");
        this.depots = data.getDepots();
        this.customers = data.getCustomers();

        plotter.plotDepots(depots);
        plotter.plotCustomers(customers);
    }


    private void initPop(){
        // population is alot of individual. Individual = solution. Individual = chromosone

        // one solution = individual is; all customers are covered by a vehicle
    }

    public static void main(String[] args) {
        GA ga = new GA();


    }
}
