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

        // ######### Så lenge det er kunder som ikke har blitt besøkt:

        // Choose a random depot
        int d = r.nextInt(this.depots.size()); // from 0 to but not included size = 4 --> 0, 1, 2, 3
        Depot depot = this.depots.get(d);

        // Choose a random vehicle
        int v = r.nextInt(depot.getVehicles().size());
        Vehicle vehicle = depot.getVehicle(v);

        // Find out how fare the car can travel.
        int remainingDur = vehicle.getDuration();

        // Create a route for this vehicle
        ArrayList<Customer> carPath = new ArrayList<>();

        while(remainingDur > 0 && checkIfNewCustomerIsPossible()){
            // find a random customer
            int c = r.nextInt(this.customers.size());
            Customer customer = this.customers.get(c);

            // distance til neste punkt + distansen hjem fra neste punkt er mindre enn det vi kan kjøre totalt.
            if(getDistance() + getDistance() < remainingDur && !customer.isSatisfied()){
                carPath.add(customer);
                remainingDur -= getDistance();
                customer.setSatisfaction(true);
            }
        }










    }

    private boolean checkIfNewCustomerIsPossible() {
        // sjekk om mulighet / nokk duration igjen til å dra til en kunde og hjem. Iterer over alle kunder / helt til funnet en som er mulig
        return false;

    }


    public int getDistance() {
        return 0;
    }

    public static void main(String[] args) {
        GA ga = new GA();


    }


}
