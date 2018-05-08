package jssp.BA;

import jssp.Solution;
import java.util.ArrayList;

public class BeeSolution {
    public final Solution solution;
    public final ArrayList<Integer> path;
    public final int makespan;
    public int neighbourhood;

    public BeeSolution(Solution solution, ArrayList<Integer> path, int makespan) {
        this.solution = solution;
        this.path = path;
        this.makespan = makespan;
    }
}
