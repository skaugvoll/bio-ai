package moea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Pixel implements Cloneable {

    private final String[] directions = {"left", "right", "up", "down", "none"};

    public int[] coordinates = new int[2];
    private int[] RGB = new int[3];
    private ArrayList<Edge> neighbours = new ArrayList<>();

    Segment segment = null;

    public Pixel(int[] RGB, int[] coordinates) {
        this.RGB = RGB;
        this.coordinates = coordinates;
    }


    public Pixel(int[] RGB, ArrayList<Edge> neighbours, int[] coordinates) {
        this.RGB = RGB;
        this.neighbours = neighbours;
        this.coordinates = coordinates;
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

    @Override
    public  Object clone(){
        Pixel clonedPixel = null;
        try{
            clonedPixel = (Pixel) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        ArrayList<Pixel> cp = new ArrayList<>();
        for(Edge e: clonedPixel.neighbours){

        }
        clonedPixel.neighbours = new ArrayList<>();;
        return clonedPixel;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.getRGB()) + Arrays.toString(this.coordinates);
    }
}

