## Def initPop()...

// one solution = individual is; all customers are covered by a vehicle

        while(this.population.size() < popSize){
            ArrayList<Vehicle> cars = new ArrayList<>();
            cars.clear(); // make sure we start from scratch on new solution / individual / chromosome
            boolean foundSolution= true;
            for(Customer customer : this.customers){
                // Choose a random depot
                int d = r.nextInt(this.depots.size()); // from 0 to but not included size = 4 --> 0, 1, 2, 3
                Depot depot = this.depots.get(d);

                // Choose a random vehicle
                int v = r.nextInt(depot.getVehicles().size());
                Vehicle vehicle = depot.getVehicle(v);

                // add the customer to the random chosen car.
                vehicle.addCustomer(customer);
                int attempts = 0;

                while(!this.isValidRoute(vehicle)){
                    if(attempts == 15){
                        foundSolution = false;
                        break;
                    }
                    vehicle.getPath().remove(customer);
                    vehicle.setCurrentDuration();
                    if(vehicle.getPath().size() < 1){
                        vehicle.setxyPos(vehicle.getDepo().getXpos(), vehicle.getDepo().getYpos());
                    }
                    else{
                        vehicle.setxyPos(vehicle.getPath().get(vehicle.getPath().size()-1).getXpos(), vehicle.getPath().get(vehicle.getPath().size()-1).getYpos());
                    }
                    // Choose a random depot
                    d = r.nextInt(this.depots.size()); // from 0 to but not included size = 4 --> 0, 1, 2, 3
                    depot = this.depots.get(d);

                    // Choose a random vehicle
                    v = r.nextInt(depot.getVehicles().size());
                    vehicle = depot.getVehicle(v);

                    // add the customer to the random chosen car.
                    vehicle.addCustomer(customer);
                    attempts++;
                }
                if(! foundSolution){
                    break;
                }
            }



            // add all cars to the list -- make solution
            for(Depot d : this.depots){
                for(Vehicle v : d.getVehicles()){
                    if(!cars.contains(v)){
                        cars.add(v);
                    }
                }
            }
//
//            if(! foundSolution){
//                resetCars(cars);
//                continue;
//            }

            // now we have found one solution (good or bad | legal and or illegal)
            // create chromosome.

            // now we need a deep copy, not just refrences. since we are goint to "reset" our objects for another population to use.
            // TODO: Rewrite this into a create new object for each object method. Much more performance pleasing.
            Cloner cloner = new Cloner();
            ArrayList<Vehicle> carsCloned = cloner.deepClone(cars);

            Chromosome chromosome = new Chromosome(carsCloned);
            this.calculateFitness(chromosome); // calculate the fitness score for this chromosome
            population.add(chromosome);// add chromosome to population
            resetCars(cars);


        }
//        this.plotter.plotChromosome(population.get(0));
