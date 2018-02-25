package moea;

public class GA {

    DataGenerator dg = new DataGenerator();
    Prims prim = new Prims();

    private Pixel[][] pixels = {};

    public void run(){
        pixels = dg.readImage("1");
        calculateNeighbourDistance();
//        prim.algorithm(pixels);
        dg.drawImage(pixels);

    }

    public void calculateNeighbourDistance() {
        int numberOfPRows = this.pixels.length;
        int numberOfPixelsPerRow = this.pixels[0].length;
        for (int row = 0; row < numberOfPRows; row++) {
            for (int pixel = 0; pixel < numberOfPixelsPerRow; pixel++) {
                Pixel p1 = this.pixels[row][pixel];
                for(int n = 0; n < 4; n++) {
                    if (p1.getNeighbours()[n][0] == -1) {
                        p1.setNeighboursDistance(n, -1);
                        continue;
                    }
                    int xv = p1.getNeighbours()[n][0];
                    int swish = p1.getNeighbours()[n][1];
                    Pixel p2 = pixels[p1.getNeighbours()[n][0]][p1.getNeighbours()[n][1]];
                    p1.setNeighboursDistance(n, this.RGBdistance(p1, p2));
                }
            }
        }
    }

    private double RGBdistance(Pixel p1, Pixel p2){
        return Math.sqrt(Math.pow(p1.getRGB()[0] - p2.getRGB()[0], 2) + Math.pow(p1.getRGB()[1] - p2.getRGB()[1], 2) + Math.pow(p1.getRGB()[2] - p2.getRGB()[2], 2));
    }

    public static void main(String[] args) {
        GA g = new GA();
        g.run();
    }

}
