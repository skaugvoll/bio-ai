package moea;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;



public class DataGenerator {

    public void readImage(String number) {
        String resourcePath = "/TestImages/"+number+"/Test image.jpg";

        try{
            BufferedImage hugeImage = ImageIO.read(DataGenerator.class.getResourceAsStream(resourcePath));
            System.out.println("okay, now bufferdImage");

            int[][] result = convertTo2DWithoutUsingGetRGB(hugeImage);
//            int[][] result = convertTo2DUsingGetRGB(hugeImage);

            System.out.println(this.GBRtoRGB(result[0][0]));
        }
        catch (IOException e){
            System.out.println("fåkk, something went wrong!\n" + e);
        }


    }

    private static int[][] convertTo2DUsingGetRGB(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] result = new int[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                result[row][col] = image.getRGB(col, row);
                System.out.println(image.getRGB(col, row));
            }
        }

        return result;
    }


    private static int[][] convertTo2DWithoutUsingGetRGB(BufferedImage image) {

        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;
        System.out.println("hasAlphaCHannel: " + hasAlphaChannel);

        int[][] result = new int[height][width];
        if (hasAlphaChannel) {
            final int pixelLength = 4;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
                argb += ((int) pixels[pixel + 1] & 0xff); // blue
                argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
                result[row][col] = argb;
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
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        }

        return result;
    }


    public int[] GBRtoRGB(int pixelBGRinteger){
        int  red   = (pixelBGRinteger & 0x00ff0000) >> 16;
        int  green = (pixelBGRinteger & 0x0000ff00) >> 8;
        int  blue  =  pixelBGRinteger & 0x000000ff;
        System.out.println("Red Color value = "+ red);
        System.out.println("Green Color value = "+ green);
        System.out.println("Blue Color value = "+ blue);
        int[] data = {red,green,blue};
        return data;


    }


    public static void main(String[] args) {
        DataGenerator dg = new DataGenerator();
        dg.readImage("1");
    }


}
