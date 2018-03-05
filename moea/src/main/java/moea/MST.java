package moea;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MST {

    public Pixel rootnode;
    public ArrayList<Edge> edges;
    public HashMap<Pixel, ArrayList<Edge>> pixelEdges = new HashMap<>();
    public ArrayList<Pixel> fuckersVisited;


    public MST(Pixel rootnode){
        this.rootnode =rootnode;
        this.edges = new ArrayList<>();
        this.fuckersVisited = new ArrayList<>();
        fuckersVisited.add(rootnode);

    }

    public void addEdge(Edge e){
        this.edges.add(e);
        if(pixelEdges.containsKey(e.getCurrentPixel())){
            pixelEdges.get(e.getCurrentPixel()).add(e);
        }else{
            ArrayList<Edge> edgeList = new ArrayList<>(Arrays.asList(e));
            pixelEdges.put(e.getCurrentPixel(), edgeList);

        }

    }



}
