package jssp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class Job {
    int job_number;
    ArrayList<Integer> order = new ArrayList<Integer>();
    HashMap<Integer, Integer> processingMap = new HashMap<Integer, Integer>();
    int[] processingtimesOrdered;


    public Job(int job_number){
        this.job_number = job_number;
        
    }

    public void generateOrderedProcessingTimes(){
        this.processingtimesOrdered = new int[order.size()];
        // we know that machines starts with 0 -->
        for(int i = 0; i < this.order.size(); i++){
            this.processingtimesOrdered[i] = this.processingMap.get(i);
        }

    }
    
    
    public int getOperation_machine_number(int number){
        return this.order.get(number);
    }

    public int getMachineTime(int machineID){
        return this.processingMap.get(machineID);
    }
    
    


    @Override
    public String toString() {
        return String.format("" +
                "\nJob #%d" +
                "\nMachine-Order: %s" +
                "\nProcessingMap: %s" +
                "\nProcessingTimes-Ordered: %s" +
                "\n",
                this.job_number,
                Arrays.toString(this.order.toArray()),
                this.processingMap,
                Arrays.toString(processingtimesOrdered)
        );
    }

    public ArrayList<Integer> getOrder() {
        return order;
    }
}
