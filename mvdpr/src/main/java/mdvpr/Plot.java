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

    public void plotDepots(ArrayList<Depot> depotList){
        double[] x = new double[depotList.size()];
        double[] y = new double[depotList.size()];
        for(int i = 0; i < depotList.size(); i++){
            x[i] = depotList.get(i).getXpos();
            y[i] = depotList.get(i).getYpos();
        }
        System.out.println(x);

        this.plot.addScatterPlot("Depots", new Color(255,10,10), x, y);
        this.plot.updateUI();
    }

    public void plotCustomers(ArrayList<Customer> customerList){
        double[] x = new double[customerList.size()];
        double[] y = new double[customerList.size()];
        for(int i = 0; i < customerList.size(); i++){
            x[i] = customerList.get(i).getXpos();
            y[i] = customerList.get(i).getYpos();
        }
        System.out.println(x);

        this.plot.addScatterPlot("Depots", new Color(10,100,10), x, y);
        this.plot.updateUI();
    }


    public static void main(String[] args) {
        Plot plot = new Plot();


    }
}

