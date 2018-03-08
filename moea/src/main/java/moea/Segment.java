package moea;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Segment {

    public Color color;

    public final Pixel root;
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
        this.tempSum = IntStream.of(root.getRGB()).sum();

        this.edges = new ArrayList<>();
    }

    void addPixel(Pixel pix){
        this.pixels.add(pix);

        for(int i = 0; i < pix.getRGB().length; i++){
            this.avgSegColors[i] += pix.getRGB()[i];
            this.avgSegColors[i] /= this.pixels.size();
        }

        int pc = IntStream.of(pix.getRGB()).sum();
        tempSum = tempSum + pc;
        this.avgSegCol = (tempSum) / pixels.size();

    }

    void addEdge(Edge e){
        this.edges.add(e);
    }

    public void addAllPixels(ArrayList<Pixel> foundThisSegment) {
        this.pixels.addAll(foundThisSegment);

        int avgSum = pixels.stream().mapToInt(p -> IntStream.of(p.getRGB()).sum()).sum();
        tempSum += avgSum;
        this.avgSegCol = tempSum / pixels.size();

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
        int red = 0;
        int green = 0;
        int blue = 0;
        for(Pixel pix : pixels){
            red += pix.getRGB()[0];
            green += pix.getRGB()[1];
            blue += pix.getRGB()[2];
        }
        this.avgSegColors = new int[] {red/this.pixels.size(), green/this.pixels.size(), blue/this.pixels.size()};

        for(Pixel p : pixels){
            deviation += AvgRGBdistance(p);
        }
    }

    public double RGBdistance(Pixel p1, Pixel p2){
        return Math.sqrt(Math.pow(p1.getRGB()[0] - p2.getRGB()[0], 2) + Math.pow(p1.getRGB()[1] - p2.getRGB()[1], 2) + Math.pow(p1.getRGB()[2] - p2.getRGB()[2], 2));
    }
    public double AvgRGBdistance(Pixel p2){
        return Math.sqrt(Math.pow(avgSegColors[0] - p2.getRGB()[0], 2) + Math.pow(avgSegColors[1] - p2.getRGB()[1], 2) + Math.pow(avgSegColors[2] - p2.getRGB()[2], 2));
    }


}
