package moea;

import java.util.ArrayList;
import java.util.Random;

public class Chromosome {
    // In most cases, pixels are stored as corresponding color values (RGB or CIE L*a*b as the color space [1]).
    ArrayList<Pixel> mst;
    ArrayList<Pixel> segments;
    int numberOfPixels;
    int minSegments;

    public Chromosome(){

    }

    public Chromosome(ArrayList<Pixel> mst, int numberOfPixels, int minSegments){
        this.mst = mst;
        this.segments = new ArrayList<>();
        this.numberOfPixels = numberOfPixels;
        this.minSegments = minSegments;

        this.generateSegments();

    }

    private void generateSegments(){
        int numberOfFollowers = (int) Math.floor(this.numberOfPixels / this.minSegments);
        ArrayList<Pixel> visited = new ArrayList<>();

        while(segments.size() <= minSegments && mst.size() > 0) {
            if(segments.size() >= minSegments){
                ArrayList<Pixel> found = new ArrayList<>();
                for(Pixel node : mst){
                    if(! segments.contains(node) || ! visited.contains(node)){
                        if(! found.contains(node) && ! segments.contains(node)){
                            segments.add(node);
                        }

                        found.addAll(node.getChildren());
//                        for(Pixel child : found){
//                            found.addAll(child.getChildren());
//                        }



                        visited.addAll(found);
                    }
                }
                break;
            }

            int randomPixel = new Random().nextInt(mst.size());
            Pixel p = mst.get(randomPixel);
            if(segments.contains(p) || visited.contains(p)){
                continue;
            }

            Pixel parent = p.getParent();

            if(parent != null){
                parent.removeChild(p);
            }

            p.setParent(null);

            segments.add(p);
            mst.remove(p);

            if(p.getChildren().size() <1){
                continue;
            }
            else{
                for(int i = 0; i < p.getChildren().size(); i++){
                    if(i >= numberOfFollowers){
                        // TODO: remove them as children, and this as parent.
                        p.getChildren().get(i).setParent(null);
                        p.removeChild(i);
                    } else {
                        // TODO: add this child to segments if it's not there already.
                        Pixel child = p.getChildren().get(i);
                        if(! segments.contains(child)){
                            visited.add(child);
                            mst.remove(child);

                        }
                    }

                }
            }
        }

        System.out.println("Done segmenting");
    }

}
