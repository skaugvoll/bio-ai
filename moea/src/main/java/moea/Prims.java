
package moea;

import com.rits.cloning.Cloner;

import java.util.*;

public class Prims {

    public ArrayList<Pixel> algorithm(Pixel[][] orgPixels){
        Pixel[][] pixels = new Cloner().deepClone(orgPixels);
        long startTime = System.currentTimeMillis();

        int numberOfpixels = pixels.length * pixels[0].length;

        ArrayList<Pixel> visited = new ArrayList<Pixel>();
        SortedMap<Double, ArrayList<Pixel[]>> open = new TreeMap<Double, ArrayList<Pixel[]>>();

        int initPixel = new Random().nextInt(numberOfpixels); // choose random pixel to start from.
//        int initPixel = 5;
        int initRow = (int) initPixel / pixels[0].length;
        int initPix = initPixel % pixels[0].length;

        visited.add(pixels[initRow][initPix]);

        // add first pixels neighbours to open list (in a sorted manner)
        for(int n = 0; n < visited.get(0).getNeighbours().size(); n++){
            Edge edge = visited.get(0).getNeighbourEdge(n);
            Pixel nbr = edge.getNeighbourPixel();
            Pixel parent = edge.getCurrentPixel();
            double dist = edge.getDistance();
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
            for(int n = 0; n < gonnaBevisited.getNeighbours().size(); n++){
//                int[] coord = gonnaBevisited.getNeighbour(n);
                Edge edge = gonnaBevisited.getNeighbourEdge(n);


                Pixel nbr = edge.getNeighbourPixel();
                if (visited.contains(nbr) || open.containsValue(nbr)){
                    continue;
                }
                else{
                    double dist = edge.getDistance();
                    checkIfAllreadyFound(gonnaBevisited, nbr, dist, open);
                }
            }
        }
        long endTime = System.currentTimeMillis();

        System.out.println(visited.size() + " :: " + pixels.length + "\n Time: " + (endTime - startTime));
        return visited; // visited = MST, root = visited[0]

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
