package mdvpr;


import com.rits.cloning.Cloner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GA {

    private Plot plotter;
    private ArrayList<Depot> depots;
    private ArrayList<Customer> customers;
    // population is alot of individual. Individual = solution. Individual = chromosone
    private ArrayList<Chromosome> population = new ArrayList<>();
    Random r = new Random();

    public GA(String filename, int populationSize, int maxEphochs, double crossoverRate, double mutationRate) {
        plotData(filename);
        run(populationSize, maxEphochs, crossoverRate, mutationRate);
    }

    public void plotData(String filename){
        this.plotter = new Plot();
        DataGenerator data = new DataGenerator(filename);
        this.depots = data.getDepots();
        this.customers = data.getCustomers();
        plotter.plotDepots(depots);
        plotter.plotCustomers(customers);

    }

    private void initPop(int popSize){
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
                while(!this.isValidRoute(vehicle)){
                    vehicle.getPath().remove(customer);
                    // Choose a random depot
                    d = r.nextInt(this.depots.size()); // from 0 to but not included size = 4 --> 0, 1, 2, 3
                    depot = this.depots.get(d);

                    // Choose a random vehicle
                    v = r.nextInt(depot.getVehicles().size());
                    vehicle = depot.getVehicle(v);

                    // add the customer to the random chosen car.
                    vehicle.addCustomer(customer);
                }


            }

            // add all cars to the list
            for(Depot d : this.depots){
                for(Vehicle v : d.getVehicles()){
                    if(!cars.contains(v)){
                        cars.add(v);
                    }
                }
            }

            // now we have found one solution (good or bad | legal and or illegal)
            // create chromosome.

            // now we need a deep copy, not just refrences. since we are goint to "reset" our objects for another population to use.
            // TODO: Rewrite this into a create new object for each object method. Much more performance pleasing.
            Cloner cloner = new Cloner();
            ArrayList<Vehicle> carsCloned = cloner.deepClone(cars);

            Chromosome chromosome = new Chromosome(carsCloned);
            this.calculateFitness(chromosome); // calculate the fitness score for this chromosome
            population.add(chromosome);// add chromosome to population

            // reset cars and customers
            for(Vehicle v : cars){
                v.resetCurrentDuration();
                v.clearPath();
                for(Customer c : v.getPath()){
                    c.setScheduled(false);
                }
            }
        }
