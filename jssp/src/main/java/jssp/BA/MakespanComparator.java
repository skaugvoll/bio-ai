package jssp.BA;

import java.util.Comparator;

public class MakespanComparator implements Comparator<BeeSolution>{

    @Override
    public int compare(BeeSolution x, BeeSolution y){
        if (x.makespan > y.makespan)
        {
        return 1;
        }
        if (x.makespan < y.makespan)
        {
        return -1;
        }
        return 0;
    }
}
