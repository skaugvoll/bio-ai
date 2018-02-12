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

    private void initPop(int popSize){

        while(this.population.size() < popSize){
            ArrayList<Vehicle> cars = new ArrayList<>();

            for(Customer customer : this.customers){
                Depot nearestDepot = null;
                double closestDistance = Double.MAX_VALUE;

                for(Depot depot : this.depots){
                    double tempDist = this.getEuclideanDistance(customer.getXpos(), customer.getYpos(), depot.getXpos(), depot.getYpos());
                    if(tempDist < closestDistance){
                        nearestDepot = depot;
                        closestDistance = tempDist;
                    }

                }

                int v = r.nextInt(nearestDepot.getVehicles().size());
                Vehicle vehicle = nearestDepot.getVehicle(v);
                vehicle.addCustomer(customer);

//                while(!this.isValidRoute(vehicle)){
//                    vehicle.getPath().remove(customer);
//                    vehicle.setCurrentDuration();
//                    if(vehicle.getPath().size() < 1){
//                        vehicle.setxyPos(vehicle.getDepo().getXpos(), vehicle.getDepo().getYpos());
//                    }
//                    else{
//                        vehicle.setxyPos(vehicle.getPath().get(vehicle.getPath().size()-1).getXpos(), vehicle.getPath().get(vehicle.getPath().size()-1).getYpos());
//                    }
//                    v = r.nextInt(nearestDepot.getVehicles().size());
//                    vehicle = nearestDepot.getVehicle(v);
//                    vehicle.addCustomer(customer);
//                }
            }

            addCarsToSolutionList(cars);


            Cloner cloner = new Cloner();
            ArrayList<Vehicle> carsCloned = cloner.deepClone(cars);

            Chromosome chromosome = new Chromosome(carsCloned);
            this.calculateFitness(chromosome); // calculate the fitness score for this chromosome
            population.add(chromosome);// add chromosome to population
            resetCars(cars);
        }

//        this.plotter.plotChromosome(population.get(0));
//        this.plotter.updateUI();
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
//            if(v.getCurrentDuration() > v.getMaxDuration()){
//                System.out.println("Current duration: " + v.getCurrentDuration() + " Max duration: " + v.getMaxDuration());
//            }
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
        ArrayList<Chromosome> parents = new ArrayList<>();
        parents.add(this.population.get(0)); // 0 is the best

        //number of candidates = 2 gives binary tournament.
        ArrayList<Chromosome> candidates = new ArrayList<>();
        for(int i = 0; i < numberOfCandidates; i++){
            candidates.add(this.population.get(r.nextInt(this.population.size())));
        }
        Collections.sort(candidates, new SortPopulationComparator());

        double k = r.nextDouble();
        double prob = 0.8;
        if (k <= prob){
            parents.add(candidates.get(0));
        } else {
            parents.add(candidates.get(r.nextInt(candidates.size())));
        }

        return parents;
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
                vehic.getPath().remove(cust);
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
            this.mutation(temp);
        }
        this.calculateFitness(temp);
        return temp;
    }

    private void mutation(Chromosome offspring){

        if(offspring.getCars().size() < 1) {
            return;
        }

        Vehicle vehicleOne = offspring.getCars().get(r.nextInt(offspring.getCars().size()));


        if(vehicleOne.getPath().size() > 1){
            int custIndex = r.nextInt(vehicleOne.getPath().size());
            Customer cust = vehicleOne.getPath().remove(custIndex);
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

    public void run(int populationSize, int maxEphochs, double crossoverRate, double mutationRate){
        this.initPop(populationSize); // 2 & 3. this also evaluates the fitness
        int epoch = 0; // 1.

        while(epoch < maxEphochs){ // 4

            ArrayList<Chromosome> newPopulation = new ArrayList<>();
            ArrayList<Chromosome> parents = selectParents(2); // 5. select parents
            newPopulation.add(this.population.get(0)); //:: ELITISM ; best is always taken to the next generation.

            System.out.println("population: " + epoch + " :: " + population.get(0).getFitness());

            // basically create all offsprings and crossover them.
            while(newPopulation.size() < population.size()){
                // 6. Crossover and // 7. mutation on offspring
                newPopulation.add(this.crossover(parents.get(0), parents.get(1), crossoverRate, mutationRate));
                if(newPopulation.size() == population.size()) {
                    break;
                }
                newPopulation.add(this.crossover(parents.get(1), parents.get(0), crossoverRate, mutationRate));
            }
            this.population = new Cloner().deepClone(newPopulation);
            epoch ++;
        }
        this.plotter.plotChromosome(population.get(0));
        this.plotter.updateUI();
    }

    public static void main(String[] args) {
        GA ga = new GA("p01");
//        ga.initPop(10);
        ga.run(100, 1000, 0.6, 1);
        ga.printSolution();

    }
}
