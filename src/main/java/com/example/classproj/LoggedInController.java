package com.example.classproj;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
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
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.time.LocalDate;

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

    @FXML
    private Button helpButton;

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

    @FXML
    private PieChart pieChart;
    private Connection connection;

//    public void setUserId(int userId) {
//        this.userId = userId;
//        System.out.println("Logged in user ID: " + this.userId);
//        loadTransactionsData();
//    }

    private int counter = 1;

    @FXML
    private void generateReport() {
        final double[] total = {0.0};
        Stage popupStage = new Stage();
        popupStage.setTitle("Generate Report");

        Label startDateLabel = new Label("Start Date:");
        DatePicker startDatePicker = new DatePicker();
        Label endDateLabel = new Label("End Date:");
        DatePicker endDatePicker = new DatePicker();

        Label categoryLabel = new Label("Category:");
        ObservableList<String> categoryOptions = FXCollections.observableArrayList("food", "shopping", "bills", "other", "all");
        ComboBox<String> categoryComboBox = new ComboBox<>(categoryOptions);
        categoryComboBox.setValue("Select a category");

        Label fileTypeLabel = new Label("File Type:");
        ObservableList<String> fileTypeOptions = FXCollections.observableArrayList("csv", "txt", "pdf");
        ComboBox<String> fileTypeComboBox = new ComboBox<>(fileTypeOptions);
        fileTypeComboBox.setValue("Select a file type");

        Button submitBtn = new Button("Generate Report");
        submitBtn.setOnAction(submitEvent -> {
            try {
                // Retrieve the selected start and end dates, category, and file type
                LocalDate startDate = startDatePicker.getValue();
                LocalDate endDate = endDatePicker.getValue();
                String category = categoryComboBox.getValue();
                String fileType = fileTypeComboBox.getValue();

                // Query the transactions table for transactions that match the user's selections
                ResultSet rs;
                String sql;
                if (category.equals("all")) {
                    sql = "SELECT t.category, t.purpose, t.amount, t.date " +
                            "FROM transactions t " +
                            "WHERE t.userId = ? AND t.date BETWEEN ? AND ?";
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setInt(1, userId);
                    pstmt.setDate(2, Date.valueOf(startDate));
                    pstmt.setDate(3, Date.valueOf(endDate));
                    rs = pstmt.executeQuery();
                } else {
                    sql = "SELECT t.category, t.purpose, t.amount, t.date " +
                            "FROM transactions t " +
                            "WHERE t.userId = ? AND t.category = ? AND t.date BETWEEN ? AND ?";
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setInt(1, userId);
                    pstmt.setString(2, category);
                    pstmt.setDate(3, Date.valueOf(startDate));
                    pstmt.setDate(4, Date.valueOf(endDate));
                    rs = pstmt.executeQuery();
                }

                // Generate report file
                String filename = "report." + fileType;
                BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

                // I want to generate a timestamp for the filename but it breaks the program
                /*DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                String timestamp = LocalDateTime.now().format(formatter);
                String filename = "report_" + timestamp + "." + fileType;*/

                if (fileType.equals("csv")) {
                    writer.write(String.format("%s's report for category \"%s\" from %s to %s\n", loggedInUsername, category, startDate, endDate));
                    writer.write("Category,Purpose,Amount,Date\n");
                    while (rs.next()) {
                        category = rs.getString("category");
                        String purpose = rs.getString("purpose");
                        double amount = rs.getDouble("amount");
                        LocalDate date = rs.getDate("date") != null ? rs.getDate("date").toLocalDate() : null;
                        if (date != null) {
                            writer.write(String.format("%s,%s,%.2f,%s\n", category, purpose, amount, date));
                        } else {
                            writer.write(String.format("%s,%s,%.2f,%s\n", category, purpose, amount, "N/A"));
                        }
                        total[0] = total[0] + amount;
                    }
                    writer.write(String.format("Total: ,,,,,%.2f\n", total[0]));
                } else if (fileType.equals("txt")) {
                    writer.write(String.format("%s's report for category \"%s\" from %s to %s\n\n", loggedInUsername, category, startDate, endDate));
                    while (rs.next()) {
                        category = rs.getString("category");
                        String purpose = rs.getString("purpose");
                        double amount = rs.getDouble("amount");
                        total[0] += amount;
                        Date date = rs.getDate("date");
                        writer.write(String.format("Category: %s\nPurpose: %s\nAmount: %.2f\nDate: %s\n\n", category, purpose, amount, date));
                    }
                    writer.write(String.format("Total: %.2f\n", total[0]));
                } else if (fileType.equals("pdf")) {
                    Document document = new Document();
                    PdfWriter.getInstance(document, new FileOutputStream(filename));
                    document.open();
                    Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
                    PdfPTable table = new PdfPTable(4);

                    // Add title row
                    PdfPCell titleCell = new PdfPCell(new Phrase(loggedInUsername + "'s report for category \"" + category +
                            "\" from " + startDate + " to " + endDate, boldFont));
                    titleCell.setColspan(4);
                    titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(titleCell);

                    PdfPCell cell1 = new PdfPCell(new Phrase("Category", boldFont));
                    PdfPCell cell2 = new PdfPCell(new Phrase("Purpose", boldFont));
                    PdfPCell cell3 = new PdfPCell(new Phrase("Amount", boldFont));
                    PdfPCell cell4 = new PdfPCell(new Phrase("Date", boldFont));
                    table.addCell(cell1);
                    table.addCell(cell2);
                    table.addCell(cell3);
                    table.addCell(cell4);
                    while (rs.next()) {
                        category = rs.getString("category");
                        String purpose = rs.getString("purpose");
                        double amount = rs.getDouble("amount");
                        Date date = rs.getDate("date");
                        table.addCell(category);
                        table.addCell(purpose);
                        table.addCell(Double.toString(amount));
                        table.addCell(date.toString());
                        total[0] += amount;
                    }

                    PdfPCell totalValueCell = new PdfPCell(new Phrase(""));
                    totalValueCell.setColspan(3);
                    table.addCell(totalValueCell);
                    PdfPCell totalCell = new PdfPCell(new Phrase("Total: " + Double.toString(total[0]), boldFont));
                    totalCell.setColspan(1);
                    table.addCell(totalCell);

                    //table.addCell("");

                    document.add(table);
                    document.close();
                }

                writer.close();
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setHeaderText(null);
                successAlert.setContentText("Report successfully generated!");
                successAlert.showAndWait();

                popupStage.close();
            } catch (SQLException | IOException | DocumentException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Error generating report: " + e.getMessage());
                errorAlert.showAndWait();
            }
        });

        VBox popupLayout = new VBox();
        popupLayout.setSpacing(10);
        popupLayout.setPadding(new Insets(20, 20, 20, 20));
        popupLayout.getChildren().addAll(startDateLabel, startDatePicker, endDateLabel, endDatePicker, categoryLabel, categoryComboBox, fileTypeLabel, fileTypeComboBox, submitBtn);

        Scene popupScene = new Scene(popupLayout);
        popupStage.setScene(popupScene);
        popupStage.show();
    }

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


        //--------Help Button Start---------- //


        helpButton.setOnAction(actionEvent -> {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Help");
            dialog.setHeaderText("Budget Tracker Help");
            dialog.setContentText("Here is some basic help information for the budget tracker app.\n\n" +
                    "1. Add Cash: Use this button to add cash to your budget.\n" +
                    "2. Dashboard: View the main dashboard with summary information.\n" +
                    "3. Help: Get help and assistance on using the app.\n" +
                    "4. Logout: Log out from the app.\n\n" +
                    "For more detailed help, please refer to the user manual.");

            ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().add(closeButton);

            dialog.showAndWait();
        });



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
        //idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        purposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));

        // Load data
        loadTransactionsData(userId);

//        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
//
//
//        try {
//            Connection conn = DriverManager.getConnection("jdbc:mysql://classproj.c4pj5kawvmlt.us-east-2.rds.amazonaws.com:3307/java_fx?useSSL=true", "admin", "Ur05x^$4qL&F");
//            Statement stmt = conn.createStatement();
//            ResultSet rs = stmt.executeQuery("SELECT category, sum(amount) FROM transactions GROUP BY category");
//
//            while (rs.next()) {
//                String category = rs.getString("category");
//                double amount = rs.getDouble("sum(amount)");
//
//                PieChart.Data data = new PieChart.Data(category, amount);
//                pieChartData.add(data);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        pieChart.setData(pieChartData);

    }

    private void updatePieChart(int userId) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT category, sum(amount) FROM transactions WHERE userId = ? GROUP BY category");
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String category = rs.getString("category");
                double amount = rs.getDouble("sum(amount)");

                PieChart.Data data = new PieChart.Data(category, amount);
                pieChartData.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        pieChart.setData(pieChartData);
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

                updatePieChart(userId);
                loadTransactionsData(userId);

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
            updatePieChart(userId);

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
