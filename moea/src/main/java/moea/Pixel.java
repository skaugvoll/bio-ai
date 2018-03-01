package moea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Pixel {

    private final String[] directions = {"left", "right", "up", "down", "none"};

    private int[] coordinates = new int[2];
    private String arrow;
    private int[] RGB = new int[3];
    private ArrayList<Edge> neighbours;

    private Pixel parent = null;
    private ArrayList<Pixel> children = new ArrayList<Pixel>();


    public Pixel(int[] RGB, int[] coordinates) {
        this.RGB = RGB;
        this.coordinates = coordinates;
        this.arrow = directions[new Random().nextInt(4)];
    }


    public Pixel(int[] RGB, ArrayList<Edge> neighbours, int[] coordinates) {
        this.RGB = RGB;
        this.neighbours = neighbours;
        this.coordinates = coordinates;
        this.arrow = directions[new Random().nextInt(4)];
    }



    public int[] getRGB() {
        return RGB;
    }

    public void addNeighbour(Edge edge){
        this.neighbours.add(edge);
    }

    public ArrayList<Edge> getNeighbours() {
        return neighbours;
    }

    public Edge getNeighbourEdge(int index){
        return this.neighbours.get(index);
    }

    public void addChild(Pixel child){
        this.children.add(child);
    }

    public void setParent(Pixel parent) {
        this.parent = parent;
    }

    
    @Override
    public String toString() {
        return this.neighbours.toString() + "C: " + Arrays.toString(this.coordinates);
    }

    public Pixel getParent() {
        return parent;
    }
}

