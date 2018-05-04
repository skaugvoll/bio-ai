package jssp.ACO;

import java.util.Arrays;

public class Solution {

    /*
        schedule:
        [machineNumber][jobNumber][2]
        [X][Y][0] = Start Time
        [X][Y][1] = Time Required
    */
    private final int[][][] schedule;
    private final int makespan;

    Solution(int[][][] schedule) {
        this.schedule = schedule;

        int max = Integer.MIN_VALUE;
        for (int i = 0; i < schedule.length; i ++) {
            for (int j = 0; j < schedule[0].length; j ++) {
                final int value = schedule[i][j][0] + schedule[i][j][1];
                if (value > max) {
                    max = value;
                }
            }
        }
        makespan = max;
    }

    public int getMakespan() {
        return makespan;
    }

    public int[][][] getSchedule() {
        return schedule;
    }


}
