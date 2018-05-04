package jssp.ACO;

import java.util.ArrayList;

public class AntSolution {
    final Solution solution;
    final ArrayList<Integer> path;
    final int makespan;

    public AntSolution(Solution solution, ArrayList<Integer> path, int makespan) {
        this.solution = solution;
        this.path = path;
        this.makespan = makespan;
    }
}