//        this.plotter.plotChromosome(population.get(0));

    }

    private void calculateFitness(Chromosome chrome){
        // the fitness for a solution is the total distance  (not duration) traveled. duration = criteria for route.
        double totalDistance = 0;

        for(Vehicle v : chrome.getCars()){
            // get the "init depo/car position"
            int lastX = v.getXPos();
            int lastY = v.getYPos();

            for(Customer c : v.getPath()){
                int currentX = c.getXpos();
                int currentY = c.getYpos();

                totalDistance += this.getEuclideanDistance(lastX, lastY, currentX, currentY);
                lastX = currentX;
                lastY = currentY;
            }

            // now we need to go back home again
            totalDistance += this.getEuclideanDistance(lastX, lastY, v.getDepo().getXpos(), v.getDepo().getYpos());

            // TODO: implement some sort of stuff here...
            // punish the illegal routes
            if(v.getCurrentDuration() > v.getMaxDuration()){
                System.out.println("Current duration: " + v.getCurrentDuration() + " Max duration: " + v.getMaxDuration());
            }
        }

        // set the fitnesscore for this chromosome.
        chrome.setFitness(totalDistance);
    }

    public double getEuclideanDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
    }

    public Boolean isValidRoute(Vehicle vehicle){
        int currentLoad = 0;
        int currentDurration = 0;

        int lastX = vehicle.getDepo().getXpos();
        int lastY = vehicle.getDepo().getYpos();

        for(Customer c : vehicle.getPath()){
            currentLoad += c.getDemand();

            int currentX = c.getXpos();
            int currentY = c.getYpos();

            currentDurration += this.getEuclideanDistance(lastX, lastY, currentX, currentY);
            lastX = currentX;
            lastY = currentY;
        }
        // now we need to go back home again
        currentDurration += this.getEuclideanDistance(lastX, lastY, vehicle.getDepo().getXpos(), vehicle.getDepo().getYpos());

        if (vehicle.getMaxDuration() > 0) {
            return currentLoad <= vehicle.getMaxLoad() && currentDurration <= vehicle.getMaxDuration();
        }

        return currentLoad < vehicle.getMaxLoad();
    }

    public ArrayList<Chromosome> selectParents(){
        Collections.sort(this.population, new SortPopulationComparator());
        return new ArrayList<>(population.subList(0, population.size()/3));
    }

    private Chromosome crossover(Chromosome survivor1, Chromosome survivor2, double crossoverRate) {
        Chromosome temp = new Cloner().deepClone(survivor2);
        ArrayList<Customer> custs = new ArrayList<>();

        Vehicle v1 = survivor1.getCars().get(r.nextInt(survivor1.getCars().size()));
        for (Customer c : v1.getPath()){
            for(Vehicle v : temp.getCars()){
                for(Customer c2 : v.getPath()){
                    if(c.getId() == c2.getId()){
                        custs.add(v.getPath().remove(v.getPath().indexOf(c2)));
                        break;
                    }
                }
            }
        }

        for(Customer cust : custs){
            ArrayList<Vehicle> possibleEntries = new ArrayList<>();
            ArrayList<Double> costs = new ArrayList<>();
            for(Vehicle vehic: temp.getCars()){
                vehic.getPath().add(cust);
                if(this.isValidRoute(vehic)){
                    possibleEntries.add(vehic);
                    this.calculateFitness(temp);
                    costs.add(temp.getFitness());
                }
                vehic.getPath().remove(cust);
            }

            double k = r.nextDouble();
            if(k <= crossoverRate){
                possibleEntries.get(0).addCustomer(cust);
            }else{
                possibleEntries.get(costs.indexOf(Collections.min(costs))).addCustomer(cust);
            }

        }
        this.mutation(temp);
        this.calculateFitness(temp);
        return temp;
    }

    private void mutation(Chromosome offspring){
        Random r = new Random();

        if(offspring.getCars().size() < 1) {
            return;
        }

        Vehicle vehicleOne = offspring.getCars().get(r.nextInt(offspring.getCars().size()));


        if(vehicleOne.getPath().size() > 1){
            int custIndex = r.nextInt(vehicleOne.getPath().size());
            Customer cust = vehicleOne.getPath().remove(custIndex);
            int newPosition = r.nextInt(vehicleOne.getPath().size());
            vehicleOne.getPath().add(newPosition, cust);
        }

    }

    public void run(int populationSize, int maxEphochs, double crossoverRate, double mutationRate){
        this.initPop(populationSize); // 2 & 3. this also evaluates the fitness
        int epoch = 0; // 1.
        ArrayList<Chromosome> newPopulation;
        while(epoch < maxEphochs){ // 4
            newPopulation = selectParents(); // 5. select parents

            System.out.println("population: " + epoch + " :: " + population);
            // basically create all offsprings and crossover them.
            while(newPopulation.size() < population.size()){
//                Collections.sort(population, new WeightPopulationComparator());
                Chromosome survivor1 = population.get(0);

                Chromosome survivor2 = newPopulation.get(r.nextInt(newPopulation.size()));

//                // 6. Crossover and // 7. mutation on offspring
                newPopulation.add(this.crossover(survivor1, survivor2, crossoverRate));
                if(newPopulation.size() == population.size())
                    break;
                newPopulation.add(this.crossover(survivor2, survivor1, crossoverRate));
            }
            this.population = new Cloner().deepClone(newPopulation);
            epoch ++;
        }
        this.plotter.plotChromosome(population.get(0));
        this.plotter.updateUI();
    }

    public static void main(String[] args) {
        GA ga = new GA("p01", 100, 1000, 0.8, 1);
    }
}
