package jssp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class Job {
    int job_number;
    int [][] operations;


    public Job(int job_number, int[][] operations){
        this.job_number = job_number;
        this.operations = operations;
    }

    @Override
    public String toString() {
        return "";
    }
}
