package moea;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class GA {

    DataGenerator dg = new DataGenerator();
    Prims prim = new Prims();

    private Pixel[][] pixels = {};

    public void run(){
        pixels = dg.readImage("1");
        calculateNeighbourDistance();
        prim.algorithm(pixels);

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

    public void drawImage(){
        BufferedImage newImage = new BufferedImage(481, 321, BufferedImage.TYPE_INT_ARGB);
        File f = null;

        for(int row = 0; row < 321; row++){
            for(int col = 0; col < 481; col++){
//                int a = (int)(Math.random()*256);
                int a = (int) 255;
                int r = (int)(Math.random()*256);
                int g = (int)(Math.random()*256);
                int b = (int)(Math.random()*256);

                int p = (a << 24) | (r << 16) | (g << 8) | b;

                newImage.setRGB(col, row, p);
            }
        }

        try{
            f = new File(GA.class.getResource("/Output/out.png").getPath());
            ImageIO.write(newImage, "png", f);
        }catch(IOException e){
            System.out.println("Kunne ikke skrive ut fil");
        }
    }

    public static void main(String[] args) {
        GA g = new GA();
//        g.run();
        g.drawImage();

    }

}
