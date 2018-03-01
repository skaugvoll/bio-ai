package moea;

public class Edge {

    private Pixel currentPixel;
    private Pixel neighbourPixel;
    private double distance;

    public Edge(Pixel startPixel, Pixel endPixel) {
        this.currentPixel = startPixel;
        this.neighbourPixel = endPixel;
        this.distance = this.RGBdistance(currentPixel, neighbourPixel);
    }


    private double RGBdistance(Pixel p1, Pixel p2){
        return Math.sqrt(Math.pow(p1.getRGB()[0] - p2.getRGB()[0], 2) + Math.pow(p1.getRGB()[1] - p2.getRGB()[1], 2) + Math.pow(p1.getRGB()[2] - p2.getRGB()[2], 2));
    }






}
