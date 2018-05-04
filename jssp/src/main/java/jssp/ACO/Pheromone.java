package jssp.ACO;

public class Pheromone {

    int machine_number;
    int job_number;
    int duration;

    Pheromone[] edges;
    double[] pheromones;



    public Pheromone(int mn, int jn, int dur){
        this.machine_number = mn;
        this.job_number = jn;
        this.duration = dur;
    }


}
