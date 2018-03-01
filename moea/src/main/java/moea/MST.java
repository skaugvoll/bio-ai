package moea;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MST {

    Color centrum;
    PixelFamily rootnode;
    HashMap<Pixel, PixelFamily> pixels;
    ArrayList<Pixel> pixelsArray;

    public MST(Pixel rootnode){
        this.rootnode = new PixelFamily(rootnode);
        this.pixels = new HashMap<>();
        this.pixelsArray = new ArrayList<>();
    }

    public boolean contains(Pixel pixel){
        return pixels.containsKey(pixel);
    }

    public void add(Pixel parent, Pixel child){
        PixelFamily pixelFamily = pixels.get(parent);

        if(pixelFamily == null){
            pixelFamily = new PixelFamily(parent);
            pixels.put(parent, pixelFamily);

        }
        pixelsArray.add(child);
        pixelFamily.add(child);

    }



}
