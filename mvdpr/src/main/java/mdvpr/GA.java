package mdvpr;


import com.rits.cloning.Cloner;

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

        this.initPop(10);

        plotter.plotDepots(depots);
        plotter.plotCustomers(customers);
    }


    private void initPop(int popSize){
        // population is alot of individual. Individual = solution. Individual = chromosone
        ArrayList<Chromosome> population = new ArrayList<>();

        // one solution = individual is; all customers are covered by a vehicle



        for(int i = 0; i < popSize; i++){
            Random r = new Random();
            ArrayList<Vehicle> cars = new ArrayList<>();
            cars.clear(); // make sure we start from scratch on new solution / individual / chromosome

            for(Customer customer : this.customers){
                // Choose a random depot
                int d = r.nextInt(this.depots.size()); // from 0 to but not included size = 4 --> 0, 1, 2, 3
                Depot depot = this.depots.get(d);

                // Choose a random vehicle
                int v = r.nextInt(depot.getVehicles().size());
                Vehicle vehicle = depot.getVehicle(v);

                // add the customer to the random chosen car.
                vehicle.addCustomer(customer);

                // add this car to the list of cars, if it's not there already (been chosen and added previously)
                if(!cars.contains(vehicle)){
                    cars.add(vehicle);
                }


            }
            // now we have found one solution (good or bad | legal and or illegal)
            // create chromosome.

            // now we need a deep copy, not just refrences. since we are goint to "reset" our objects for another population to use.
            // TODO: Rewrite this into a create new object for each object method. Much more performance pleasing.
            Cloner cloner = new Cloner();
            ArrayList<Vehicle> carsCloned = cloner.deepClone(cars);

            Chromosome chromosome = new Chromosome(carsCloned);
            // add chromosome to population
            population.add(chromosome);
            // reset cars and customers
            for(Vehicle v : cars){
                v.resetCurrentDuration();
                v.clearPath();
                for(Customer c : v.getPath()){
                    c.setScheduled(false);
                }
            }
        }

        this.plotter.plotChromosome(population.get(0));




    }

    private boolean checkIfNewCustomerIsPossible() {
        // sjekk om mulighet / nokk duration igjen til å dra til en kunde og hjem. Iterer over alle kunder / helt til funnet en som er mulig
        for(Customer c : this.customers){
            if(!c.isScheduled()){
                return true;
            }
        }
        return false;


    }


    public int getDistance() {
        return 0;
    }

    public static void main(String[] args) {
        GA ga = new GA();


    }


}
