package jssp.ACO;

import jssp.Job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Ant {
    private Job[] jobs;
    private int num_jobs, num_machines, total;

    public Ant(Job[] jobs, int num_jobs, int num_machines, int total) {
        this.jobs = jobs;
        this.num_jobs = num_jobs;
        this.num_machines = num_machines;
        this.total = total;

    }

    public AntSolution findSolution(ArrayList<Node> vertices){
        int[] visited = new int[num_jobs];
        int[] jobTime = new int[num_jobs];
        int[] machineTime = new int[num_machines];
        int[][][] path = new int[num_machines][num_jobs][2];

        int makespan = 0;

        Node current = vertices.get(0); //first element is root-node
        ArrayList<Integer> nodePath = new ArrayList<Integer>();

        while(nodePath.size() != total){
            int index = selectPath(current, jobTime, machineTime, makespan);

            if(index == -1){
                return findSolution(vertices);
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

        double heuristic = makespan - (startTime + node.duration);
        if (heuristic < 0.0) {
            return 0;
        }

        return heuristic;
    }
}
