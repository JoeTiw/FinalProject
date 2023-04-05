package com.example.classproj;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Button loginbutton;
    @FXML
    private Button signupbutton;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Circle profilePicCircle;

        @Override
        public void initialize(java.net.URL url, ResourceBundle resourceBundle) {

           loginbutton.setOnAction(new EventHandler<ActionEvent>() {
               @Override
               public void handle(ActionEvent event) {
                     DBUtils.loginUsers(event, username.getText(), password.getText());
               }
           });

              signupbutton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // once button is clicked it will go to the sign up page
                    DBUtils.changeScene(event, "sign-up.fxml", "Sign Up", null);

                }
              });
        }

    public void userLogin(ActionEvent event) {
        DBUtils.loginUsers(event, username.getText(), password.getText());
    }


}
