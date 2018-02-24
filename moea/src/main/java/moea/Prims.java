
package moea;

import java.util.*;

public class Prims {

    public void algorithm(Pixel[][] pixels){

        int numberOfpixels = pixels.length * pixels[0].length;


        ArrayList<Pixel> visited = new ArrayList<Pixel>();
        SortedMap<Double, Pixel[]> open = new TreeMap<Double, Pixel[]>();

//        int initPixel = new Random().nextInt(numberOfpixels); // choose random pixel to start from.
        int initPixel = 5;
        int initRow = (int) initPixel / pixels[0].length;
        int initPix = initPixel % pixels[0].length;

        visited.add(pixels[initRow][initPix]);

        // add first pixels neighbours to open list (in a sorted manner)
        for(int n = 0; n < visited.get(0).getNeighbours().length; n++){
            int[] coord = visited.get(0).getNeighbour(n);

//          If there is no neighbour
            if(coord[0] == -1){
                continue;
            }

            Pixel nbr = pixels[coord[0]][coord[1]];
            Pixel parent = visited.get(0);
            double dist = parent.getNeighboursDistances()[n];
            Pixel[] value = {parent, nbr};
            open.put(dist, value);
        }

        while(visited.size() != numberOfpixels){
            double lowestCost = Double.MAX_VALUE;
            Pixel gonnaBevisited = null;
            Pixel parent = null;

            double gonnaBeVisitedKey = open.firstKey();
            Pixel[] edge = open.get(gonnaBeVisitedKey);
            gonnaBevisited = edge[1];
            parent = edge[0];

            // add the gonnaBeVisited to visitedList
            visited.add(gonnaBevisited);
            // remove gonna be visited from open ?
//            open.remove(gonnaBeVisitedKey, edge);

            // add new neighbours to open list
            // check if neighbour is allready visited
            for(int n = 0; n < gonnaBevisited.getNeighbours().length; n++){
                int[] coord = gonnaBevisited.getNeighbour(n);

//          If there is no neighbour
                if(coord[0] == -1){
                    continue;
                }

                Pixel nbr = pixels[coord[0]][coord[1]];
                if (visited.contains(nbr)){
                    continue;
                }
                else{
                    double dist = gonnaBevisited.getNeighboursDistances()[n];
                    Pixel[] value = {gonnaBevisited, nbr};
                    open.put(dist, value);
                }
            }
        }



        System.out.println(visited.size() + " :: " + pixels.length);


    }


}
