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

public class Controller implements Initializable {

    @FXML
    private Button loginbutton;
    @FXML
    private Button signupbutton;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;
        @Override
        public void initialize(java.net.URL url, ResourceBundle resourceBundle) {

           loginbutton.setOnAction(new EventHandler<ActionEvent>() {
               @Override
               public void handle(ActionEvent event) {
                     DBUtils.loginUsers(event, username.getText(), password.getText());
                     // once button is clicked it will go to the logged in page
                   //if username is "admin" and password is "admin" then it will go to the logged in page else it will stay on the login page

//                     if(username.getText().equals("admin") && password.getText().equals("admin")) {
//                         DBUtils.changeScene(event, "logged-in.fxml", "Logged In", null);
//                     } else{
//                            System.out.println("Login Failed");
//                         Alert alert = new Alert(Alert.AlertType.ERROR);
//                            alert.setContentText("Login Failed");
//                            alert.show();
//                     }

                        //DBUtils.changeScene(event, "logged-in.fxml", "Logged In", null);
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
