package jssp;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class GanttChart {

    String[] job_colors;

    public GanttChart(Stage primaryStage, int num_machines, int num_jobs, int makespan, int[][][] schedule) throws Exception {
        super();
        Pane pane = new Pane();


        job_colors = new String[num_jobs];
        for(int i=0; i < num_jobs; i++){
            job_colors[i] = colorHexGenerator();
        }


        int height = 50;
        int width = 1200;
        double widthInterval = width /makespan;

        int timeUnit;
        if(makespan < 500){
            timeUnit = 5;
        }else{
            timeUnit = 25;
            width = makespan+50;
        }

        int y = 0;
        int interval = 0;

        for(int t = 50; t <= width+50; t += timeUnit*widthInterval){
            HBox line;
            Label timeString = new Label( Integer.toString((int)((t-50)/widthInterval)));
            timeString.setTranslateX(t-10);
            if(interval % 2 == 0){
                line = makeHbox(t, 0, 1, (num_machines*height)+10);
                timeString.setTranslateY((num_machines*50)+10);
            }else{
                line = makeHbox(t, 0, 1, (num_machines*height)+20);
                timeString.setTranslateY((num_machines*50)+20);
            }
            interval++;
            pane.getChildren().add(timeString);
            line.setStyle("-fx-background-color: gray" );
            pane.getChildren().add(line);
        }

        for(int m = 0; m < schedule.length; m++) {
            HBox machine = makeHbox(0, y, height, height);
            machine.setStyle("-fx-background-color: lightgrey" );
            int number = m +1;
            Label mLabl = new Label("M" + number);
            machine.getChildren().add(mLabl);
            machine.setAlignment(Pos.CENTER);
            pane.getChildren().add(machine);

            for (int j = 0; j < schedule[m].length; j++) {
                HBox operation = makeHbox((schedule[m][j][0]*widthInterval)+50 , y, schedule[m][j][1]*widthInterval, height);
                Label jobString = new Label(Integer.toString(j));
                operation.getChildren().add(jobString);
                operation.setAlignment(Pos.CENTER);
                operation.setStyle("-fx-background-color: " + job_colors[j]);
                pane.getChildren().add(operation);
            }
            y += 50;
        }
        primaryStage.setScene(new Scene(pane, width, height*(num_machines+1) ));
        primaryStage.show();
    }

    public String colorHexGenerator(){
        Random rg = new Random();
        int stringLength = 6;
        char[] chars = new char[]{'a','b','c','d','e','f','3','4','5','6','7','8','9'};
        String color = "#";
        int idx;
        for(int i = 0; i < stringLength; i++){
            Collections.shuffle(Arrays.asList(chars));
            idx = rg.nextInt(chars.length);
            color += chars[idx];
        }
        return color;
    }

    public HBox makeHbox(double x, int y, double width, int height){
        HBox hbox = new HBox();
        hbox.setTranslateX(x);
        hbox.setTranslateY(y);
        hbox.setPrefWidth(width);
        hbox.setPrefHeight(height);
        return hbox;
    }

}

