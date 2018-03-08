package moea;

import java.util.Comparator;

public class EdgeValueComparator implements Comparator<Chromosome> {

    @Override
    public int compare(Chromosome c1, Chromosome c2) {
        return (int)(c1.edgeValue - c2.edgeValue);
    }
}
