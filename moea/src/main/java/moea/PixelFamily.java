package moea;

import java.util.ArrayList;

public class PixelFamily {

    public final Pixel parent;
    public ArrayList<Pixel> children;

    public PixelFamily(Pixel p){
        this.parent = p;
        this.children = new ArrayList<>();
    }

    public void add(Pixel child) {
        children.add(child);
    }


}
