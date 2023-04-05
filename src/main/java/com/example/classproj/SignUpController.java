package com.example.classproj;


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
        private TextField signUpUsername;

        @FXML
        private PasswordField signUpPassword;

        @Override
        public void initialize(java.net.URL url, ResourceBundle resourceBundle) {

                buttonToSignUp.setOnAction(event -> {
                        if(!signUpUsername.getText().isEmpty() && !signUpPassword.getText().isEmpty()) {
                                DBUtils.signUpUsers(event, signUpUsername.getText(), signUpPassword.getText());
                               // DBUtils.changeScene(event, "login.fxml", "Login", null);


                        } else {
                                System.out.println("Sign Up Failed");
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setContentText("Sign Up Failed");
                                alert.show();
                        }

                });

                buttonToLogin.setOnAction(event -> {
                    DBUtils.changeScene(event, "hello-view.fxml", "JJK Tracker", null);
                });




        }



}
