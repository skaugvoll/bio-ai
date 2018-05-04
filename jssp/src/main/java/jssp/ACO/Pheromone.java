package jssp.ACO;

import jssp.Job;

public class Pheromone {

    public Job job_one;
    public int operation_one;

    public Job job_two;
    public int operation_two;

    public double level;

    public Pheromone(Job jo, int oo, Job jt, int ot, double level) {
        this.job_one = jo;
        this.operation_one = oo;
        this.operation_two = ot;
        this.level = level;
    }

    public double getPriority() {
        return this.level;
    }
}
