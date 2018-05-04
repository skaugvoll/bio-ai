package jssp.ACO;

import java.util.Comparator;

public class PrioritySorter implements Comparator<Pheromone> {
    public int compare(Pheromone o1, Pheromone o2) {
        return Double.compare(o1.getPriority(), o2.getPriority());
    }
}
