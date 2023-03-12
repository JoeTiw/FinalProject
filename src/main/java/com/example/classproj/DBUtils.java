package com.example.classproj;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.*;

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

        Connection conn = null;  // connection to the database
        PreparedStatement chkUserExist = null; // check if the user exists
        ResultSet resultSet = null;     // results of the query

        try {
            String url = "jdbc:mysql://127.0.0.1:3306/java_fx?useSSL=false&serverTimezone=UTC";
            String usrname = "root";
            String psswd = "password";
            conn = DriverManager.getConnection(url, usrname, psswd);
            chkUserExist = conn.prepareStatement("SELECT * FROM users WHERE username = ?"); // check if the user exists
            chkUserExist.setString(1, username); // set the username
            resultSet = chkUserExist.executeQuery(); // execute the query

            if (resultSet.isBeforeFirst()) { // if the user exists
                System.out.println("User already exists");
                Alert alert = new Alert(Alert.AlertType.ERROR); // create an alert
                alert.setTitle("Username is Taken");
                alert.show();
            } else { // if the user does not exist
                PreparedStatement insertUser = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)"); // insert the user into the database
                insertUser.setString(1, username); // set the username
                insertUser.setString(2, password); // set the password
                insertUser.executeUpdate(); // execute the query
                changeScene(event, "logged-in.fxml", "Welcome", username);

            }


        } catch (SQLException e) {
            e.printStackTrace();
            //finally block used to close resources so that there is no memory leak
        } finally {
            try {
                if (resultSet != null) resultSet.close(); // close the result set
                if (chkUserExist != null) chkUserExist.close(); // close the prepared statement
                if (conn != null) conn.close(); // close the connection
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    public static void loginUsers(ActionEvent event, String username, String password) {

        Connection conn = null;  // connection to the database
        PreparedStatement preparedStatement = null; // check if the user exists
        ResultSet resultSet = null;     // results of the query





        try {


            //conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_fx?useSSL=false&serverTimezone=UTC", "root", "password");
            String url = "jdbc:mysql://127.0.0.1:3306/java_fx?useSSL=false&serverTimezone=UTC";
            String usrname = "root";
            String psswd = "password";

            conn = DriverManager.getConnection(url, usrname, psswd);
            if(!conn.isClosed()){
                Alert alert = new Alert(Alert.AlertType.ERROR); // create an alert
                alert.setContentText("Connection is closed");
                alert.show();
            }
            preparedStatement = conn.prepareStatement("SELECT password FROM users WHERE username = ?");
            preparedStatement.setString(1, username); // set the username
            resultSet = preparedStatement.executeQuery(); // execute the query

            if (!resultSet.isBeforeFirst()) { // if the user exists
               System.out.println("User does not exist");
                Alert alert = new Alert(Alert.AlertType.ERROR); // create an alert
                alert.setContentText("Enter correct username and password");
                alert.show();
            } else {
                while (resultSet.next()) {
                    String retrievedPassword = resultSet.getString("password");


                    if (retrievedPassword.equals(password)) {
                        changeScene(event, "logged-in.fxml", "Welcome", username);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR); // create an alert
                        alert.setContentText("Enter correct username and password");
                        alert.show();
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close(); // close the result set
                if (preparedStatement != null) preparedStatement.close(); // close the prepared statement
                if (conn != null) conn.close(); // close the connection
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

}



