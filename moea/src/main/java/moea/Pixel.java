package moea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Pixel {

    private final String[] directions = {"left", "right", "up", "down", "none"};

    private int[] coordinates = new int[2];
    private String arrow;
    private int[] RGB = new int[3];
    private int[][] neighbours = new int[4][2];
    private double[] neighboursDistances = new double[4];

    private Pixel parent = null;
    private ArrayList<Pixel> children = new ArrayList<Pixel>();


    public Pixel(int[] RGB, int[][] neighbours, int[] coordinates) {
        this.RGB = RGB;
        this.neighbours = neighbours;
        this.coordinates = coordinates;
        this.arrow = directions[new Random().nextInt(4)];
    }

    public int[] getRGB() {
        return RGB;
    }

    public int[][] getNeighbours() {
        return neighbours;
    }

    public void setNeighboursDistance(int index, double value) {
        this.neighboursDistances[index] = value;
    }

    public double[] getNeighboursDistances() {
        return neighboursDistances;
    }

    public int[] getNeighbour(int index){
        return this.neighbours[index];
    }

    public void addChild(Pixel child){
        this.children.add(child);
    }

    public void setParent(Pixel parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.neighboursDistances) + "C: " + Arrays.toString(this.coordinates);
    }
}

