package jssp;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class GanttChart {

    String[] job_colors;

    public GanttChart(Stage primaryStage, int num_machines, int num_jobs, int makespan, int[][][] schedule) throws Exception {
        super();
        GridPane grid = new GridPane();
        grid.setGridLinesVisible(true);

        job_colors = new String[num_jobs];
        for(int i=0; i < num_jobs; i++){
            job_colors[i] = colorGenerator();
        }


        int height = 40;
        int scale = 1;

        for (int i = 0; i < makespan + 1; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            if(i == 0){
                colConst.setPrefWidth(40);
            }else {
                colConst.setPrefWidth(10);
            }
            grid.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < num_machines+1; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPrefHeight(40);
            grid.getRowConstraints().add(rowConst);

        }

        for(int m = 0; m < schedule.length; m++) {
            for (int j = 0; j < schedule[m].length+1; j++) {
                if(j == 0){
                    HBox cellbox = new HBox();
                    int number = m +1;
                    Label machine = new Label("M" + number);
                    cellbox.getChildren().add(machine);
                    cellbox.setAlignment(Pos.CENTER);
                    grid.add(cellbox, j, m);
                }else{
                    for(int cell = schedule[m][j-1][0]; cell < schedule[m][j-1][0] + schedule[m][j-1][1]; cell++){
                        HBox cellbox = new HBox();
                        cellbox.setStyle("-fx-background-color: " + job_colors[j-1]);
                        grid.add(cellbox, cell+1, m);
                    }
                }
            }
        }

        primaryStage.setScene(new Scene(grid, makespan*10, height*num_machines ));
        primaryStage.show();
    }

    public String colorGenerator(){
        Random rg = new Random();
        int stringLength = 6;
        char[] chars = new char[]{'a','b','c','d','e','f','0','1','2','3','4','5','6','7','8','9'};
        String color = "#";
        int idx;
        for(int i = 0; i < stringLength; i++){
            Collections.shuffle(Arrays.asList(chars));
            idx = rg.nextInt(chars.length);
            color += chars[idx];
        }
        return color;
    }



}

