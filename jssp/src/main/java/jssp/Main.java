package jssp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import jssp.ACO.ACO;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static javafx.application.Application.launch;

public class Main extends Application{

    GanttChart gantt;
    static int num_machines;
    static int num_jobs;
    static int bestPossibleMakespan;
    static int makespan;
    static int[][][] schedule;

    public static void greetings(){
        System.out.println("Hello jssp, lets get you solved");
    }

    public static void main(String[] args) {
        Main.greetings();

        DataGenerator dg = new DataGenerator(1);
        Job[] jobs = dg.getJobs();

        Main.num_machines = dg.getNumMachines();
        Main.num_jobs = dg.getNumJobs();
        Main.bestPossibleMakespan = dg.getBestPossibleMakespan();

        Solution solution = new ACO(jobs, num_machines, num_jobs, bestPossibleMakespan, 100).solve(10);

        Main.makespan = solution.getMakespan();
        Main.schedule = solution.getSchedule();
        drawImage(schedule, num_jobs, num_machines, makespan);
//        openImage("gant");
        launch(args);

    }

    public static void drawImage(int[][][] schedule, int num_jobs, int num_machines, int makespan){
        System.out.println(makespan);
        int width = makespan * 10;
        int height = num_machines * 10 + 10;
        int scale = 10;
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        File f = null;

        int currentRow = 5;

        int[][] job_colors = new int[num_jobs][3];
        Random rg = new Random();
        for(int jc = 0; jc < num_jobs; jc++){
            int r = rg.nextInt(256);
            int g = rg.nextInt(256);
            int b = rg.nextInt(256);
            job_colors[jc] = new int[]{r,g,b};
        }

        for(int m = 1; m < schedule.length+1; m++){
                for(int j = 0; j < schedule[m-1].length; j++){
                    for(int pixelPos = schedule[m-1][j][0]*scale; pixelPos < schedule[m-1][j][0]*scale + schedule[m-1][j][1]*scale; pixelPos++){
                        for(int row = currentRow; row < currentRow + 10; row++){
                            int p = (255 << 24) | (job_colors[j][0] << 16) | (job_colors[j][1] << 8) | job_colors[j][2];
                            newImage.setRGB(pixelPos, row, p);
                        }
                    }
                }
                currentRow += 10;
        }

        try{
            f = new File(System.getProperty("user.dir") + "/src/main/resources/Output/gant.png");
            ImageIO.write(newImage, "png", f);
        }catch(IOException e){
            System.out.println("Kunne ikke skrive ut fil");
        }
    }


    public static void openImage(String filename){
        if (Desktop.isDesktopSupported()) {
            try {
                File img = new File(System.getProperty("user.dir") + "/src/main/resources/Output/"+filename+".png");
                Desktop.getDesktop().open(img);
            } catch (IOException ex) {
                // no application registered for PDFs
                System.out.println("Could not launch the image");
            }
        }


    }

    public void start(Stage primaryStage) throws Exception {

        // JavaFX Initialization
        gantt = new GanttChart(primaryStage, num_machines, num_jobs, makespan, schedule);


        //Dev
//        run("ACO", "1");
    }


}
