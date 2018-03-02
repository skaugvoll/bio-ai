package moea;

import java.awt.*;
import java.util.ArrayList;

public class Segment {

    public Color color;

    public final Pixel root;
    public ArrayList<Pixel> pixels;
    public ArrayList<Edge> edges;

    public Segment(Pixel root, Color color){
        this.root = root;
        this.color = color;

        this.pixels = new ArrayList<>();
        this.pixels.add(root);

        this.edges = new ArrayList<>();
    }

    void addPixel(Pixel pix){
        this.pixels.add(pix);
    }

    void addEdge(Edge e){
        this.edges.add(e);
    }

    public void addAllPixels(ArrayList<Pixel> foundThisSegment) {
        this.pixels.addAll(foundThisSegment);
    }

    public void addAllEdges(ArrayList<Edge> foundThisEdges) {
        this.edges.addAll(foundThisEdges);
    }
}
