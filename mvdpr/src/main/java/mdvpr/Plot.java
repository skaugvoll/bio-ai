package mdvpr;

import org.math.plot.Plot2DPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

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
//        System.out.println(x);

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
//        System.out.println(x);

        this.plot.addScatterPlot("Depots", new Color(10,100,10), x, y);
        this.plot.updateUI();
    }


    public void plotChromosome(Chromosome chromosome){
        Random cg = new Random(); // random color generator (cr).
        // tegn ruten for hver bil

        for(Vehicle v : chromosome.getCars()){
            Color color = new Color(cg.nextInt(255),cg.nextInt(255),cg.nextInt(255));

            if(v.getPath().size() < 1){
                continue;
            }

            // Create two arrays to contain the x and y positions of the rout points. specific for plotting library
            //  depot(leave) + there will be as many x positions as there is customers + depor (home)
            double[] x = new double[v.getPath().size() + 2];
            double[] y = new double[v.getPath().size() + 2];

            double homeX = v.getDepo().getXpos();
            double homeY = v.getDepo().getYpos();

            // Add depot
            x[0] = homeX;
            y[0] = homeY;

            // since depo takes index 0, start loop at 1, and remove 1 from the index when accessing path-array, not x/y array.
            for (int i = 1; i < v.getPath().size() + 1; i++) {
                x[i] = v.getPath().get(i-1).getXpos();
                y[i] = v.getPath().get(i-1).getYpos();
            }

            // add driving home to depot as the last thing the car does.
            x[x.length-1] = homeX;
            y[y.length-1] = homeY;

            this.plot.addLinePlot("Car route", color, x, y);
        }


    }

    public void updateUI(){
        this.plot.updateUI();
    }


    public static void main(String[] args) {
        Plot plot = new Plot();


    }
}

