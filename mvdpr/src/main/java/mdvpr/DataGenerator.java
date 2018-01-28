package mdvpr;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class DataGenerator {

    public String dataPath;
    public String solutionPath;

    private int numVehicles;
    private int numCustomers;
    private int numDepots;

    private ArrayList<Depot> depots = new ArrayList<Depot>();

    public DataGenerator(String filename){

        this.dataPath = "/DataFiles/" + filename;
        this.solutionPath = "/SolutionFiles/" + filename + ".res";
        InputStream is = DataGenerator.class.getResourceAsStream(this.dataPath);
        try{
            this.readfile(is);
        }
        catch (Exception e){
            System.out.println("Fåkk off");
        }


    }

    private void readfile(InputStream path){
        Scanner scanner = new Scanner(path);
        int counter = 1;
        int t = 0;
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            line = line.trim();
            String[] parts = line.split(" ");

            if(counter == 1){
                this.numVehicles = Integer.parseInt(parts[0]);
                this.numCustomers = Integer.parseInt(parts[1]);
                this.numDepots = Integer.parseInt(parts[2]);
            }

            else if(counter > 1 && counter < this.numDepots+1){
                this.depots.add(new Depot(counter-1, Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
            }

            else if(counter > numDepots + 1 && counter < this.numCustomers + numDepots+1){
                // create customers
            }

            else{ // update depots
                Depot d = this.depots.get(t);
                d.setPos(Integer.parseInt(parts[1]),Integer.parseInt(parts[2]));
                t++;
            }


            counter++;
        }
        scanner.close();
    }

    public static void main(String[] args) {
        DataGenerator dg = new DataGenerator("p01");
    }





}
