package mdvpr;

import org.math.plot.utils.Array;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class DataGenerator {

    public String dataPath;
    public String solutionPath;

    private int numVehicles;
    private int numCustomers;
    private int numDepots;

    private ArrayList<Depot> depots = new ArrayList<Depot>();
    private ArrayList<Customer> customers = new ArrayList<Customer>();

    public DataGenerator(String filename){

        this.dataPath = "/DataFiles/" + filename;
        this.solutionPath = "/SolutionFiles/" + filename + ".res";
        InputStream is = DataGenerator.class.getResourceAsStream(this.dataPath);
        try{
            this.readfile(is);
        }
        catch (Exception e){
            System.out.println("Fåkk off: " + e);
        }


    }


    private void readfile(InputStream path){
        Scanner scanner = new Scanner(path);
        int counter = 1;
        int t = 0;
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            line = line.trim();
            String[] lineSplit = line.split(" ");
            if(line.equals("")){
                break;
            }
            ArrayList<String> partList = new ArrayList<>(Arrays.asList(lineSplit));
            List<String> partsReduced = partList.stream().
                    filter(
                            f -> !f.equals("")
                    ).
                    collect(
                            Collectors.toList()
                    );

            String[] parts = new String[partsReduced.size()];
            parts = partsReduced.toArray(parts);


            if(counter == 1){
                this.numVehicles = Integer.parseInt(parts[0]);
                this.numCustomers = Integer.parseInt(parts[1]);
                this.numDepots = Integer.parseInt(parts[2]);
            }

            else if(counter > 1 && counter <= this.numDepots+1){
                this.depots.add(new Depot(counter-1, Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), numVehicles));
            }

            else if(counter > numDepots + 1 && counter <= this.numCustomers + (numDepots+1)){
//                System.out.println(Arrays.toString(parts));
                this.customers.add(new Customer(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4])));
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

    public ArrayList<Depot> getDepots() {
        return depots;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public static void main(String[] args) {
        DataGenerator dg = new DataGenerator("p01");
    }





}
