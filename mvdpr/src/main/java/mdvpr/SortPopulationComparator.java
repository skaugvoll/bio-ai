package mdvpr;

import java.util.Comparator;

public class SortPopulationComparator implements Comparator<Chromosome> {

    @Override
    public int compare(Chromosome o1, Chromosome o2) {
        if (o1.getFitness() < o2.getFitness())
            return -1;
        else if (o1.getFitness() > o2.getFitness())
            return 1;
        else
            return 0;

//        return (int) (o1.getFitness() - o2.getFitness());
    }
}
