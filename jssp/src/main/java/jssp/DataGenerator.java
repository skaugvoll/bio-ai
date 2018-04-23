package jssp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;


public class DataGenerator {
    private int task;
    private int num_jobs;
    private int num_machines;

    private ArrayList<Job> jobs = new ArrayList<Job>();

    public DataGenerator(int task){
        this.task = task;
        String filename = "/Test_Data/"+this.task+".txt";

        try{
            InputStream in = getClass().getResourceAsStream(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line = null;
            int line_number = 0;
            while((line = reader.readLine()) != null){
                if (line.equals("")){
                    continue;
                }
                line = line.trim();

                String[] line_split = line.split(" ");
                ArrayList<Integer> converted_line = new ArrayList<Integer>();

                for(String s : line_split){
                    s = s.replaceAll(" ", "");
                    if(s.length() > 0){
                        converted_line.add(Integer.parseInt(s));
                    }
                }


                if (line_number == 0){
                    int found = 1;

                    for(int chr : converted_line){
                            if(found == 1){
                                this.num_jobs = chr;
                                found++;
                            } else {
                                this.num_machines = chr;
                            }
                        }
                    line_number++;
                    continue;
                    }

//              PROCESS line (machine, time)
                Job job = new Job(line_number);
                int counter = 1;
                int machine = -1;
                for(int chr : converted_line){
                    if (counter % 2 == 0 && machine != -1) { // processingtime
                        job.processingMap.put(machine, chr);
                    } else{
                        job.order.add(chr);
                        machine = chr;
                    }
                    counter++;
                }
                this.jobs.add(job);
                job.generateOrderedProcessingTimes();

                line_number++;
            }

            System.out.println("All jobs:" + this.jobs);

        }
        catch (Exception e){
            System.out.println("exception: " + e);
        }

    }


    public ArrayList<Job> getJobs(){
        return this.jobs;
    }


    public static void main(String[] args) {
        DataGenerator dg = new DataGenerator(1);
    }

}