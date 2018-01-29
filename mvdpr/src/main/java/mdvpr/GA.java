package mdvpr;

import java.util.ArrayList;
import java.util.Random;

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
        ArrayList<Chromosome> population = new ArrayList<>();

        // one solution = individual is; all customers are covered by a vehicle
        Random r = new Random();
        int d = r.nextInt(this.depots.size()); // from 0 to but not included size = 4 --> 0, 1, 2, 3
        System.out.println("Depo int: " + d);

        Depot depot = this.depots.get(d);

        int v = r.nextInt(depot.getVehicles().size());
        Vehicle vehicle = depot.getVehicle(v);
    


    }

    public static void main(String[] args) {
        GA ga = new GA();


    }
}
