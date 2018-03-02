package moea;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class DataGenerator {

    public Pixel[][] readImage(String number) {
        String resourcePath = "/TestImages/"+number+"/Test image.jpg";
        Pixel[][] pixels = {};

        try{
            BufferedImage hugeImage = ImageIO.read(DataGenerator.class.getResourceAsStream(resourcePath));
            System.out.println("okay, now bufferdImage");

            pixels = convertTo2DWithoutUsingGetRGB(hugeImage); // height x width
            this.createNeighbours(pixels);
//            System.out.println(Arrays.toString(pixels[0][1].getRGB()));
//            System.out.println(pixels[0][1]);
            return pixels;
        }
        catch (IOException e){
            System.out.println("fåkk, something went wrong!\n" + e);
        }

        return pixels;

    }


    private static Pixel[][] convertTo2DWithoutUsingGetRGB(BufferedImage image) {

        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;
        System.out.println("hasAlphaCHannel: " + hasAlphaChannel);

        Pixel[][] result = new Pixel[height][width];
        if (hasAlphaChannel) {
            final int pixelLength = 4;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
                argb += ((int) pixels[pixel + 1] & 0xff); // blue
                argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
                result[row][col] = new Pixel(GBRtoRGB(argb), new int[]{row, col});
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        } else {
            final int pixelLength = 3;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += -16777216; // 255 alpha
                argb += ((int) pixels[pixel] & 0xff); // blue
                argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
                result[row][col] = new Pixel(GBRtoRGB(argb), new int[]{row, col});
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        }

        return result;
    }


    public static int[] GBRtoRGB(int pixelBGRinteger){
        int  red   = (pixelBGRinteger & 0x00ff0000) >> 16;
        int  green = (pixelBGRinteger & 0x0000ff00) >> 8;
        int  blue  =  pixelBGRinteger & 0x000000ff;
//        System.out.println("Red Color value = "+ red);
//        System.out.println("Green Color value = "+ green);
//        System.out.println("Blue Color value = "+ blue);
        int[] data = {red,green,blue};
        return data;


    }


    public void createNeighbours(Pixel[][] result){
        int numberOfPRows = result.length;
        int numberOfPixelsPerRow = result[0].length;
//        Pixel[][] pixels = new Pixel[numberOfPRows][numberOfPixelsPerRow];
        for(int row = 0; row < numberOfPRows; row++){
            for(int pixel = 0; pixel < numberOfPixelsPerRow; pixel++) {
                Pixel currentPixel = result[row][pixel];

                //North
                if(row == 0 && pixel == 0){ // øvre venstre hjørne
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row][pixel+1]));
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row+1][pixel]));
                }
                else if(row == 0 && pixel == numberOfPixelsPerRow -1){ // øvre høyre hjørne
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row+1][pixel]));
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row][pixel-1]));
                }
                else if(row == 0){ // denne tar alle på øverste rad som ikke er i et hjørne
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row][pixel+1]));
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row+1][pixel]));
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row][pixel-1]));
                }
                else if(row == numberOfPRows -1 && pixel == 0){ // nedre venstre hjørne
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row-1][pixel]));
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row][pixel+1]));
                }
                else if(row == numberOfPRows -1  && pixel == numberOfPixelsPerRow -1) { // nedre høyre hjørne
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row][pixel-1]));
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row-1][pixel]));
                }
                else if(row == numberOfPRows -1) { // denne tar alle på nederste linje som ikke er i hjørne
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row][pixel+1]));
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row][pixel-1]));
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row-1][pixel]));
                }
                else if(pixel == 0){ // denne tar alle som er helt til venstre i bildet.
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row][pixel+1]));
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row+1][pixel]));
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row-1][pixel]));
                }
                else if(pixel == numberOfPixelsPerRow -1) { // denne tar alle helt til høyre i bildet.
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row+1][pixel]));
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row][pixel-1]));
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row-1][pixel]));
                }
                else { // denne tar alle som ikke er langs en kant.
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row][pixel+1]));
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row+1][pixel]));
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row][pixel-1]));
                    currentPixel.addNeighbour(new Edge(currentPixel, result[row-1][pixel]));
                }
            }
        }

    }

    public void drawSegments(ArrayList<Segment> segments){
//        BufferedImage newImage = new BufferedImage(5, 5, BufferedImage.TYPE_INT_ARGB);
        BufferedImage newImage = new BufferedImage(4, 3, BufferedImage.TYPE_INT_ARGB);
        File f = null;

        for(Segment s : segments){
            for(Pixel p : s.pixels){
                int color = (255 << 24) | (s.color.getRed() << 16) | (s.color.getGreen() << 8) | s.color.getBlue();

                newImage.setRGB(p.coordinates[1], p.coordinates[0], color);
            }
        }

        try{
//            f = new File(this.getClass().getResource("Output/out.png").getPath());
            f = new File("/Users/sigveskaugvoll/Documents/Skole/2018V/Bio-Insipred Artificial intelligence/Assignments/bio-ai/moea/src/main/resources/Output/out.png");
//            f = new File("C:\\Users\\thmwl\\Documents\\git\\bio-ai\\moea\\src\\main\\resources\\Output\\out.png");
            ImageIO.write(newImage, "png", f);
        }catch(IOException e){
            System.out.println("Kunne ikke skrive ut fil");
        }
    }

    public void drawImage(Pixel[][] pixels){
        BufferedImage newImage = new BufferedImage(481, 321, BufferedImage.TYPE_INT_ARGB);
        File f = null;


        for(int row = 0; row < pixels.length; row++){
            for(int col = 0; col < 481; col++){
                int[] RGB = pixels[row][col].getRGB();

                int p = (255 << 24) | (RGB[0] << 16) | (RGB[1] << 8) | RGB[2];

                newImage.setRGB(col, row, p);
            }
        }

        try{
//            f = new File(this.getClass().getResource("Output/out.png").getPath());
            f = new File("/Users/sigveskaugvoll/Documents/Skole/2018V/Bio-Insipred Artificial intelligence/Assignments/bio-ai/moea/src/main/resources/Output/out.png");
            ImageIO.write(newImage, "png", f);
        }catch(IOException e){
            System.out.println("Kunne ikke skrive ut fil");
        }
    }

    public static void main(String[] args) {
        DataGenerator dg = new DataGenerator();
        dg.readImage("10");
    }


}
