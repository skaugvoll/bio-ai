package jssp.ACO;

import jssp.Job;

import java.util.ArrayList;

public class ACO {

    private Job[] jobs;
    private int num_jobs, num_machines, total, bestPossibleMakespan;
    private Pheromone root;
    private final ArrayList<Pheromone> vertices = new ArrayList<Pheromone>();

    private double evaporationRate = 0.1;

    public ACO(Job[] jobs, int num_machines, int num_jobs, int bestPossibleMakespan){
        this.jobs = jobs;
        this.num_machines = num_machines;
        this.num_jobs = num_jobs;
        this.bestPossibleMakespan = bestPossibleMakespan;

        this.total = num_machines * num_jobs; // total number of tasks or operations to be performed

        root = new Pheromone(-1,-1,-1);
        vertices.add(root); // start building the graph / schedule

        root.edges = new Pheromone[num_jobs]; // init the root edges array
        root.pheromones = new double[num_jobs]; // init the root pheromone trails.

        // Make the pheromone trail from root to other jobs.
        for(int i=0; i < num_jobs; i++){
            final int machineNumber = jobs[i].operations[0][0];
            final int timeRequired = jobs[i].operations[0][1];
            final int jobNumber = jobs[i].job_number;
            final Pheromone neighbour = new Pheromone(machineNumber, jobNumber, timeRequired);
            vertices.add(neighbour);
            root.edges[i] = neighbour;
            root.pheromones[i] = 1.0;
        }
    }


    public Solution solve(int num_iterations, int num_ants){

        


    }




}
