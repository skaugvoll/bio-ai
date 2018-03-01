package moea;

import java.util.ArrayList;
import java.util.Random;

public class Chromosome {
    // In most cases, pixels are stored as corresponding color values (RGB or CIE L*a*b as the color space [1]).
    MST mst;
    ArrayList<Pixel> segments;
    int numberOfPixels;
    int minSegments;

    ArrayList<Pixel> rootNodes;

    public Chromosome(MST mst, int numberOfPixels, int minSegments){
        this.mst = mst;
        this.segments = new ArrayList<>();
        this.numberOfPixels = numberOfPixels;
        this.minSegments = minSegments;
        rootNodes = new ArrayList<>();

        this.generateSegments();

    }

    private void generateSegments(){
        for(int s = 0; s < minSegments; s++){

            int newRoot = new Random().nextInt(numberOfPixels);
            Pixel currentRoot = mst.fuckersVisited.get(newRoot);

            while(rootNodes.contains(currentRoot)){
                currentRoot = mst.fuckersVisited.get(new Random().nextInt(numberOfPixels));
            }

            for(int i = 0; i < mst.edges.size(); i++){
                Edge e = mst.edges.get(i);
                if(e.getNeighbourPixel() == currentRoot){
                    mst.edges.remove(e);
                    rootNodes.add(currentRoot);
                    break;
                }
            }
        }

        if (!rootNodes.contains(mst.rootnode)){
            rootNodes.add(mst.rootnode);
        }

        // brutt opp MST inn i segmenter, vi vet hva som er rot nodene til segmentene, men ikke hvilke edges som tilhører hvilke segmenter
        
    }

}
