package jssp.BA;

import jssp.Job;
import jssp.Node;
import jssp.Solution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Bee {

    private Job[] jobs;
    private int num_jobs, num_machines, total;
    private final Random random;

    public Bee(Job[] jobs, int num_jobs, int num_machines, int total) {
        this.jobs = jobs;
        this.num_jobs = num_jobs;
        this.num_machines = num_machines;
        this.total = total;
        this.random = new Random();
    }


    BeeSolution findSolution(BeeSolution beeSolution, int neighbourhood, ArrayList<Node> nodes) {

        final int[] visited = new int[num_jobs];
        final int[] jobTime = new int[num_jobs];
        final int[] machineTime = new int[num_machines];
        final int[][][] path = new int[num_machines][num_jobs][2];

        int makespan = 0;

        Node current = nodes.get(0);
        final ArrayList<Integer> nodePath = new ArrayList<>();

        //if we are performing neighbourhood search, do this first
        if(beeSolution != null){
            for(int k = 0; k<beeSolution.path.size() - neighbourhood;k++){
                nodePath.add(beeSolution.path.get(k));
                current = current.edges[beeSolution.path.get(k)];
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
            }
        }

        while (nodePath.size() != total) {

            //Selecting a path
            final int index = selectPath(current, jobTime, machineTime, makespan);

            //Fixing random exception
            if (index == -1) {
                return findSolution(null,0, nodes);
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
                final ArrayList<Node> choices = new ArrayList<>();
                for (int i = 0; i < num_jobs; i ++) {
                    if (visited[i] < num_machines) {
                        final int neighbourMachineNumber = jobs[i].operations[visited[i]][0];
                        final int neighbourTimeRequired = jobs[i].operations[visited[i]][1];
                        final Node neighbour = new Node(neighbourMachineNumber, jobs[i].job_number, neighbourTimeRequired);
                        choices.add(neighbour);
                        nodes.add(neighbour);
                    }
                }
                current.edges = new Node[choices.size()];
                choices.toArray(current.edges);
            }
        }

        return new BeeSolution(new Solution(path), nodePath, makespan);
    }

    public int selectPath(Node current, int[] jobTime, int[] machineTime, int makespan) {

        double a = 1.0, b = 1.0;
        double denominator = 0;
        final double[] probability = new double[current.edges.length];
        for (int i = 0; i < probability.length; i ++) {
            probability[i] = /*Math.pow(current.pheromones[i], a) */ Math.pow((heuristic(current.edges[i], jobTime, machineTime, makespan)), b);
            denominator += probability[i];
        }

        if (denominator == 0.0) {
            //Random random = new Random();
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


    private double heuristic(Node node, int[] jobTime, int[] machineTime, int makespan) {
        final int startTime = Math.max(jobTime[node.job_number], machineTime[node.machine_number]);

        double heuristic = makespan - (startTime + node.duration);
        if (heuristic < 0.0) {
            return 0;
        }

        return heuristic;
    }

}
