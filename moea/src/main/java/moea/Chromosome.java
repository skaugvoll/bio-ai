package moea;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Chromosome {
    // In most cases, pixels are stored as corresponding color values (RGB or CIE L*a*b as the color space [1]).
    MST mst;
    int numberOfPixels;
    int minSegments;

    ArrayList<Pixel> rootNodes;
    ArrayList<Segment> segments;

    public Chromosome(MST mst, int numberOfPixels, int minSegments){
        this.mst = mst;
        this.numberOfPixels = numberOfPixels;
        this.minSegments = minSegments;

        rootNodes = new ArrayList<>();
        this.segments = new ArrayList<>();

        this.generateSegments();
        System.out.println("faen");
        DataGenerator dg = new DataGenerator();
        dg.drawSegments(this.segments);


    }


    private void generateSegments() {
        ArrayList<Pixel> foundNewSegment = new ArrayList<>();

        double teta = 5;

        Pixel root = mst.rootnode;
        foundNewSegment.add(root);

        for (int i = 0; i < foundNewSegment.size(); i++) {
            root = foundNewSegment.remove(i);
            ArrayList<Pixel> foundThisSegment = new ArrayList<>();
            
            Segment s = new Segment(root, new Color(10,10,10));

            for (int j = 0; j < mst.edges.size(); j++) {
                Edge e = mst.edges.get(j);
                if (e.getCurrentPixel() == root) {
                    mst.edges.remove(e);
                    if (e.getDistance() <= teta) {
                        foundThisSegment.add(e.getNeighbourPixel());
                    } else {
                        foundNewSegment.add(e.getNeighbourPixel());
                    }

                }
            }
        }


    }



    private void generateSegments2(){
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
        for(Pixel p : rootNodes){
            Segment segment = new Segment(p, new Color(new Random().nextInt(256),new Random().nextInt(256),new Random().nextInt(256)));

            for(int i = 0; i < segment.pixels.size(); i++){
//                for(int edg = 0; edg < mst.edges.size(); edg++){
                int edg = 0;
                while(edg < mst.edges.size()){
                    Edge e = mst.edges.get(edg);
                    if(segment.pixels.get(i) == e.getCurrentPixel()){
                        segment.addPixel(e.getNeighbourPixel());
                        segment.addEdge(e);
                        mst.edges.remove(e); // reduce the searchtime for each segment.
                        continue;
                    }
                    edg++;
                }
            }
        this.segments.add(segment);
        }
    }

}
