package com.p3ps0n.robotarm;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("resources/fxml/Main.fxml"));

        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Robot Arm controller");
        stage.setResizable(false);
        stage.show();
    }
}
