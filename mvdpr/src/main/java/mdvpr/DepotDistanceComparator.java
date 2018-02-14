package mdvpr;

import java.util.Comparator;
import java.util.Random;

public class DepotDistanceComparator implements Comparator<Depot> {


    @Override
    public int compare(Depot d1, Depot d2) {
        return (int) (d1.getCurrentDistance() - d2.getCurrentDistance());
    }
}
