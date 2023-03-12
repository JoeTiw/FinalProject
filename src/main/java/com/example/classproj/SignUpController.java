package com.example.classproj;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.ResourceBundle;

public class SignUpController implements Initializable {

    @FXML
    private Button buttonToSignUp;

    @FXML
    private Button buttonToLogin;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;


    @Override
    public void initialize(java.net.URL url, ResourceBundle resourceBundle) {

        buttonToSignUp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!username.getText().trim().isEmpty() && !password.getText().trim().isEmpty()) {
                    DBUtils.signUpUsers(event, username.getText(), password.getText());
                }
                else {
                    System.out.println("Please enter a username and password");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Please enter a username and password");
                    alert.show();
                }
            }
        });

        buttonToLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.changeScene(event, "hello-view.fxml", "Hello!", null);
            }
        });

    }

}
