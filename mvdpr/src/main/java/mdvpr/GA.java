package mdvpr;


import com.rits.cloning.Cloner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class GA {

    private Plot plotter;
    private ArrayList<Depot> depots;
    private ArrayList<Customer> customers;
    // population is alot of individual. Individual = solution. Individual = chromosone
    private ArrayList<Chromosome> population = new ArrayList<>();
    Random r = new Random();

    public GA(String filename) {
        plotData(filename);
    }

    public void plotData(String filename){
        this.plotter = new Plot();
        DataGenerator data = new DataGenerator(filename);
        this.depots = data.getDepots();
        this.customers = data.getCustomers();
        plotter.plotDepots(depots);
        plotter.plotCustomers(customers);

    }

    private void initPop(int popSize, boolean print) {

        while (this.population.size() < popSize) {
            Collections.shuffle(this.customers);
            HashMap<Integer, ArrayList<Depot>> preferedDepots = new HashMap<>();
            for (Customer customer : this.customers) {
                ArrayList<Depot> nearestDepots = new ArrayList<>();
                for (Depot depot : this.depots) {
                    depot.setCurrentDistance(this.getEuclideanDistance(customer.getXpos(), customer.getYpos(), depot.getXpos(), depot.getYpos()));
                    nearestDepots.add(depot);
                }
                nearestDepots.sort(new DepotDistanceComparator());
                preferedDepots.put(customer.getId(), nearestDepots);
                resetDepots();
            }
            generateSolution(preferedDepots);
        }

        if(print){
            this.plotter.plotChromosome(population.get(0));
            this.plotter.updateUI();
        }
    }

    private void generateSolution(HashMap<Integer, ArrayList<Depot>> preferedDepots){
        ArrayList<Vehicle> cars = new ArrayList<>();
        ArrayList<Customer> unsatisfiedCustomers = new Cloner().deepClone(this.customers);

        while(! unsatisfiedCustomers.isEmpty()){
            Vehicle bestVehicle = null;
            for(Depot depot : preferedDepots.get(unsatisfiedCustomers.get(0).getId())){
                int bestIndex = 0;
                double bestCost = Double.MAX_VALUE;

                for(Vehicle vehicle : depot.getVehicles()){
                    if(vehicle.getPath().size() < 1){
                        bestIndex = 0;
                        bestVehicle = vehicle;
                        break;
                    }
                    for(int i = 0; i < vehicle.getPath().size(); i++){
                        vehicle.addCustomerToSpot(unsatisfiedCustomers.get(0), i);
                        if(isValidRoute(vehicle)){
                            if(vehicle.getCurrentDuration() < bestCost){
                                bestCost = vehicle.getCurrentDuration();
                                bestVehicle = vehicle;
                                bestIndex = i;
                            }
                        }
                        vehicle.removeCustomer(unsatisfiedCustomers.get(0));
                    }
                }
                if(bestVehicle != null){
                    bestVehicle.addCustomerToSpot(unsatisfiedCustomers.get(0), bestIndex);
                    unsatisfiedCustomers.remove(0);
                    break;
                }
            }
            if(bestVehicle == null){
                Depot dp = preferedDepots.get(unsatisfiedCustomers.get(0).getId()).get(0);
                Vehicle v = dp.getVehicles().get(r.nextInt(dp.getVehicles().size()));
                unsatisfiedCustomers.add(v.removeCustomerFromSpot(r.nextInt(v.getPath().size())));
                v.addCustomer(unsatisfiedCustomers.remove(0));
            }
        }

        addCarsToSolutionList(cars);

        Cloner cloner = new Cloner();
        ArrayList<Vehicle> carsCloned = cloner.deepClone(cars);

        Chromosome chromosome = new Chromosome(carsCloned);
        this.calculateFitness(chromosome); // calculate the fitness score for this chromosome
        population.add(chromosome);// add chromosome to population
        resetCars(cars);
        resetDepots();
    }

    private void resetDepots() {
        for(Depot d : this.depots){
            d.resetCurrentDistance();
        }
    }


    private void addCarsToSolutionList(ArrayList<Vehicle> cars) {
        // add all cars to the list -- make solution
        for(Depot d : this.depots){
            for(Vehicle v : d.getVehicles()){
                if(!cars.contains(v)){
                    cars.add(v);
                }
            }
        }
    }

    private void resetCars(ArrayList<Vehicle> cars) {
        // reset cars and customers
        for(Vehicle v : cars){
            v.resetCurrentDuration();
            v.clearPath();
            for(Customer c : v.getPath()){
                c.setScheduled(false);
            }
        }
    }


    private void calculateFitness(Chromosome chrome){
        // the fitness for a solution is the total distance  (not duration) traveled. duration = criteria for route.
        double totalDistance = 0;

        for(Vehicle v : chrome.getCars()) {

            totalDistance += v.getCurrentDuration();
        }
        // set the fitnesscore for this chromosome.
        chrome.setFitness(totalDistance);
    }

    public double getEuclideanDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
    }

    public Boolean isValidRoute(Vehicle vehicle){
        if (vehicle.getMaxDuration() > 0) {
            return vehicle.getCurrentLoad() <= vehicle.getMaxLoad() && vehicle.getCurrentDuration() <= vehicle.getMaxDuration();
        }

        return vehicle.getCurrentLoad() < vehicle.getMaxLoad();
    }

    public ArrayList<Chromosome> selectParents(int numberOfCandidates){
        Collections.sort(this.population, new SortPopulationComparator());

        //number of candidates = 2 gives binary tournament.
        ArrayList<Chromosome> candidates = new ArrayList<>();
        for(int i = 0; i < numberOfCandidates; i++){
            candidates.add(this.population.get(r.nextInt(this.population.size())));
        }
        Collections.sort(candidates, new SortPopulationComparator());

        return candidates;
    }

    private Chromosome crossover(Chromosome survivor1, Chromosome survivor2, double crossoverRate, double mutationRate) {
        Chromosome temp = new Cloner().deepClone(survivor2);
        ArrayList<Customer> custs = new ArrayList<>();
        Vehicle v1 = survivor1.getCars().get(r.nextInt(survivor1.getCars().size()));
        for (Customer c : v1.getPath()){
            for(Vehicle v : temp.getCars()){
                for(Customer c2 : v.getPath()){
                    if(c.getId() == c2.getId()){
                        custs.add(v.getPath().remove(v.getPath().indexOf(c2)));
                        v.setCurrentDuration();
                        break;
                    }
                }
            }
        }

        for(Customer cust : custs){
            ArrayList<Vehicle> possibleEntries = new ArrayList<>();
            ArrayList<Double> costs = new ArrayList<>();

            for(Vehicle vehic: temp.getCars()){
                vehic.addCustomer(cust);

                if(this.isValidRoute(vehic)){
                    possibleEntries.add(vehic);
                    this.calculateFitness(temp);
                    costs.add(temp.getFitness());
                }
                vehic.removeCustomer(cust);
                vehic.setCurrentDuration();
            }

            if (possibleEntries.size() < 1){
//                return this.crossover(survivor1, survivor2, crossoverRate, mutationRate);
                return new Cloner().deepClone(survivor2);
            }
            double k = r.nextDouble();
            if(k <= crossoverRate){
                possibleEntries.get(0).addCustomer(cust);
            }else{
                possibleEntries.get(costs.indexOf(Collections.min(costs))).addCustomer(cust);
            }
        }


        if(r.nextDouble() < mutationRate){
            double mutationProb = r.nextDouble();
            if(mutationProb < 0.33){
//                this.mutation(temp);
                this.swapping(temp);
            } else if(mutationProb >= 0.33 && mutationProb < 0.66){
                this.mutation(temp);
//                this.singleCustomerReRoutingMutation(temp);
            } else{
                this.reversMutation(temp);
            }
        }
        this.calculateFitness(temp);
        return temp;
    }

    private void swapping(Chromosome temp) {
        Vehicle v1 = temp.getCars().get(r.nextInt(temp.getCars().size()));
        while(v1.getPath().size() <= 1){
            v1 = temp.getCars().get(r.nextInt(temp.getCars().size()));
        }
        Vehicle v2 = temp.getCars().get(r.nextInt(temp.getCars().size()));
        while(v2.getPath().size() <= 1){
            v2 = temp.getCars().get(r.nextInt(temp.getCars().size()));
        }

        int cust1Spot = v1.getPath().size() <= 1 ? 0 : r.nextInt(v1.getPath().size()-1);
        int cust2Spot = v2.getPath().size() <= 1 ? 0 : r.nextInt(v2.getPath().size()-1);

        Customer c1 = v1.removeCustomerFromSpot(cust1Spot);
        Customer c2 = v2.removeCustomerFromSpot(cust2Spot);

        if(v1.getPath().size() == 0){
            v1.addCustomer(c2);
        }else {
            v1.addCustomerToSpot(c2, r.nextInt(v1.getPath().size()));
        }

        if(! isValidRoute(v1)) {
            v1.removeCustomer(c2);

            if (v2.getPath().size() == 0) {
                v2.addCustomer(c1);
            } else {
                v2.addCustomerToSpot(c1, r.nextInt(v2.getPath().size()));
            }

            if (!isValidRoute(v2)) {
                v2.removeCustomer(c1);
                v1.addCustomerToSpot(c1, cust1Spot);
                v2.addCustomerToSpot(c2, cust2Spot);
            } else {
                v2.addCustomer(c2);
                if (!isValidRoute(v2)) {
                    v2.removeCustomer(c1);
                    v1.addCustomerToSpot(c1, cust1Spot);
                }

            }
        }


        ////////////////////////////


        if(v2.getPath().size() == 0){
            v2.addCustomer(c1);
        }else {
            v2.addCustomerToSpot(c1, r.nextInt(v2.getPath().size()));
        }

        if(! isValidRoute(v2)) {
            v2.removeCustomer(c1);

            if (v1.getPath().size() == 0) {
                v1.addCustomer(c2);
            } else {
                v1.addCustomerToSpot(c2, r.nextInt(v1.getPath().size()));
            }

            if (!isValidRoute(v1)) {
                v1.removeCustomer(c2);
                v2.addCustomerToSpot(c2, cust2Spot);
                v1.addCustomerToSpot(c1, cust1Spot);
            } else {
                v1.addCustomer(c1);
                if (!isValidRoute(v1)) {
                    v1.removeCustomer(c2);
                    v2.addCustomerToSpot(c2, cust2Spot);
                }

            }
        }




    }


    private void singleCustomerReRoutingMutation(Chromosome offspring){
        Customer c = null;
        while(c == null){
            Vehicle v = offspring.getCars().get(r.nextInt(offspring.getCars().size()));
            if(v.getPath().size() >= 1){
                if(v.getPath().size() > 2){
                    c = v.removeCustomerFromSpot(r.nextInt(v.getPath().size()));
                }
                else {
                    c = v.removeCustomerFromSpot(0);
                }
            }
        }

        Vehicle vehicle = null;
        int index = -1;
        double cost = Double.MAX_VALUE;

        for(Vehicle v: offspring.getCars()){
            if(v.getPath().isEmpty()){
                v.addCustomer(c);
                if(v.getCurrentDuration() < cost){
                    cost = v.getCurrentDuration();
                    vehicle = v;
                    index = 0;
                }
                v.removeCustomer(c);

            } else {
                for(int i = 0; i < v.getPath().size(); i++){
                    v.addCustomerToSpot(c,i);
                    if(v.getCurrentDuration() < cost){
                        cost = v.getCurrentDuration();
                        vehicle = v;
                        index = i;
                    }
                    v.removeCustomer(c);
                }
            }
        }

        if(vehicle != null){
            if(index > 0) {
                vehicle.addCustomerToSpot(c, index);
            }
            else {
                vehicle.addCustomer(c);
            }
        }
    }

    private void reversMutation(Chromosome offspring){

        Vehicle v = offspring.getCars().get(r.nextInt(offspring.getCars().size()));

        if(v.getPath().size() < 3){
            return;
        }

        int cutpoint1 = r.nextInt(v.getPath().size());
        int cutpoint2 = r.nextInt(v.getPath().size());

        while(cutpoint1 >= cutpoint2){
            cutpoint1 = r.nextInt(v.getPath().size());
            cutpoint2 = r.nextInt(v.getPath().size());
        }

        Collections.reverse(v.getPath().subList(cutpoint1,cutpoint2));
    }

   
    private void mutation(Chromosome offspring){
        if(offspring.getCars().size() < 1) {
            return;
        }

        Vehicle vehicleOne = offspring.getCars().get(r.nextInt(offspring.getCars().size()));

        if(vehicleOne.getPath().size() > 1){
            int custIndex = r.nextInt(vehicleOne.getPath().size());
            Customer cust = vehicleOne.removeCustomerFromSpot(custIndex);

            int newPosition = r.nextInt(vehicleOne.getPath().size());
            vehicleOne.getPath().add(newPosition, cust);
            vehicleOne.setCurrentDuration();
        }

    }

    public void printSolution(){
        Chromosome solution = this.population.get(0);
        StringBuilder sb = new StringBuilder();
        // first line
        sb.append(String.format("%.2f\n",solution.getFitness()));

        // l k d q list :
        // l: number of the depot
        // k: number of the vehicle (for above depot)
        // d: duration of the route for a particular vehicle from a particular depot  q: carried load of the vehicle
        // list: ordered sequence of customers (served by a particular vehicle with “0” means the vehicle starts and ends from a particular depot.

        for(Vehicle v : solution.getCars()){
            if(v.getPath().isEmpty()){
                continue;
            }
            int depotNumber = v.getDepo().getId();
            int carId = v.getDepo().getVehicles().indexOf(v);
            double duration = v.getCurrentDuration();
            int load = v.getLoad();
            String s = String.format("%d\t %d\t %.2f\t %d\t" + v.getPath() + "\n", depotNumber, carId, duration, load );
            sb.append(s);

        }

        System.out.println("Solution:\n" + sb);
    }

    public void run(int populationSize, int maxEphochs, double crossoverRate, double mutationRate) {
        this.initPop(populationSize, false); // 2 & 3. this also evaluates the fitness
        int epoch = 0; // 1.

        while (epoch < maxEphochs) { // 4

            ArrayList<Chromosome> newPopulation = new ArrayList<>();
            ArrayList<Chromosome> parents = selectParents(200); // 5. select parents
            ArrayList<Chromosome> kids = new ArrayList<>();

            System.out.println("population: " + epoch + " :: " + population.get(0).getFitness());

            // basically create all offsprings and crossover them.
            while (kids.size() < population.size()) {
                // 6. Crossover and // 7. mutation on offspring
                double k = r.nextDouble();
                double prob = 0.8;
                if (k <= prob) {
                    kids.add(this.crossover(population.get(0), parents.get(0), crossoverRate, mutationRate));
                } else {
                    kids.add(this.crossover(parents.get(r.nextInt(parents.size())), parents.get(r.nextInt(parents.size())), crossoverRate, mutationRate));
                }

            }
            ArrayList<Chromosome> temp = new ArrayList<>();
            temp.addAll(parents);
            temp.addAll(kids);
            Collections.sort(temp, new SortPopulationComparator());
            newPopulation.add(population.get(0)); //:: ELITISM ; best is always taken to the next generation.
            newPopulation.addAll(temp.subList(0, populationSize-1));

            this.population = new Cloner().deepClone(newPopulation);
            epoch++;
        }
        this.plotter.plotChromosome(population.get(0));
        this.plotter.updateUI();
    }

    public static void main(String[] args) {
        GA ga = new GA("p01");
//        ga.initPop(100, false);

        ga.run(100, 1000, 0.6, 0.8);
        ga.printSolution();

    }
}