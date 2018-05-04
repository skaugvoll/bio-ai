package jssp.ACO;


import jssp.DataGenerator;
import jssp.Job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;

public class Ant {

    // schedule | sequence:: is a string representing the order job-tasks was selected.
    // "1,2,3,4,1,1,2,3,4  --> read (job,operation) -->(1,1), (2,1) (3,1) (4,1) (1,2) (1,3) (2,2) (3,2) (4,2)
    private ArrayList<Integer> schedule;

    // consumedTime :
    private int consumedTime;

    // pheromone : list indicating the pheromone strength between operations ??
    private PriorityQueue<Pheromone> pheromones;

    // operations : this should have the pointer to which operation is next for each job.
    private HashMap<Job, Integer> operations;

    // jobs : list which contains all the possible jobs
    private ArrayList<Job> jobs;

    // rg : random generator for selecting next job operation to do
    private Random rg;


    public Ant(ArrayList<Job> jobs){
        this.schedule = new ArrayList<Integer>();
        this.operations = new HashMap<Job, Integer>();
        this.consumedTime = 0;
        this.rg = new Random();
        this.jobs = jobs;
        this.pheromones = new PriorityQueue<Pheromone>(this.jobs.size(), new PrioritySorter());


        // init operations
        for(Job job : this.jobs){
            operations.put(job, 0);
            pheromones.add(new Pheromone(null, -1, job, 0, 0));
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

        // add to the consumedTime, the amount of time used on this operation, remember to think || - and not sequential.
        // TODO: figure out something smart on how to count counsumedTime


        // update the operations index for this job, letting it know that we executed it.
        int currentJobOperation = this.operations.get(currentJob);
        this.operations.put(currentJob, currentJobOperation + 1);

        // update the pheromone level to this job-operation ??
        // TODO: update the pheromone level to this job-operation ??
        for (int i = 0; i < this.pheromones.size(); i++){
            Pheromone p = this.pheromones.poll();
            if (p.job_two == currentJob){
                p.level++;
            }
            // TODO: create new  pheromone level from all the old job-operation to the new jobs  operation ??
            pheromones.add(new Pheromone(p.job_two, p.operation_two, currentJob, currentJobOperation + 1, p.level));
            this.pheromones.add(p);
        }


        // TODO:  (this must be a loop?)  Then the ant iteratively appends an unscheduled operation to the partial sequence constructed so far until a complete solution is built.
        /*
        The ant decides where to go from that node,
        based on probabilities calculated from:
            - pheromone strengths, :: not sure random at first?
            - next-hop distances. :: perhaps operation time?
        */
        while(! checkIfAllJobsAreDone()){

            // Find the job
            currentJob = this.pheromones.poll().job_two;
            jobNumber = currentJob.job_number;

            // remember what operation next to perform
            operation = this.operations.get(currentJob);


            // get the machine which the operation is to be perfomed
            job_operation_machine = currentJob.getOperation_machine_number(operation);
            // find out how long the machine uses on the operation
            job_operation_time = currentJob.getMachineTime(job_operation_machine);

            // add to the schedule that this job-operation is execute
            schedule.add(jobNumber);
            // add to the consumedTime, the amount of time used on this operation, remember to think || - and not sequential.
            // TODO: figure out something smart on how to count counsumedTime


            // update the operations index for this job, letting it know that we executed it.
            currentJobOperation = this.operations.get(currentJob);
            this.operations.put(currentJob, currentJobOperation + 1);

            // update the pheromone level to this job-operation ??
            // TODO: update the pheromone level to this job-operation ??
            for (Pheromone p : this.pheromones){
                if (p.job_two.equals(currentJob)){
                    p.level++;
                }
                // TODO: create new  pheromone level from all the old job-operation to the new jobs  operation ??
                pheromones.add(new Pheromone(p.job_two, p.operation_two, currentJob, currentJobOperation + 1, p.level));
            }

        }

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


    public static void main(String[] args) {
        DataGenerator dg = new DataGenerator(1);

        Ant a = new Ant(dg.getJobs());
        a.generateSolution();
    }




}
