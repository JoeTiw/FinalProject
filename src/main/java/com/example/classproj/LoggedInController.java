package com.example.classproj;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.sql.*;
import java.util.ResourceBundle;


public class LoggedInController implements Initializable {



    @FXML
    private Button logout_button;

    @FXML
    private Label welcome_label;

    @FXML
    private Circle profilePicCircle;

    @FXML
    private Button addCashBtn;

    private double totalCash;

    @FXML
    private Label totalLabel;

    private int userId;

    private String loggedInUsername;

    @FXML
    private Label shopLabel;

    @FXML
    private Label foodLabel;

    @FXML
    private Label billsLabel;

    @FXML
    private Label otherLabel;

    @FXML
    private Button submitButton;

    @FXML
    private CheckBox foodCheckBox;

    @FXML
    private CheckBox billsCheckBox;

    @FXML
    private CheckBox shopCheckBox;

    @FXML
    private CheckBox otherCheckBox;






    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        //--------Profile picture start---------- //

        profilePicCircle.setStroke(Color.SEAGREEN);

        // Load the image from a URL
        Image image = new Image("GroupLogo.png", false);

        // Check if the image was loaded successfully
        if (image.isError()) {
            System.out.println("Error loading image");
        } else {
            profilePicCircle.setFill(new ImagePattern(image));
            profilePicCircle.setEffect(new DropShadow(+25d, 0d, +2d, Color.DARKSEAGREEN));
        }

        //--------Profile picture End---------- //



