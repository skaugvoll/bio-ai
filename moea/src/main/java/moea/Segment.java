package moea;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Segment {

    public Color color;
    public Pixel root = null;
    public ArrayList<Pixel> pixels;
    public ArrayList<Edge> edges;

    public double deviation = 0;
    public int avgSegCol = 0;
    private int tempSum = 0;

    public int[] centroid = new int[2];

    ///////
    //
    public int[] avgSegColors = new int[3];
    //
    ///////


    public Segment(Pixel root, Color color){
        this.root = root;
        this.color = color;

        this.pixels = new ArrayList<>();
        this.addPixel(root);
        calcualteCentroid();
        this.tempSum = IntStream.of(root.getRGB()).sum();

        this.edges = new ArrayList<>();
    }

    public Segment(ArrayList<Pixel> pixels, Chromosome c) {
        this.color = new Color(new Random().nextInt(256),new Random().nextInt(256),new Random().nextInt(256));
        this.pixels = new ArrayList<>();
        for(Pixel p: pixels){
            Pixel newPixel = (Pixel) p.clone();
            c.coordinateToPixel.put(Arrays.toString(newPixel.coordinates), newPixel);
            newPixel.segment = this;
            addPixel(newPixel);
        }
        calculateAvgSegColor();


    }

    void calculateAvgSegColor(){
        this.avgSegColors[0] = 0;
        this.avgSegColors[1] = 0;
        this.avgSegColors[2] = 0;

        for(Pixel pix: this.pixels){
            for(int i = 0; i < pix.getRGB().length; i++){
                this.avgSegColors[i] += pix.getRGB()[i];
            }
        }
        if(this.pixels.size() > 0){
            this.avgSegColors[0] /= this.pixels.size();
            this.avgSegColors[1] /= this.pixels.size();
            this.avgSegColors[2] /= this.pixels.size();
        }
    }

    void addPixel(Pixel pix){
        this.pixels.add(pix);

//        for(int i = 0; i < pix.getRGB().length; i++){
//            this.avgSegColors[i] += pix.getRGB()[i];
////            this.avgSegColors[i] /= this.pixels.size();
//        }

        int pc = IntStream.of(pix.getRGB()).sum();
        tempSum = tempSum + pc;
        this.avgSegCol = (tempSum) / pixels.size();
//        calculateAvgSegColor();
    }

    void addEdge(Edge e){
        this.edges.add(e);
    }

    public void addAllPixels(ArrayList<Pixel> foundThisSegment) {
        this.pixels.addAll(foundThisSegment);

        for(Pixel pix : foundThisSegment){
            pix.segment = this;
        }

        int avgSum = pixels.stream().mapToInt(p -> IntStream.of(p.getRGB()).sum()).sum();
        tempSum += avgSum;
        this.avgSegCol = tempSum / pixels.size();
//        calculateAvgSegColor();

    }

    public void addAllEdges(ArrayList<Edge> foundThisEdges) {
        this.edges.addAll(foundThisEdges);
    }

    public int getSegmentSize() {
        return this.pixels.size();
    }

    private void calcualteCentroid(){
        int x = 0;
        int y = 0;

        for(Pixel p : pixels){
            x += p.coordinates[0];
            y += p.coordinates[1];
        }

        this.centroid = new int[] {x/pixels.size(), y/pixels.size()};
    }

    public void calculateDeviation(){
        deviation = 0;
        for(Pixel p : pixels){
            deviation += avgRGBdistance(p);
        }
    }



    public double avgRGBdistance(Pixel p2){
        return Math.sqrt(Math.pow(avgSegColors[0]/pixels.size() - p2.getRGB()[0], 2) + Math.pow(avgSegColors[1]/pixels.size() - p2.getRGB()[1], 2) + Math.pow(avgSegColors[2]/pixels.size() - p2.getRGB()[2], 2));
    }
}
