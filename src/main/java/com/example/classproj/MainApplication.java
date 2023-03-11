package com.example.classproj;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {


    @Override
    public void start(Stage mainStage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        mainStage.setTitle("Hello!");
        mainStage.setScene(scene);
        mainStage.show();
    }



    public static void main(String[] args) {
        launch();
    }
}