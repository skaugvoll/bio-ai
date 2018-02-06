package mdvpr;

import java.util.Comparator;
import java.util.Random;

public class WeightPopulationComparator implements Comparator<Chromosome> {

    Random r = new Random();

    @Override
    public int compare(Chromosome o1, Chromosome o2) {
        return (int) (o1.getFitness() * r.nextDouble() *  - o2.getFitness() * r.nextDouble());
    }
}
