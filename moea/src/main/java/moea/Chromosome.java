package moea;

import java.awt.*;
import java.time.temporal.ValueRange;
import java.util.*;
import java.util.List;
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

        double teta = 25;

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

        ValueRange range = ValueRange.of((long) (s.avgSegCol - teta), (long) (s.avgSegCol + teta));
        if(mst.pixelEdges.containsKey(root)){
            ArrayList<Edge> edges = mst.pixelEdges.get(root);
            for(Edge e : edges){

                mst.edges.remove(e);
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
            mst.pixelEdges.remove(root);
        }

    }

    private void concatenateSegments() {
        // burde kansje bruke en form for k-nearest Neighbours tror det er ett bra utg.punkt.
        System.out.println("Now concatenating...");
        int minPixels = 10000;

        List<Segment> segs = this.segments.stream().filter(
                segment -> segment.getSegmentSize() < minPixels
        ).collect(Collectors.toList());


        while (segs.size() > 3) {

//            System.out.println("Seg size: " + segs.size());

            Segment s1 = segs.get(new Random().nextInt(segs.size()));

            PriorityQueue<SegmentEdge> pq = new PriorityQueue<>();
            // Choose one random, find its distance to all other segments, merge with the closest one

            for (int i = 0; i < segs.size(); i++) {
                Segment s2 = segs.get(i);
                if (s2 == s1) {
                    continue;
                }
                pq.add(new SegmentEdge(s1, s2));
            }
            for(int k = 0; k < 10 && !pq.isEmpty(); k++){
                SegmentEdge se = pq.remove(); // get the two closest.
                se.s1.addAllPixels(se.s2.pixels); // merge the 2 segments
                segs.remove(se.s2); // fjern s2 fra segs slik at vi ikke kan adde til den, men da heller til s1.
                this.segments.remove(se.s2);
            }
//            SegmentEdge se = pq.remove(); // get the two closest.
//            se.s1.addAllPixels(se.s2.pixels); // merge the 2 segments
//            segs.remove(se.s2); // fjern s2 fra segs slik at vi ikke kan adde til den, men da heller til s1.
//            this.segments.remove(se.s2);

            segs = this.segments.stream().filter(
                    segment -> segment.getSegmentSize() < minPixels
            ).collect(Collectors.toList());
        }
    }


}
