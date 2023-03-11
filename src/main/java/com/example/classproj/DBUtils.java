package com.example.classproj;

import eu.hansolo.fx.countries.tools.Connection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;

public class DBUtils {

    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username) {
        Parent root = null;

        //
        if (username != null) { // if username is not null, then we are logging in
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile)); // fxmlFile is the fxml file we want to load
                root = loader.load(); // load the fxml file
                LoggedInController controller = loader.getController(); // get the controller of the fxml file
                controller.setWelcomeLabel(username); // set the welcome label to the username

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                root = FXMLLoader.load(DBUtils.class.getResource(fxmlFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new javafx.scene.Scene(root, 600, 400));
        stage.show();

    }
        //--------------------------------------------//

    public static void signUpUsers(ActionEvent event, String username, String password) {

    }


    }



