package jssp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class DataGenerator {
    private int task;
    private int num_jobs;
    private int num_machines;

    Job[] jobs;

    public DataGenerator(int task){
        this.task = task;
        String filename = "/Test_Data/"+this.task+".txt";

        try{
            InputStream in = getClass().getResourceAsStream(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = reader.readLine().trim();
            String[] meta = line.split("\\s+");
            num_jobs = Integer.valueOf(meta[0]);
            num_machines = Integer.valueOf(meta[1]);

            jobs = new Job[num_jobs];


            for(int i = 0; i < num_jobs; i++){
                line = reader.readLine().trim();
                String[] data = line.split("\\s+");

                int[][] operations = new int[num_machines][2];
                for(int j = 0; j < num_machines; j++){
                    int index = j * 2;
                    operations[j][0] = Integer.valueOf(data[index]);
                    operations[j][1] = Integer.valueOf(data[index + 1]);
                }

                jobs[i] = new Job(i, operations);
            }

        }
        catch (Exception e){
            System.out.println("exception: " + e);
        }

    }

    public static void main(String[] args) {
        DataGenerator dg = new DataGenerator(1);
    }

    public Job[] getJobs() {
        return jobs;
    }

    public int getNumMachines() {
        return this.num_machines;
    }

    public int getNumJobs() {
        return num_jobs;
    }
}