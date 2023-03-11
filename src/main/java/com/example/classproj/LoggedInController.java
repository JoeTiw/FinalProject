package com.example.classproj;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class LoggedInController implements Initializable {

    @FXML
    private Button logout_button;

    @FXML
    private Label welcome_label;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        logout_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.changeScene(event, "hello-view.fxml", "Hello!", null);
            }
        });
    }

    public void setWelcomeLabel(String username) {
        welcome_label.setText("Welcome, " + username);
    }
}
