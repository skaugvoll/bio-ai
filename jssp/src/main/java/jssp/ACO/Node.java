package jssp.ACO;

public class Node {

    int machine_number;
    int job_number;
    int duration;

    Node[] edges;
    double[] pheromones;



    public Node(int mn, int jn, int dur){
        this.machine_number = mn;
        this.job_number = jn;
        this.duration = dur;
    }


}
