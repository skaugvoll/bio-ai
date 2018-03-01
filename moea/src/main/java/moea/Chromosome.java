package moea;

import java.util.ArrayList;
import java.util.Random;

public class Chromosome {
    // In most cases, pixels are stored as corresponding color values (RGB or CIE L*a*b as the color space [1]).
    ArrayList<Pixel> mst;
    ArrayList<Pixel> segments;
    int numberOfPixels;
    int minSegments;

    public Chromosome(){

    }

    public Chromosome(ArrayList<Pixel> mst, int numberOfPixels, int minSegments){
        this.mst = mst;
        this.segments = new ArrayList<>();
        this.numberOfPixels = numberOfPixels;
        this.minSegments = minSegments;

        this.generateSegments();

    }

    private void generateSegments(){

    }

}
