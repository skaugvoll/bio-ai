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
    int maxSegments;
    int wantedSegments;
    int colorTeta;

    ArrayList<Pixel> rootNodes;
    ArrayList<Segment> segments = new ArrayList<>();
    ArrayList<Pixel> edges = new ArrayList<>();
    public HashMap<String, Pixel> coordinateToPixel = new HashMap<>();

    double overallDeviation = 0;
    double edgeValue = 0;
    double fitness; // the lower the better!

    ///////// NSGA-II shit
    //
    int rank;
    ArrayList<Chromosome> sp = new ArrayList<>();  // a set of solutions that the solution 'ch' dominates.
    int np = 0;// np = domination count. How many CH is dominated by
    double crowdingDistance = 0;
    //
    /////////

    double[] weights = {0.5,0.5};

    public Chromosome(MST mst, int numberOfPixels, int minSegments, int maxSegments, double[] weights, int colorTeta){
        this.mst = mst;
        this.numberOfPixels = numberOfPixels;
        this.minSegments = minSegments;
        this.maxSegments = maxSegments;
        this.wantedSegments = new Random().nextInt(maxSegments-minSegments) + minSegments; // gives [minsegs, maxSegs)
        this.weights = weights;
        this.colorTeta = colorTeta;

        this.rootNodes = new ArrayList<>();
        this.segments = new ArrayList<>();
        this.edges = new ArrayList<>();

        this.generateSegments();
        this.concatenateSegments();

        findEdgePixels();
        calculateOverallDeviation();
        calculateEdgeValue();
        this.fitness = calculateFitness();

        // TODO: move out of this constructor into the GA
//        DataGenerator dg = new DataGenerator();
//        dg.drawSegments(this.segments);
//        dg.drawTrace(this, true);
//        dg.drawTrace(this, false);
    }

    public Chromosome(int numberOfPixels, int minSegments, int maxSegments, ArrayList<Segment> segments) {
        this.segments = new ArrayList<>();
        this.numberOfPixels = numberOfPixels;
        this.minSegments = minSegments;
        this.maxSegments = maxSegments;
        ArrayList<Pixel> newPixels = new ArrayList<>();
        for(Segment s : segments){
            Segment newSegment = new Segment(s.pixels, this);
            this.segments.add(newSegment);
            newPixels.addAll(newSegment.pixels);
        }

        int numberOfPRows = 321;
        int numberOfPixelsPerRow = 481;
        for(Pixel p: newPixels){
            if(p.coordinates[0] == 0 && p.coordinates[1] == 0){ // øvre venstre hjørne
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0], p.coordinates[1]+1}))));
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0]+1, p.coordinates[1]}))));
            }
            else if(p.coordinates[0] == 0 && p.coordinates[1] == numberOfPixelsPerRow -1){ // øvre høyre hjørne
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0]+1, p.coordinates[1]}))));
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0], p.coordinates[1]-1}))));

            }
            else if(p.coordinates[0] == 0){ // denne tar alle på øverste rad som ikke er i et hjørne
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0]+1, p.coordinates[1]}))));
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0], p.coordinates[1]-1}))));

            }
            else if(p.coordinates[0] == numberOfPRows -1 && p.coordinates[1] == 0){ // nedre venstre hjørne
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0]-1, p.coordinates[1]}))));
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0], p.coordinates[1]+1}))));
            }
            else if(p.coordinates[0] == numberOfPRows -1  && p.coordinates[1] == numberOfPixelsPerRow -1) { // nedre høyre hjørne
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0], p.coordinates[1]-1}))));
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0]-1, p.coordinates[1]}))));
            }
            else if(p.coordinates[0] == numberOfPRows -1) { // denne tar alle på nederste linje som ikke er i hjørne
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0], p.coordinates[1]+1}))));
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0], p.coordinates[1]-1}))));
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0]-1, p.coordinates[1]}))));
            }
            else if(p.coordinates[1] == 0){ // denne tar alle som er helt til venstre i bildet.
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0], p.coordinates[1]+1}))));
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0]+1, p.coordinates[1]}))));
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0]-1, p.coordinates[1]}))));
            }
            else if(p.coordinates[1] == numberOfPixelsPerRow -1) { // denne tar alle helt til høyre i bildet.
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0]+1, p.coordinates[1]}))));
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0], p.coordinates[1]-1}))));
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0]-1, p.coordinates[1]}))));
            }
            else { // denne tar alle som ikke er langs en kant.
//                Pixel p1 = coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0], p.coordinates[1]+1}));
//                Pixel p2 = coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0]+1, p.coordinates[1]}));
//                Pixel p3 = coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0], p.coordinates[1]-1}));
//                Pixel p4 = coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0]-1, p.coordinates[1]}));
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0], p.coordinates[1]+1}))));
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0]+1, p.coordinates[1]}))));
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0], p.coordinates[1]-1}))));
                p.addNeighbour(new Edge(p, coordinateToPixel.get(Arrays.toString(new int[] {p.coordinates[0]-1, p.coordinates[1]}))));
            }

        }

    }

    public void calculateOverallDeviation(){
        overallDeviation = 0;
        for(Segment s : segments){
            s.calculateDeviation();
            overallDeviation += s.deviation;
        }
    }

    public void calculateEdgeValue(){
        double value = 0;


        for(Pixel p : this.edges){
            for(Edge nbrs : p.getNeighbours()){
                Pixel nbr = nbrs.getNeighbourPixel();
                if( ! p.segment.equals(nbr.segment) && nbr.segment != null){
                    value += RGBdistance(p, nbr);
                }
            }
        }
        this.edgeValue = -value; // tar negative verdien fordi da kan vi forholde oss til kun minimalisering av fitness objektiver.
    }

    public double RGBdistance(Pixel p1, Pixel p2){
        return Math.sqrt(Math.pow(p1.getRGB()[0] - p2.getRGB()[0], 2) + Math.pow(p1.getRGB()[1] - p2.getRGB()[1], 2) + Math.pow(p1.getRGB()[2] - p2.getRGB()[2], 2));
    }

    private void generateSegments() {
        ArrayList<Pixel> foundNewSegment = new ArrayList<>();

        double teta = this.colorTeta;

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
//        int minPixels = 10000;

        int minPixels = this.numberOfPixels / wantedSegments;

//        List<Segment> segs = this.segments.stream().filter(
//                segment -> segment.getSegmentSize() < minPixels
//        ).collect(Collectors.toList());

        boolean run = true;
//        while (segs.size() > minSegments) {
        while (run) {

            List<Segment> segs = this.segments.stream().filter(
                    segment -> segment.getSegmentSize() < minPixels
            ).collect(Collectors.toList());

            // check if we are allowed to remove // concatenate segments
            if(this.segments.size() -1 < wantedSegments){
                run = false;
                break;
            }

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
            for(int k = 0; k < 1 && !pq.isEmpty(); k++){
                SegmentEdge se = pq.remove(); // get the two closest.
                se.s1.addAllPixels(se.s2.pixels); // merge the 2 segments
                segs.remove(se.s2); // fjern s2 fra segs slik at vi ikke kan adde til den, men da heller til s1.
                this.segments.remove(se.s2);

                // check if after concatination, we can concatinate another one, or have min segments
                if((this.segments.size()) - 1 < wantedSegments){
                    run = false;
                    break;
                }


            }

//            segs = this.segments.stream().filter(
//                    segment -> segment.getSegmentSize() < minPixels
//            ).collect(Collectors.toList());
        }
    }

    public double calculateFitness(){
//        double value = 0;
//
//        for(Segment s : segments){
//            s.calculateDeviation();
//            overallDeviation += s.deviation;
//            for(Pixel p : s.pixels){
//                for(Edge nbrs : p.getNeighbours()){
//                    Pixel nbr = nbrs.getNeighbourPixel();
//                    if( s.pixels.contains(nbr) ){
//                        value += 0;
//                        continue;
//                    }
//                    value += s.RGBdistance(p, nbr);
//                }
//            }
//        }
//        this.edgeValue = -value; // tar negative verdien fordi da kan vi forholde oss til kun minimalisering av fitness objektiver.

        return (this.overallDeviation * weights[0]) - (this.edgeValue * weights[1]);
    }

    public double getFitness() {
        return fitness;
    }



    public void findEdgePixels(){
        edges.clear();
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
            s.calculateAvgSegColor();
            for(Pixel p : s.pixels){
                p.segment = s;
                if(! coordinateToPixel.containsKey(Arrays.toString(p.coordinates))){
                    coordinateToPixel.put(Arrays.toString(p.coordinates), p);
                }
                for(Edge nbrs : p.getNeighbours()){
                    Pixel nbr = nbrs.getNeighbourPixel();
                    if (p.coordinates[0] == 0 || p.coordinates[0] == maxRow || p.coordinates[1] == 0 || p.coordinates[1] == maxCol || !s.pixels.contains(nbr)) {
                        if(!edges.contains(p)){
                            this.edges.add(p);
                        }
                    } else if( s.pixels.contains(nbr) ){
                        continue;
                    }
                }
            }
        }
    }


}
