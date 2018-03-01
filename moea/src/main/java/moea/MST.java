package moea;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MST {

    public Pixel rootnode;
    public ArrayList<Edge> edges;
    public ArrayList<Pixel> fuckersVisited;


    public MST(Pixel rootnode){
        this.rootnode =rootnode;
        this.edges = new ArrayList<>();
        this.fuckersVisited = new ArrayList<>();
        fuckersVisited.add(rootnode);

    }

    public void addEdge(Edge e){
        this.edges.add(e);
    }



}
