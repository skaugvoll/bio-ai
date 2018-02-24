
package moea;

import java.util.*;

public class Prims {

    public void algorithm(Pixel[][] pixels){
        long startTime = System.currentTimeMillis();

        int numberOfpixels = pixels.length * pixels[0].length;

        ArrayList<Pixel> visited = new ArrayList<Pixel>();
        SortedMap<Double, ArrayList<Pixel[]>> open = new TreeMap<Double, ArrayList<Pixel[]>>();

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
            checkIfAllreadyFound(parent, nbr, dist, open);

        }

        while(visited.size() != numberOfpixels){
            double lowestCost = Double.MAX_VALUE;
            Pixel gonnaBevisited = null;
            Pixel parent = null;

            double gonnaBeVisitedKey = open.firstKey();
            ArrayList<Pixel[]> edges = open.get(gonnaBeVisitedKey);
            if(visited.contains(edges.get(0)[1])){
                edges.remove(0); // remove the "edge" from open list.
                if(open.get(gonnaBeVisitedKey).size() <1){
                    open.remove(gonnaBeVisitedKey);;
                }
                continue;
            }
            gonnaBevisited = edges.get(0)[1];
            parent = edges.get(0)[0];

            // add the gonnaBeVisited to visitedList
            visited.add(gonnaBevisited);
            gonnaBevisited.setParent(parent);
            parent.addChild(gonnaBevisited);
            // remove gonna be visited from open ?
            edges.remove(0); // remove the "edge" from open list.
            if(open.get(gonnaBeVisitedKey).size() <1){
                open.remove(gonnaBeVisitedKey);;
            }

            // add new neighbours to open list
            // check if neighbour is allready visited
            for(int n = 0; n < gonnaBevisited.getNeighbours().length; n++){
                int[] coord = gonnaBevisited.getNeighbour(n);

//          If there is no neighbour
                if(coord[0] == -1){
                    continue;
                }

                Pixel nbr = pixels[coord[0]][coord[1]];
                if (visited.contains(nbr) || open.containsValue(nbr)){
                    continue;
                }
                else{
                    double dist = gonnaBevisited.getNeighboursDistances()[n];
                    checkIfAllreadyFound(gonnaBevisited, nbr, dist, open);
                }
            }
        }
        long endTime = System.currentTimeMillis();

        System.out.println(visited.size() + " :: " + pixels.length + "\n Time: " + (endTime - startTime));


    }

    private void checkIfAllreadyFound(Pixel parent, Pixel child, double dist, SortedMap<Double, ArrayList<Pixel[]>> open) {
        if(open.get(dist) == null){
            ArrayList<Pixel[]> values = new ArrayList<Pixel[]>();
            Pixel[] value = {parent, child};
            values.add(value);
            open.put(dist, values);
        } else {
            Pixel[] value = {parent, child};
            ArrayList<Pixel[]> values = open.get(dist);
            values.add(value);
            open.put(dist, values);
        }
    }


}
