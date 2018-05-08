package jssp;

import javafx.application.Application;
import javafx.stage.Stage;
import jssp.ACO.ACO;
import jssp.BA.BA;
import org.apache.commons.cli.CommandLine;


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

        CommandLine cmd = Utils.cli(args);
        int task = Integer.valueOf(cmd.getOptionValue('t'));
        String algorithm = cmd.getOptionValue('a');
        int workers = Integer.valueOf(cmd.getOptionValue('w'));
        int iterations = Integer.valueOf(cmd.getOptionValue('i'));


        DataGenerator dg = new DataGenerator(task);
        Job[] jobs = dg.getJobs();

        Main.num_machines = dg.getNumMachines();
        Main.num_jobs = dg.getNumJobs();
        Main.bestPossibleMakespan = dg.getBestPossibleMakespan();

        Solution solution = null;
        if (algorithm.equals("aco")){
            System.out.println("Solving aco...");
            solution = new ACO(jobs, num_machines, num_jobs, workers).solve(iterations);
        }
        else if(algorithm.equals("ba")){
            System.out.println("Solving ba...");
            solution = new BA(jobs, num_machines, num_jobs, workers).solve(iterations);
        }
        else {
            System.out.println("Not valid algorithm specified\n -a <algo>\nAlgo {aco, ba}");
            System.exit(0);
        }

        Main.makespan = solution.getMakespan();
        Main.schedule = solution.getSchedule();
        System.out.println("Makespan:" + Main.makespan);
//        Utils.drawImage(schedule, num_jobs, num_machines, makespan);
//        Utils.openImage("gant");
        launch(args);


    }


    public void start(Stage primaryStage) throws Exception {

        // JavaFX Initialization
        gantt = new GanttChart(primaryStage, num_machines, num_jobs, makespan, schedule);


        //Dev
//        run("ACO", "1");
    }


}
