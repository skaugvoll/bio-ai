package jssp;


import org.apache.commons.cli.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Utils {

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


    public static CommandLine cli(String[] args) {
        CommandLine cmd;
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        Option task = Option.builder("t")
                .required(true)
                .desc("Choose which task to solve")
                .longOpt("-t")
                .build();

        Option algo = Option.builder("a")
                .required(true)
                .desc("Choose which algoritm to run")
                .longOpt("-a")
                .build();

        Option num_workers = Option.builder("w")
                .required(true)
                .desc("Number of workers in swarm")
                .longOpt("-w")
                .build();

        Option num_iterations = Option.builder("i")
                .required(true)
                .desc("How many iterations to run")
                .longOpt("-i")
                .build();


        task.setArgs(1);
        algo.setArgs(1);
        num_workers.setArgs(1);
        num_iterations.setArgs(1);

        options.addOption(task);
        options.addOption(algo);
        options.addOption(num_workers);
        options.addOption(num_iterations);



        try{
            cmd = parser.parse(options, args);
            String[] remainder = cmd.getArgs();

            if (remainder.length > 1) {
                System.out.print("Remaining arguments: " + Arrays.toString(remainder));
                for (String argument : remainder) {
                    System.out.print(argument);
                    System.out.print(" ");
                }

            }

//            String a = cmd.getOptionValue('a');
//            System.out.println(a);
//
//            String w = cmd.getOptionValue('w');
//            System.out.println(w);
//
//            String i = cmd.getOptionValue('i');
//            System.out.println(i);
            return cmd;


        } catch (ParseException e){
            System.out.print("Parse error: ");
            System.out.println(e.getMessage());
            formatter.printHelp("main", options);
            System.exit(0);
        }


        return null;
    }





}
