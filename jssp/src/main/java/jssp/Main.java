package jssp;

import jssp.ACO.ACO;
import jssp.ACO.Solution;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Main {

    public static void greetings(){
        System.out.println("Hello jssp, lets get you solved");
    }

    public static void main(String[] args) {
        Main.greetings();

        DataGenerator dg = new DataGenerator(1);
        Job[] jobs = dg.getJobs();

        int num_machines = dg.getNumMachines();
        int num_jobs = dg.getNumJobs();
        int bestPossibleMakespan = dg.getBestPossibleMakespan();

        Solution solution = new ACO(jobs, num_machines, num_jobs, bestPossibleMakespan, 100).solve(10);
        drawImage(solution.getSchedule(), num_jobs, num_machines, solution.getMakespan());
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
}
