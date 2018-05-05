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

public class GanttChart {

    public GanttChart(Stage primaryStage, int num_machines, int makespan, int[][][] schedule) throws Exception {
        super();
        GridPane grid = new GridPane();
        grid.setGridLinesVisible(true);

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

//        for(int m = 1; m < schedule.length+1; m++){
//            for(int j = 0; j < schedule[m-1].length; j++){
//                for(int pixelPos = schedule[m-1][j][0]*scale; pixelPos < schedule[m-1][j][0]*scale + schedule[m-1][j][1]*scale; pixelPos++){
//                    for(int row = currentRow; row < currentRow + 10; row++){
//                        int p = (255 << 24) | (job_colors[j][0] << 16) | (job_colors[j][1] << 8) | job_colors[j][2];
//                        newImage.setRGB(pixelPos, row, p);
//                    }
//                }
//            }
//            currentRow += 10;
//        }

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
                    for(int operation = 0; operation < schedule[m][j].length; operation++){
                        int[] op = schedule[m][operation];
                        


                    }
                }
//                    for(int cell = schedule[m][j+1][0]; pixelPos < schedule[m][j+1][0] + schedule[m][j+1][1]; pixelPos++){
//                        HBox cellbox = new HBox();
//                        cellbox.setStyle("-fx-background-color: #80ff80;");
//                        grid.add(cellbox, j, pixelPos);
//                    }
//                if(path.contains(board.get(i).get(j))){
//                    javafx.scene.control.Label inPath = new javafx.scene.control.Label("");
//                    javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResourceAsStream("dot.png"));
//                    inPath.setGraphic(new ImageView(image));
//                    cellbox.getChildren().add(inPath);
//                }
//                if((closedSet.contains(board.get(i).get(j)) || openSet.contains(board.get(i).get(j))) && !path.contains(board.get(i).get(j))){
//                    if (closedSet.contains(board.get(i).get(j))){
//                        javafx.scene.control.Label inClosed = new javafx.scene.control.Label("");
//                        javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResourceAsStream("cross.png"));
//                        inClosed.setGraphic(new ImageView(image));
//
//                        cellbox.getChildren().add(inClosed);
//                    }else{
//                        javafx.scene.control.Label inOpen = new Label("");
//                        javafx.scene.image.Image image = new Image(getClass().getResourceAsStream("star.png"));
//                        inOpen.setGraphic(new ImageView(image));
//                        cellbox.getChildren().add(inOpen);
//                    }
//                }
//                if(board.get(i).get(j).getType() == '#') {
//                    cellbox.setStyle("-fx-background-color: #000000;");
//                }if(board.get(i).get(j).getType() == 'w'){
//                    cellbox.setStyle("-fx-background-color: #4d4dff;");
//                }if(board.get(i).get(j).getType() == 'm'){
//                    cellbox.setStyle("-fx-background-color: #a6a6a6;");
//                }if(board.get(i).get(j).getType() == 'f'){
//                    cellbox.setStyle("-fx-background-color: #008000;");
//                }if(board.get(i).get(j).getType() == 'g'){
//                    cellbox.setStyle("-fx-background-color: #80ff80;");
//                }if(board.get(i).get(j).getType() == 'r'){
//                    cellbox.setStyle("-fx-background-color: #bf8040;");
//                }if(board.get(i).get(j).getType() == 'A'){
//                    cellbox.setStyle("-fx-background-color: #ff0000;");
//                }if(board.get(i).get(j).getType() == 'B'){
//                    cellbox.setStyle("-fx-background-color: #00ff00;");
//                }

            }
        }

        primaryStage.setScene(new Scene(grid, makespan*10, height*num_machines ));
        primaryStage.show();
    }
}

//
//        for(int i = 0; i < board.size(); i++) {
//            for (int j = 0; j < board.get(0).size(); j++) {
//                HBox cellbox = new HBox();
//                if(path.contains(board.get(i).get(j))){
//                    javafx.scene.control.Label inPath = new javafx.scene.control.Label("");
//                    javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResourceAsStream("dot.png"));
//                    inPath.setGraphic(new ImageView(image));
//                    cellbox.getChildren().add(inPath);
//                }
//                if((closedSet.contains(board.get(i).get(j)) || openSet.contains(board.get(i).get(j))) && !path.contains(board.get(i).get(j))){
//                    if (closedSet.contains(board.get(i).get(j))){
//                        javafx.scene.control.Label inClosed = new javafx.scene.control.Label("");
//                        javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResourceAsStream("cross.png"));
//                        inClosed.setGraphic(new ImageView(image));
//
//                        cellbox.getChildren().add(inClosed);
//                    }else{
//                        javafx.scene.control.Label inOpen = new Label("");
//                        javafx.scene.image.Image image = new Image(getClass().getResourceAsStream("star.png"));
//                        inOpen.setGraphic(new ImageView(image));
//                        cellbox.getChildren().add(inOpen);
//                    }
//                }
//                if(board.get(i).get(j).getType() == '#') {
//                    cellbox.setStyle("-fx-background-color: #000000;");
//                }if(board.get(i).get(j).getType() == 'w'){
//                    cellbox.setStyle("-fx-background-color: #4d4dff;");
//                }if(board.get(i).get(j).getType() == 'm'){
//                    cellbox.setStyle("-fx-background-color: #a6a6a6;");
//                }if(board.get(i).get(j).getType() == 'f'){
//                    cellbox.setStyle("-fx-background-color: #008000;");
//                }if(board.get(i).get(j).getType() == 'g'){
//                    cellbox.setStyle("-fx-background-color: #80ff80;");
//                }if(board.get(i).get(j).getType() == 'r'){
//                    cellbox.setStyle("-fx-background-color: #bf8040;");
//                }if(board.get(i).get(j).getType() == 'A'){
//                    cellbox.setStyle("-fx-background-color: #ff0000;");
//                }if(board.get(i).get(j).getType() == 'B'){
//                    cellbox.setStyle("-fx-background-color: #00ff00;");
//                }
//                grid.add(cellbox, j, i);
//            }
//        }