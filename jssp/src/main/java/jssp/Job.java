package jssp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class Job {
    public int job_number;

    /*
        Operations:
        [x][0] = Machine Number
        [x][1] = Time required for the operation
        Following representation in assignment description.
    */
    public int [][] operations;


    public Job(int job_number, int[][] operations){
        this.job_number = job_number;
        this.operations = operations;
    }

    @Override
    public String toString() {
        return "";
    }
}
