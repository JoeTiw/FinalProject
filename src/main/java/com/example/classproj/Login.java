package com.example.classproj;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class Login {

    public void LogIn(){

    }

    @FXML
    private Button loginButton;

    @FXML
    private Label loginError;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    public void userLogin(ActionEvent event) throws IOException{
        checkLogin();
    }

    public void checkLogin() throws IOException{
        if(username.getText().equals("admin") && password.getText().equals("admin")){
            loginError.setText("Login Successful");
        }else{
            loginError.setText("Login Failed");
        }
    }

}
