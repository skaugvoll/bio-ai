package moea;

import java.awt.*;
import java.time.temporal.ValueRange;
import java.util.*;
import java.util.List;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Chromosome {
    // In most cases, pixels are stored as corresponding color values (RGB or CIE L*a*b as the color space [1]).
    MST mst;
    int numberOfPixels;
    int minSegments;

    ArrayList<Pixel> rootNodes;
    ArrayList<Segment> segments;
    ArrayList<Pixel> edges;

    double overallDeviation = 0;
    double edgeValue = 0;
    double fitness; // the lower the better!

    double[] weights = {0.5,0.5};

    public Chromosome(MST mst, int numberOfPixels, int minSegments, double[] weights){
        this.mst = mst;
        this.numberOfPixels = numberOfPixels;
        this.minSegments = minSegments;
        this.weights = weights;

        this.rootNodes = new ArrayList<>();
        this.segments = new ArrayList<>();
        this.edges = new ArrayList<>();

        this.generateSegments();
        this.concatenateSegments();


        calculateOverallDeviation();
        calculateEdgeValue();
        this.fitness = calculateFitness();

        // TODO: move out of this constructor into the GA
        findEdgePixels();
        DataGenerator dg = new DataGenerator();
        dg.drawSegments(this.segments);
        dg.drawTrace(this, true);
        dg.drawTrace(this, false);



    }

    public void calculateOverallDeviation(){
        for(Segment s : segments){
            overallDeviation += s.deviation;
        }
    }

    public void calculateEdgeValue(){
        double value = 0;

        for(Segment s : segments){
            for(Pixel p : s.pixels){
                for(Edge nbrs : p.getNeighbours()){
                    Pixel nbr = nbrs.getNeighbourPixel();
                    if( s.pixels.contains(nbr) ){
                        value += 0;
                        continue;
                    }
                    value += s.RGBdistance(p, nbr);
                }
            }
        }
        this.edgeValue = -value; // tar negative verdien fordi da kan vi forholde oss til kun minimalisering av fitness objektiver.
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
            // Choose one random, find its distance to all other segments, merge with the closest one
            Segment s1 = segs.get(new Random().nextInt(segs.size()));

            PriorityQueue<SegmentEdge> pq = new PriorityQueue<>();

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

            segs = this.segments.stream().filter(
                    segment -> segment.getSegmentSize() < minPixels
            ).collect(Collectors.toList());
        }
    }

    public double calculateFitness(){
        return (this.overallDeviation * weights[0]) - (this.edgeValue * weights[1]);
    }


    public double getFitness() {
        return fitness;
    }

    public void findEdgePixels(){
        int maxRow = Integer.MIN_VALUE;
        int maxCol = Integer.MIN_VALUE;

        for(Segment s  : segments){
            for( Pixel p : s.pixels){
                if(p.coordinates[0] > maxRow){
                    maxRow = p.coordinates[0];
                }
                else if(p.coordinates[1] > maxCol){
                    maxCol = p.coordinates[1];
                }
            }
        }

        for(Segment s : segments){
            for(Pixel p : s.pixels){
                for(Edge nbrs : p.getNeighbours()){
                    Pixel nbr = nbrs.getNeighbourPixel();
                    if (p.coordinates[0] == 0 || p.coordinates[0] == maxRow || p.coordinates[1] == 0 || p.coordinates[1] == maxCol) {
                        this.edges.add(p);
                    } else if( s.pixels.contains(nbr) ){
                        continue;
                    } else {
                        this.edges.add(p);
                    }
                }
            }
        }
    }


}
