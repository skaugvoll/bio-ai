package mdvpr;

import org.math.plot.Plot2DPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Plot {

    Plot2DPanel plot;
    JFrame frame;


    public Plot() {
        this.plot = new Plot2DPanel();
        this.frame = new JFrame("MDVPR");
        this.frame.setMinimumSize(new Dimension(700, 700));
        this.addToJFrame();

    }

    private void addToJFrame() {
        this.frame.getContentPane().add(this.plot, BorderLayout.CENTER);
        this.frame.pack();
        this.frame.setVisible(true);
    }

    public void plot(ArrayList<Depot> objectList){
        double[] x = new double[objectList.size()];
        double[] y = new double[objectList.size()];
        for(int i = 0; i < objectList.size(); i++){
            x[i] = objectList.get(i).getXpos();
            y[i] = objectList.get(i).getYpos();
        }
        System.out.println(x);

        this.plot.addScatterPlot("Depots", new Color(255,10,10), x, y);
        this.plot.updateUI();
    }

    public static void main(String[] args) {
        Plot plot = new Plot();


    }
}

