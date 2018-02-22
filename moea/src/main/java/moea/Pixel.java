package moea;

import java.util.Arrays;
import java.util.Random;

public class Pixel {

    private final String[] directions = {"left", "right", "up", "down", "none"};

    private String arrow;
    private int[] RGB = new int[3];
    private int[][] neighbours = new int[4][2];
    private double[] neighboursDistances = new double[4];

    public Pixel(int[] RGB, int[][] neighbours) {
        this.RGB = RGB;
        this.neighbours = neighbours;
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

    @Override
    public String toString() {
        return Arrays.toString(this.neighboursDistances);
    }

    public double[] getNeighboursDistances() {
        return neighboursDistances;
    }

    public int[] getNeighbour(int index){
        return this.neighbours[index];
    }
}
