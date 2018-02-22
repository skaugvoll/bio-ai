
package moea;

import java.util.ArrayList;
import java.util.Random;

public class Prims {

    public void algorithm(Pixel[][] pixels){

        int numberOfpixels = pixels.length * pixels[0].length;
        ArrayList<Pixel> visited = new ArrayList<Pixel>();

        int initPixel = new Random().nextInt(numberOfpixels); // choose random pixel to start from.
        int initRow = (int) initPixel / pixels[0].length;
        int initPix = initPixel % pixels[0].length;

        visited.add(pixels[initRow][initPix]);

        while(visited.size() != numberOfpixels){
            double lowestCost = Double.MAX_VALUE;
            int[] gonnaBevisited = null;
            //find cheapest edge out of vistied pixels, to unvisited pixels

            for(Pixel p : visited){
                for(int i = 0; i < p.getNeighbours().length; i++){
                    if(p.getNeighboursDistances()[i] >= 0 && p.getNeighboursDistances()[i] < lowestCost && visited.contains(pixels[p.getNeighbour(i)[0]][p.getNeighbour(i)[1]])){
                        lowestCost = p.getNeighboursDistances()[i];
                        gonnaBevisited = p.getNeighbour(i);
                    }
                }
            }
            visited.add(pixels[gonnaBevisited[0]][gonnaBevisited[1]]);
        }

        System.out.println(visited.size() + " :: " + pixels.length);





    }


}
