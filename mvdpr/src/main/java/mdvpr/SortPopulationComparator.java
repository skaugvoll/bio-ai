package mdvpr;

import java.util.Comparator;

public class SortPopulationComparator implements Comparator<Chromosome> {

    @Override
    public int compare(Chromosome o1, Chromosome o2) {
        return (int) (o1.getFitness() - o2.getFitness());
    }
}
