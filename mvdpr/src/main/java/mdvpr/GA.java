package mdvpr;

import javax.sound.midi.Soundbank;
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

//        plotter.plotDepots(depots);
//        plotter.plotCustomers(customers);
    }


    private void initPop(int popSize){
        // population is alot of individual. Individual = solution. Individual = chromosone
        ArrayList<Chromosome> population = new ArrayList<>();

        // one solution = individual is; all customers are covered by a vehicle

        Random r = new Random();

        for(int i = 0; i < popSize; i++){
            System.out.println("iter: " + i);
            ArrayList<Vehicle> cars = new ArrayList<>();

            while(checkIfNewCustomerIsPossible()){
                // Choose a random depot
                int d = r.nextInt(this.depots.size()); // from 0 to but not included size = 4 --> 0, 1, 2, 3
                Depot depot = this.depots.get(d);

                // Choose a random vehicle
                int v = r.nextInt(depot.getVehicles().size());
                Vehicle vehicle = depot.getVehicle(v);


                int c = r.nextInt(customers.size());
                Customer customer = this.customers.get(c);

                // if the customer allready has a vehicle on it's way
                if(customer.isScheduled()){
                    continue; // try a new random depot, vehicle, and customer || start while loop from top.
                }

                // make sure this customer is not choosen again.
                customer.setScheduled(true);
                // add this customer to the cars rout / path.
                vehicle.addCustomer(customer);
                if(!cars.contains(vehicle)){
                    cars.add(vehicle);
                }
            }
            // now we have found one solution (good or bad | legal and or illegal)
            // create chromosome.

            Chromosome chromosome = new Chromosome(cars);
            // add chromosome to population
            population.add(chromosome);
            // reset cars and customers
            for(Vehicle v : cars){
                v.resetCurrentDuration();
                for(Customer c : v.getPath()){
                    c.setScheduled(false);
                }
            }
        }

        System.out.println("population: " + population);




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
