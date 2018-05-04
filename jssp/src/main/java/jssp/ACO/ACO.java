package jssp.ACO;

import jssp.Job;
import sun.security.provider.certpath.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ACO {

    private Job[] jobs;
    private int num_jobs, num_machines, total, bestPossibleMakespan;
    private Node root;
    private final ArrayList<Node> vertices = new ArrayList<Node>();
    private AntSolution bestGlobalAntSolution = null;

    private double evaporationRate = 0.1;

    public ACO(Job[] jobs, int num_machines, int num_jobs, int bestPossibleMakespan){
        this.jobs = jobs;
        this.num_machines = num_machines;
        this.num_jobs = num_jobs;
        this.bestPossibleMakespan = bestPossibleMakespan;

        this.total = num_machines * num_jobs; // total number of tasks or operations to be performed

        root = new Node(-1,-1,-1);
        vertices.add(root); // start building the graph / schedule

        root.edges = new Node[num_jobs]; // init the root edges array
        root.pheromones = new double[num_jobs]; // init the root pheromone trails.

        // Make the pheromone trail from root to other jobs.
        for(int i=0; i < num_jobs; i++){
            final int machineNumber = jobs[i].operations[0][0];
            final int timeRequired = jobs[i].operations[0][1];
            final int jobNumber = jobs[i].job_number;
            final Node neighbour = new Node(machineNumber, jobNumber, timeRequired);
            vertices.add(neighbour);
            root.edges[i] = neighbour;
            root.pheromones[i] = 1.0;
        }
    }


    public Solution solve(int num_iterations, int num_ants){
        for(int i = 0; i < num_iterations; i++){
            AntSolution[] solutions = new AntSolution[num_ants];
            for(int ant = 0; ant < num_ants; ant++){
                solutions[ant] = findSolutino();
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

            double delta = 1.0;

            for(Node node: vertices){
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
            for(int j = 0; j < total; j++){
                int index = bestAntSolution.path.get(j);
                current.pheromones[index] += delta;
                current = current.edges[index];
            }

        }
        return  bestGlobalAntSolution.solution;
    }

    private AntSolution findSolutino(){
        int[] visited = new int[num_jobs];
        int[] jobTime = new int[num_jobs];
        int[] machineTime = new int[num_machines];
        int[][][] path = new int[num_machines][num_jobs][2];

        int makespan = 0;

        Node current = root;
        ArrayList<Integer> nodePath = new ArrayList<Integer>();

        while(nodePath.size() != total){
            int index = selectPath(current, jobTime, machineTime, makespan);

            if(index == -1){
                return findSolutino();
            }

            nodePath.add(index);
            current = current.edges[index];
            visited[current.job_number] ++;

            final int machineNumber = current.machine_number;
            final int jobNumber = current.job_number;
            final int timeRequired = current.duration;

            // Start time
            final int startTime = Math.max(jobTime[jobNumber], machineTime[machineNumber]);
            path[machineNumber][jobNumber][0] = startTime;
            // Time required
            path[machineNumber][jobNumber][1] = timeRequired;
            // Updating variables
            final int time = startTime + timeRequired;
            jobTime[jobNumber] = time;
            machineTime[machineNumber] = time;
            if (time > makespan) {
                makespan = time;
            }
            // New Vertex
            if (current.edges == null) {

                // Adding next option
                final ArrayList<Node> choices = new ArrayList<Node>();
                for (int i = 0; i < num_jobs; i ++) {
                    if (visited[i] < num_machines) {
                        final int neighbourMachineNumber = jobs[i].operations[visited[i]][0];
                        final int neighbourTimeRequired = jobs[i].operations[visited[i]][1];
                        final Node neighbour = new Node(neighbourMachineNumber, jobs[i].job_number, neighbourTimeRequired);
                        choices.add(neighbour);
                        vertices.add(neighbour);
                    }
                }
                current.edges = new Node[choices.size()];
                current.pheromones = new double[current.edges.length];
                choices.toArray(current.edges);
                Arrays.fill(current.pheromones, 1.0);
            }
        }

        return new AntSolution(new Solution(path), nodePath, makespan);
    }

    private int selectPath(Node current, int[] jobTime, int[] machineTime, int makespan) {

        double a = 1, b = 1;
        double denominator = 0;
        final double[] probability = new double[current.edges.length];
        for (int i = 0; i < probability.length; i ++) {
            probability[i] = Math.pow(current.pheromones[i], a) * Math.pow((heuristic(current.edges[i], jobTime, machineTime, makespan)), b);
            denominator += probability[i];
        }

        if (denominator == 0.0) {
            Random random = new Random();
            return random.nextInt(current.edges.length);
        }

        double cumulativeProbability = 0;
        double threshold = Math.random();
        for (int i = 0; i < current.edges.length; i ++) {
            cumulativeProbability += probability[i] / denominator;
            if (threshold <= cumulativeProbability) {
                return i;
            }
        }

        return -1;
    }

    private synchronized double heuristic(Node node, int[] jobTime, int[] machineTime, int makespan) {

        final int startTime = Math.max(jobTime[node.job_number], machineTime[node.machine_number]);
//        heuristic =  1.0 / Math.max(startTime + vertex.timeRequired, makespan);

        double heuristic = makespan - (startTime + node.duration);
        if (heuristic < 0.0) {
            return 0;
        }

        return heuristic;
    }
}

        




