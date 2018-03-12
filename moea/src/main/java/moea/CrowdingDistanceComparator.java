package moea;

import java.util.Comparator;

public class CrowdingDistanceComparator implements Comparator<Chromosome> {

    @Override
    public int compare(Chromosome c1, Chromosome c2) {
        return (int)(c2.crowdingDistance - c1.crowdingDistance);
    }
}
