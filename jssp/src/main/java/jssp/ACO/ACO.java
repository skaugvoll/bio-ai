package jssp.ACO;

import jssp.Job;
import jssp.Node;
import jssp.Solution;

import java.util.ArrayList;


public class ACO {


    private int total;
    Ant[] ants;
    private AntSolution bestGlobalAntSolution = null;
    private final ArrayList<Node> nodes = new ArrayList<Node>();
    private Node root;

    private double evaporationRate = 0.1;

    public ACO(Job[] jobs, int num_machines, int num_jobs, int num_ants){
        ants = new Ant[num_ants];
        this.total = num_machines * num_jobs; // total number of tasks or operations to be performed
        // Make the pheromone trail from root to other jobs.
        root = new Node(-1,-1,-1);
        nodes.add(root); // start building the graph / schedule

        root.edges = new Node[num_jobs]; // init the root edges array
        root.pheromones = new double[num_jobs]; // init the root pheromone trails.


        for(int i=0; i < num_jobs; i++){
            final int machineNumber = jobs[i].operations[0][0];
            final int timeRequired = jobs[i].operations[0][1];
            final int jobNumber = jobs[i].job_number;
            final Node neighbour = new Node(machineNumber, jobNumber, timeRequired);
            nodes.add(neighbour);
            root.edges[i] = neighbour;
            root.pheromones[i] = 1.0;
        }
        for(int i = 0; i < num_ants; i++){
            ants[i] = new Ant(jobs, num_jobs, num_machines, total);
        }


    }


    public Solution solve(int num_iterations){
        for(int i = 0; i < num_iterations; i++){
            AntSolution[] solutions = new AntSolution[ants.length];
            for(int ant = 0; ant < ants.length; ant++){
                solutions[ant] = ants[ant].findSolution(nodes);
            }

            int bestMakeSpan = Integer.MAX_VALUE;
            AntSolution bestAntSolution = null;
            for(AntSolution solution: solutions){
                if(bestAntSolution == null || bestMakeSpan > solution.makespan){
                    bestAntSolution = solution;
                    bestMakeSpan = solution.makespan;
                }
            }

            if(bestGlobalAntSolution == null || bestGlobalAntSolution.makespan > bestAntSolution.makespan){
                bestGlobalAntSolution = bestAntSolution;
            }


            for(Node node: nodes){
                if(node.edges != null){
                    for(int j = 0; j < node.edges.length; j++){
                        if(node.pheromones[j] == 0.0){
                            continue;
                        }
                        node.pheromones[j] *= (1.0 - evaporationRate);
                    }
                }
            }

            Node current = root;
            double delta = 1.0;
            double low = Double.MAX_VALUE;
            double high = Double.MIN_VALUE;

            for(int j = 0; j < total; j++){
                int index = bestAntSolution.path.get(j);
                double phr =  current.pheromones[index];
                if(phr <  low){
                    low = phr;
                }
                else if(phr > high){
                    high = phr;
                }

//                current.pheromones[index] += delta;
//                current = current.edges[index];
            }
            for(int j = 0; j < total; j++){
                int index = bestAntSolution.path.get(j);
                double phr = current.pheromones[index];
                Node e = current.edges[index];
                double np = (1 - evaporationRate) * e.duration + 1 / bestMakeSpan;
                if (np > high) {
                    np = high;
                }
                else if(np < low) {
                    np = low;
                }
                current.pheromones[index] = np;
            }

        }
        System.out.println("#nodes : " + this.nodes.size());
        return  bestGlobalAntSolution.solution;
    }

}

        




