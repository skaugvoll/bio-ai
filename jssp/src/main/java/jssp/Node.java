package jssp;

public class Node {

    public int machine_number;
    public int job_number;
    public int duration;

    public Node[] edges;
    public double[] pheromones;



    public Node(int mn, int jn, int dur){
        this.machine_number = mn;
        this.job_number = jn;
        this.duration = dur;
    }


}
