
package moea;

import com.rits.cloning.Cloner;

import java.util.*;

public class Prims {

    public MST algorithm(Pixel[][] pixels){
//
        int numberOfpixels = pixels.length * pixels[0].length;

        int initPixel = new Random().nextInt(numberOfpixels); // choose random pixel to start from.

        int initRow = (int) initPixel / pixels[0].length;
        int initPix = initPixel % pixels[0].length;

        Pixel rootNode = pixels[initRow][initPix];

        MST mst = new MST(rootNode);

        PriorityQueue<Edge> priorityQueue = new PriorityQueue<>(rootNode.getNeighbours());

        while(!priorityQueue.isEmpty()){
            Edge currentEdge = priorityQueue.remove();

            // se t hælvette  å ikke legg til en allerede sengeliggende pixel hælvette.
            if(mst.fuckersVisited.contains(currentEdge.getNeighbourPixel())){
                continue;
            }
            mst.addEdge(currentEdge);

            mst.fuckersVisited.add(currentEdge.getNeighbourPixel());
            priorityQueue.addAll(currentEdge.getNeighbourPixel().getNeighbours());

        }
//        priorityQueue.clear(); // memory performance bro!
        return mst;
    }

}
