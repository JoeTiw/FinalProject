package com.example.classproj;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.cell.PropertyValueFactory;


import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


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


    @FXML
    private TextField expenseTotal;

    @FXML
    private TextField purpose;


    //***************************List View----------------------------------//



    @FXML
    private TableColumn<Transaction, Integer> idColumn;
    @FXML
    private TableView<Transaction> transactionsTable;
    @FXML
    private TableColumn<Transaction, String> dateColumn;
    @FXML
    private TableColumn<Transaction, String> purposeColumn;
    @FXML
    private TableColumn<Transaction, Double> amountColumn;
    @FXML
    private TableColumn<Transaction, String> categoryColumn;
    @FXML
    private TableColumn<Transaction, Integer> transacNum;


    private Connection connection;

//    public void setUserId(int userId) {
//        this.userId = userId;
//        System.out.println("Logged in user ID: " + this.userId);
//        loadTransactionsData();
//    }

    private int counter = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        submitButton.setOnAction(event -> handleCategoryExpenseSubmit());
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

        //-----------------------------------Add Cash End----------------------------------------//


        logout_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.changeScene(event, "hello-view.fxml", "JJK Tracker!", null);
            }
        });
        try {
            String urll = "jdbc:mysql://classproj.c4pj5kawvmlt.us-east-2.rds.amazonaws.com:3307/java_fx?useSSL=true";
            String usrname = "admin";
            String psswd = "Ur05x^$4qL&F";
            connection = DriverManager.getConnection(urll, usrname, psswd);

        } catch (SQLException e) {
            e.printStackTrace();
        }

       // this.userId = userId;




        // Initialize columns
        //transacNum.setCellValueFactory(new PropertyValueFactory<>("transacNum"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        purposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));

        // Load data
        loadTransactionsData(userId);



    }



    public static class Transaction {
        private final Integer transacNum;
        private final Integer userId;
        private final String category;
        private final Double amount;
        private final Date date;
        private final String purpose;

        public Transaction(Integer transacNum, Integer userId, String category, Double amount, Date date, String purpose) {
            this.transacNum = transacNum;
            this.userId = userId;
            this.category = category;
            this.amount = amount;
            this.date = date;
            this.purpose = purpose;
        }



        public Integer getUserId() {
            return userId;
        }

        public String getCategory() {
            return category;
        }

        public Double getAmount() {
            return amount;
        }

        public Date getDate() {
            return date;
        }

        public String getPurpose() {
            return purpose;
        }

    }



    private void loadTransactionsData(int userId) {
        ObservableList<Transaction> transactionsList = FXCollections.observableArrayList();

        String sql = "SELECT * FROM transactions WHERE userId = ? ORDER BY date DESC";


        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            counter = 1;


            while (rs.next()) {
               // int userId = rs.getInt("userId");
                int transcId = rs.getInt("id");

                String category = rs.getString("category");
                double amount = rs.getDouble("amount");
                Date date = rs.getDate("date");
                String purpose = rs.getString("purpose");

                Transaction transaction = new Transaction(counter++, userId, category, amount, date, purpose);
                transactionsList.add(transaction);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        transactionsTable.setItems(transactionsList);
    }













    //***************************Categories Start----------------------------------//

    @FXML
    private void handleSubmitButtonClick() {
        String purposeValue = purpose.getText();
        // Your code to save the data to the database goes here
        handleCategoryExpenseSubmit();




    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }

    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    // updates the expense label after user submits expense
    private void updateExpenseLabel(double expense, Label expenseLabel) {
        double currentExpense = Double.parseDouble(expenseLabel.getText());
        currentExpense += expense;
        expenseLabel.setText(String.format("%.2f", currentExpense));
    }



    private void updateCategoryExpenseInDatabase(int userId, String category, double expense, Date date) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            String urll = "jdbc:mysql://classproj.c4pj5kawvmlt.us-east-2.rds.amazonaws.com:3307/java_fx?useSSL=true";
            String usrname = "admin";
            String psswd = "Ur05x^$4qL&F";
            conn = DriverManager.getConnection(urll, usrname, psswd);

            // Update the category table using if-else structure
            String updateQuery = "";
            if (category.equals("Food")) {
                updateQuery = "UPDATE categories SET food = food + ? WHERE userId = ?";
            } else if (category.equals("Bills")) {
                updateQuery = "UPDATE categories SET bills = bills + ? WHERE userId = ?";
            } else if (category.equals("Shop")) {
                updateQuery = "UPDATE categories SET shopping = shopping + ? WHERE userId = ?";
            } else if (category.equals("Other")) {
                updateQuery = "UPDATE categories SET other = other + ? WHERE userId = ?";
            }else {
                showErrorAlert("Invalid category: " + category);
                return;
            }


            pstmt = conn.prepareStatement(updateQuery);
            pstmt.setDouble(1, expense);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();



            // Close the PreparedStatement to reuse it
            pstmt.close();

            String purposeValue = purpose.getText();

            // Insert the updated information into a new receipt
            String insertQuery = "INSERT INTO transactions (userId, category, purpose, amount, date) VALUES (?, ?,?, ?, ?)";
            pstmt = conn.prepareStatement(insertQuery);
            pstmt.setInt(1, userId);
            pstmt.setString(2, category);
            pstmt.setString(3, purposeValue);
            pstmt.setDouble(4, expense);
            pstmt.setDate(5, date);
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
    }


    private void addNewUserToCategories(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            String urll = "jdbc:mysql://classproj.c4pj5kawvmlt.us-east-2.rds.amazonaws.com:3307/java_fx?useSSL=true";
            String usrname = "admin";
            String psswd = "Ur05x^$4qL&F";
            conn = DriverManager.getConnection(urll, usrname, psswd);

            String insertQuery = "INSERT INTO categories (userId, food, shopping, bills, other) VALUES (?, 0, 0, 0, 0)";
            pstmt = conn.prepareStatement(insertQuery);
            pstmt.setInt(1, userId);
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
    }



    private void handleCategoryExpenseSubmit() {




        Date currentDate = new java.sql.Date(System.currentTimeMillis());
        try {
            double expense = Double.parseDouble(expenseTotal.getText());

            if (foodCheckBox.isSelected()) {
                updateExpenseLabel(expense, foodLabel);
                updateCategoryExpenseInDatabase(userId, "Food", expense, currentDate);
            }
            if (billsCheckBox.isSelected()) {
                updateExpenseLabel(expense, billsLabel);
                updateCategoryExpenseInDatabase(userId, "Bills", expense , currentDate);
            }
            if (shopCheckBox.isSelected()) {
                updateExpenseLabel(expense, shopLabel);
                updateCategoryExpenseInDatabase(userId, "Shop", expense, currentDate);
            }
            if (otherCheckBox.isSelected()) {
                updateExpenseLabel(expense, otherLabel);
                updateCategoryExpenseInDatabase(userId, "Other", expense, currentDate);
            }

            // Subtract the expense from the total cash
            totalCash -= expense;

            // Update the total cash in the database
            Connection conn = null;
            PreparedStatement pstmt = null;
            try {
                String url = "jdbc:mysql://classproj.c4pj5kawvmlt.us-east-2.rds.amazonaws.com:3307/java_fx?useSSL=true";
                String username = "admin";
                String password = "Ur05x^$4qL&F";
                conn = DriverManager.getConnection(url, username, password);

                String updateQuery = "UPDATE users SET total = ? WHERE user_id = ?";
                pstmt = conn.prepareStatement(updateQuery);
                pstmt.setDouble(1, totalCash);
                pstmt.setInt(2, userId);
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

            // Update the total cash in the UI
            totalLabel.setText(String.format("%.2f", totalCash));

            expenseTotal.clear();

            showInfoAlert("Expense submitted successfully!");

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a valid number.");
            alert.showAndWait();
        }
    }

    //---------------Show the total after user logs in-----------------//

//    private void updateCategoryTotals() {
//        Connection conn = null;
//        PreparedStatement pstmt = null;
//        ResultSet resultSet = null;
//
//        try {
//            String urll = "jdbc:mysql://classproj.c4pj5kawvmlt.us-east-2.rds.amazonaws.com:3307/java_fx?useSSL=true";
//            String usrname = "admin";
//            String psswd = "Ur05x^$4qL&F";
//            conn = DriverManager.getConnection(urll, usrname, psswd);
//
//            pstmt = conn.prepareStatement("SELECT category, SUM(amount) as total FROM expenses WHERE user_id = ? GROUP BY category");
//            pstmt.setInt(1, userId);
//            resultSet = pstmt.executeQuery();
//
//            while (resultSet.next()) {
//                String category = resultSet.getString("category");
//                double total = resultSet.getDouble("total");
//
//                switch (category) {
//                    case "Food":
//                        foodLabel.setText(Double.toString(total));
//                        break;
//                    case "Bills":
//                        billsLabel.setText(Double.toString(total));
//                        break;
//                    case "Shop":
//                        shopLabel.setText(Double.toString(total));
//                        break;
//                    case "Other":
//                        otherLabel.setText(Double.toString(total));
//                        break;
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (resultSet != null) resultSet.close();
//                if (pstmt != null) pstmt.close();
//                if (conn != null) conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }


    //***************************Categories End----------------------------------//

    //***************************Welcome Label Start----------------------------------//

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

            pstmt = conn.prepareStatement("SELECT user_id, total FROM users WHERE username = ?");
            pstmt.setString(1, loggedInUsername);
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                userId = resultSet.getInt("user_id"); // Get the user ID
                totalCash = resultSet.getDouble("total"); // Get the total cash for the user

                // Check if the user exists in the categories table
                pstmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM categories WHERE userId = ?");
                pstmt.setInt(1, userId);
                resultSet = pstmt.executeQuery();
                resultSet.next();
                int count = resultSet.getInt("count");

                // If the user does not exist in the categories table, add them
                if (count == 0) {
                    addNewUserToCategories(userId);
                }

                totalLabel.setText(Double.toString(totalCash)); // Set the total cash for the user in the UI

                // Get the category amounts for the user
                pstmt = conn.prepareStatement("SELECT food, shopping, bills, other FROM categories WHERE userId = ?");
                pstmt.setInt(1, userId); // Set the user ID
                resultSet = pstmt.executeQuery(); // Execute the query

                // Set the category amounts in the UI
                if (resultSet.next()) {
                    double foodAmount = resultSet.getDouble("food");
                    double shoppingAmount = resultSet.getDouble("shopping");
                    double billsAmount = resultSet.getDouble("bills");
                    double otherAmount = resultSet.getDouble("other");

                    foodLabel.setText(Double.toString(foodAmount));
                    shopLabel.setText(Double.toString(shoppingAmount));
                    billsLabel.setText(Double.toString(billsAmount));
                    otherLabel.setText(Double.toString(otherAmount));
                }
            }
            loadTransactionsData(userId);

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
    }


}
