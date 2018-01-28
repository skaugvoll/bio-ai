package mdvpr;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

public class DataGenerator {

    public String dataPath;
    public String solutionpath;

    public DataGenerator(String filename){

        this.dataPath = "/DataFiles/" + filename;
        this.dataPath = "/SolutionFiles/" + filename;
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
        while(scanner.hasNextLine()){
            System.out.println(scanner.nextLine());
        }
        scanner.close();
    }

    public static void main(String[] args) {
        DataGenerator dg = new DataGenerator("p01");
    }





}
