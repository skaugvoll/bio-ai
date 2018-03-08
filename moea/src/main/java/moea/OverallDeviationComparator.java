package moea;

import java.util.Comparator;

public class OverallDeviationComparator implements Comparator<Chromosome> {

    @Override
    public int compare(Chromosome c1, Chromosome c2) {
        return (int)(c2.overallDeviation - c1.overallDeviation);
    }
}