        //--------Add Cash start---------- //
        addCashBtn.setOnAction(event -> {
            // Create a new stage for the pop-up window
            Stage popupStage = new Stage();
            popupStage.setTitle("Add Cash");

            // Create a label and text field for the user to enter a cash amount
            Label label = new Label("Enter the amount of cash to add:");
            TextField textField = new TextField();
            textField.setPromptText("Cash amount");

            // Create a button to submit the cash amount
            Button submitBtn = new Button("Add Cash");
            submitBtn.setOnAction(submitEvent -> {
                try {
                    // Parse the cash amount entered by the user
                    double cashAmount = Double.parseDouble(textField.getText());

                    // Update the total cash variable
                    totalCash += cashAmount;

                    // Update the Total amount on the logged-in view
                    totalLabel.setText(String.format("%.2f", totalCash));

                    //System.out.println(totalCash);

                    // Retrieve the user ID for the logged-in user
                    Connection conn = null;
                    PreparedStatement pstmt = null;
                    ResultSet resultSet = null;
                    try {
                        String urll = "jdbc:mysql://classproj.c4pj5kawvmlt.us-east-2.rds.amazonaws.com:3307/java_fx?useSSL=true";
                        String username = "admin";
                        String password = "Ur05x^$4qL&F";
                        conn = DriverManager.getConnection(urll, username, password);

                        String updateQuery = "UPDATE users SET total = ? WHERE user_id = ?"; // Update the total cash for the user
                        pstmt = conn.prepareStatement(updateQuery);
                        //System.out.println("Executing query: " + updateQuery + " with totalCash=" + totalCash + " and userId=" + userId);
                        pstmt.setDouble(1, totalCash);
                        pstmt.setInt(2, userId);
                        System.out.println(pstmt.toString());

                        pstmt.executeUpdate();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (pstmt != null) pstmt.close();
                            if (conn != null) conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    // Close the pop-up window
                    popupStage.close();
                } catch (NumberFormatException e) {
                    // If the user entered an invalid number, show an error message
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a valid number.");
                    alert.showAndWait();
                }
            });

            // Create a VBox to hold the label, text field, and submit button
            VBox vbox = new VBox(label, textField, submitBtn);
            vbox.setSpacing(10);
            vbox.setPadding(new Insets(10));

            // Create a scene for the pop-up window and set it on the stage
            Scene popupScene = new Scene(vbox);
            popupStage.setScene(popupScene);

            // Show the pop-up window
            popupStage.show();
        });

        //-----------------------------------------------------------------------------------------//


        logout_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.changeScene(event, "hello-view.fxml", "JJK Tracker!", null);
            }
        });
    }



    // Set the welcome label to display the username of the logged-in user
    public void setWelcomeLabel(String username) {
        welcome_label.setText("Welcome, " + username);
        loggedInUsername = username;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            String urll = "jdbc:mysql://classproj.c4pj5kawvmlt.us-east-2.rds.amazonaws.com:3307/java_fx?useSSL=true";
            String usrname = "admin";
            String psswd = "Ur05x^$4qL&F";
            conn = DriverManager.getConnection(urll, usrname, psswd);

            pstmt = conn.prepareStatement("SELECT user_id FROM users WHERE username = ?");
            pstmt.setString(1, loggedInUsername);
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                userId = resultSet.getInt("user_id"); // Get the user ID

                // Set the total cash for the user in the UI
                pstmt = conn.prepareStatement("SELECT total FROM users WHERE user_id = ?"); // Get the total cash for the user
                pstmt.setInt(1, userId); // Set the user ID
                resultSet = pstmt.executeQuery(); // Execute the query
                if (resultSet.next()) {
                    totalCash = resultSet.getDouble("total"); // Get the total cash for the user
                    totalLabel.setText(Double.toString(totalCash)); // Set the total cash for the user in the UI
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        submitButton.setOnAction(event -> {
            // Get the selected categories and the corresponding amount
            double shopAmount = 0, foodAmount = 0, billsAmount = 0, otherAmount = 0;
            if (shopCheckBox.isSelected()) {
                shopAmount = Double.parseDouble(shopLabel.getText());
            }
            if (foodCheckBox.isSelected()) {
                foodAmount = Double.parseDouble(foodLabel.getText());
            }
            if (billsCheckBox.isSelected()) {
                billsAmount = Double.parseDouble(billsLabel.getText());
            }
            if (otherCheckBox.isSelected()) {
                otherAmount = Double.parseDouble(otherLabel.getText());
            }

            // Update the database with the expenditure information
            Connection connn = null;
            PreparedStatement pstmtt = null;
            ResultSet resultSett = null;
            try {
                String urll = "jdbc:mysql://classproj.c4pj5kawvmlt.us-east-2.rds.amazonaws.com:3307/java_fx?useSSL=true";
                String usrname = "admin";
                String psswd = "Ur05x^$4qL&F";
                connn = DriverManager.getConnection(urll, usrname, psswd);

                // Insert the expenditure information into the expenses table
                pstmtt = connn.prepareStatement("INSERT INTO categories (userId, shopping, food, bills, other) VALUES (?, ?, ?, ?, ?)");
                pstmtt.setInt(1, userId);
                pstmtt.setDouble(2, shopAmount);
                pstmtt.setDouble(3, foodAmount);
                pstmtt.setDouble(4, billsAmount);
                pstmtt.setDouble(5, otherAmount);
                pstmtt.executeUpdate();

                // Update the total cash for the user in the users table
                double totalSpent = shopAmount + foodAmount + billsAmount + otherAmount;
                totalCash -= totalSpent;
                pstmtt = connn.prepareStatement("UPDATE users SET total = ? WHERE user_id = ?");
                pstmtt.setDouble(1, totalCash);
                pstmtt.setInt(2, userId);
                pstmtt.executeUpdate();

                // Update the total label in the UI
                totalLabel.setText(String.format("%.2f", totalCash));

                // Update the category labels in the UI
                if (shopAmount > 0) {
                    shopLabel.setText(String.format("%.2f", Double.parseDouble(shopLabel.getText()) + shopAmount));
                }
                if (foodAmount > 0) {
                    foodLabel.setText(String.format("%.2f", Double.parseDouble(foodLabel.getText()) + foodAmount));
                }
                if (billsAmount > 0) {
                    billsLabel.setText(String.format("%.2f", Double.parseDouble(billsLabel.getText()) + billsAmount));
                }
                if (otherAmount > 0) {
                    otherLabel.setText(String.format("%.2f", Double.parseDouble(otherLabel.getText()) + otherAmount));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (resultSett != null) resultSett.close();
                    if (pstmtt != null) pstmtt.close();
                    if (connn != null) connn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });  //fix this

    }
}
