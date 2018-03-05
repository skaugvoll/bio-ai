package moea;

import java.awt.*;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Chromosome {
    // In most cases, pixels are stored as corresponding color values (RGB or CIE L*a*b as the color space [1]).
    MST mst;
    int numberOfPixels;
    int minSegments;

    ArrayList<Pixel> rootNodes;
    ArrayList<Segment> segments;

    public Chromosome(MST mst, int numberOfPixels, int minSegments){
        this.mst = mst;
        this.numberOfPixels = numberOfPixels;
        this.minSegments = minSegments;

        rootNodes = new ArrayList<>();
        this.segments = new ArrayList<>();

        this.generateSegments();
        this.concatenateSegments();
        System.out.println("faen");
        DataGenerator dg = new DataGenerator();
        dg.drawSegments(this.segments);


    }


    private void generateSegments() {
        ArrayList<Pixel> foundNewSegment = new ArrayList<>();

        double teta = 50;

        Pixel root = mst.rootnode;
        foundNewSegment.add(root);

        for (int i = 0; i < foundNewSegment.size(); i++) {
            root = foundNewSegment.get(i);
            ArrayList<Pixel> foundThisSegment = new ArrayList<>();
            ArrayList<Edge> foundThisEdges = new ArrayList<>();
            
            Segment s = new Segment(root, new Color(new Random().nextInt(256),new Random().nextInt(256),new Random().nextInt(256)));

            divideIntoSegment(s, foundNewSegment, teta, root, foundThisSegment, foundThisEdges);
            // nå har vi funent alle "rettninger ut av rootnoden for dette segmentet. nå vil vi følge så langt som mulig (så langt tetta lar oss)
            for(int n = 0; n < foundThisSegment.size(); n++){
                Pixel np = foundThisSegment.get(n);
                divideIntoSegment(s, foundNewSegment, teta, np, foundThisSegment, foundThisEdges);
            }

            // Nå har vi funnet det vi trenger til segmentet.
//            s.addAllPixels(foundThisSegment);
            s.addAllEdges(foundThisEdges);
            this.segments.add(s);
        }
    }

    private void divideIntoSegment(Segment s, ArrayList<Pixel> foundNewSegment, double teta, Pixel root, ArrayList<Pixel> foundThisSegment, ArrayList<Edge> foundThisEdges) {
        int j = 0;

        while (j < mst.edges.size()) {
            ValueRange range = ValueRange.of((long) (s.avgSegCol - teta), (long) (s.avgSegCol + teta));
            Edge e = mst.edges.get(j);
            if (e.getCurrentPixel() == root) {
                mst.edges.remove(e);
//                if (e.getDistance() <= teta) {
                if (range.isValidValue((long) IntStream.of(e.getNeighbourPixel().getRGB()).sum())) {
                    foundThisSegment.add(e.getNeighbourPixel());
//                    System.out.println("FOUND ANOTHER ONE");
                    s.addPixel(e.getNeighbourPixel());
                    foundThisEdges.add(e);
                } else {
                    foundNewSegment.add(e.getNeighbourPixel());
//                    System.out.println("ANOTHER ONE BITES THE DUST");
                }
            }
            else{
                j++;
            }
        }
    }

    private void concatenateSegments(){
        // burde kansje bruke en form for k-nearest Neighbours tror det er ett bra utg.punkt.
        System.out.println("Now concatenating...");
        int minPixels = 1000;

        List<Segment> segs = this.segments.stream().filter(
                segment -> segment.getSegmentSize() < minPixels
        ).collect(Collectors.toList());

        int i = 0;
        while(! segs.isEmpty() && i < segs.size()-1){
            int temp = 0;
            Segment s1 = segs.get(i);

            temp += s1.getSegmentSize();

            int j = 1;
            while(j < segs.size()-1 && temp < minPixels){
                Segment s2 = segs.get(j);
                if(temp + s2.getSegmentSize() < minPixels){
                    temp += s2.getSegmentSize();
                    s1.addAllPixels(s2.pixels);
                    s1.addAllEdges(s2.edges);


                    segs.remove(s2);
                    this.segments.remove(s2); // hmm. er dette bra mon tro?

                }
                else{
                    j++;
                }
            }
            i++;
        }
    }

}
