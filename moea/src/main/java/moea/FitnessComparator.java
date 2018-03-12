package moea;

import java.util.Comparator;

public class FitnessComparator implements Comparator<Chromosome> {

    @Override
    public int compare(Chromosome c1, Chromosome c2) {
        return (int)(c1.fitness - c2.fitness);
    }
}
