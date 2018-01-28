package mdvpr;

import java.util.ArrayList;

public class GA {

    private Plot plotter;
    private ArrayList<Depot> depots = new ArrayList<Depot>();

    public GA() {
        this.plotter = new Plot();
        DataGenerator data = new DataGenerator("p01");
        this.depots = data.getDepots();
        plotter.plot(depots);
    }

    public static void main(String[] args) {
        GA ga = new GA();


    }
}
