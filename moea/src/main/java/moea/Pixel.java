package moea;

import java.util.Arrays;

public class Pixel {

    private String arrow;
    private int[] RGB = new int[3];
    private int[][] neighbours = new int[4][2];

    public Pixel(int[] RGB, int[][] neighbours) {
        this.RGB = RGB;
        this.neighbours = neighbours;
    }

    public int[] getRGB() {
        return RGB;
    }

    public int[][] getNeighbours() {
        return neighbours;
    }

    @Override
    public String toString() {
        return Arrays.deepToString(this.neighbours);
    }
}
