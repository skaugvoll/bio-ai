package jssp;

import jssp.ACO.ACO;
import jssp.ACO.Solution;

import java.util.Arrays;

public class Main {

    public static void greetings(){
        System.out.println("Hello jssp, lets get you solved");
    }

    public static void main(String[] args) {
        Main.greetings();

        DataGenerator dg = new DataGenerator(1);
        Job[] jobs = dg.getJobs();

        int num_machines = dg.getNumMachines();
        int num_jobs = dg.getNumJobs();

        Solution solution = new ACO(jobs, num_machines, num_jobs,  56).solve(10, 100);
        System.out.println(solution.getMakespan());
    }

}
