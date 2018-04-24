package jssp.ACO;


import jssp.Job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Ant {

    // schedule | sequence:: is a string representing the order job-tasks was selected.
    // "1,2,3,4,1,1,2,3,4  --> read (job,operation) -->(1,1), (2,1) (3,1) (4,1) (1,2) (1,3) (2,2) (3,2) (4,2)
    private ArrayList<Integer> schedule;

    // pheromone : list indicating the pheromone strength between jobs or operations ?? will be quite many
    // TODO: implement some smart way to represent the pheromone

    // operations : this should have the pointer to which operation is next for each job.
    private HashMap<Job, Integer> operations;

    // jobs : list which contains all the possible jobs
    private ArrayList<Job> jobs;

    // rg : random generator for selecting next job operation to do
    private Random rg;


    public Ant(ArrayList<Job> jobs){
        this.schedule = new ArrayList<Integer>();
        this.operations = new HashMap<Job, Integer>();
        this.rg = new Random();
        this.jobs = jobs;


        // init operations
        for(Job job : this.jobs){
            operations.put(job, 0);
        }
    }


    public void generateSolution(){
        // TODO: Each artifical ant starts with an empty sequence and chooses one of the jobs
        int jobNumber = rg.nextInt(this.jobs.size());

        // Find the job
        Job currentJob = this.jobs.get(jobNumber);
        // remember what operation next to perform
        int operation = this.operations.get(currentJob);


        // get the machine which the operation is to be perfomed
        int job_operation_machine = currentJob.getOperation_machine_number(operation);
        // find out how long the machine uses on the operation
        int job_operation_time = currentJob.getMachineTime(job_operation_machine);

        // add to the schedule that this job-operation is execute
        schedule.add(jobNumber);
        // update the operations index for this job, letting it know that we executed it.
        this.operations.put(currentJob, this.operations.get(currentJob) + 1);

        // TODO: Then the ant iteratively appends an unscheduled job to the partial sequence constructed so far until a complete solution is built.
        /*T
        he ant decides where to go from that node,
        based on probabilities calculated from:
            - pheromone strengths, :: not sure random at first?
            - next-hop distances. :: perhaps operation time?
        */


    }


    private boolean checkIfAllJobsAreDone(){
        int done = 0;
        for(Job job : this.jobs){
            int operationsExecuted = this.operations.get(job);
            if (operationsExecuted == job.getOrder().size()){
                done++;
            }
        }
        return done == this.jobs.size();
    }





}
