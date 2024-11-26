package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AddMedicinePage {
    // private Stage primaryStage;

    private Stage primaryStage;
    private int userId;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/medicenes";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root123@123";

    // UI elements for the Add Medicine Page
    private TextField medicineNameField = new TextField();
    private ComboBox<String> reminderTypeComboBox = new ComboBox<>();
    private VBox reminderTimeBox = new VBox(10);
    private Spinner<Integer> daysSpinner = new Spinner<>(1, 30, 1);
    private Button addReminderButton = new Button("Add Reminder Time");
    private VBox reminderTimesList = new VBox(10);

    public AddMedicinePage(Stage stage, int userId) {
        this.primaryStage = stage;
        this.userId = userId;
    }

    private void saveMedicineToDatabase(String medName, List<String> reminderTimes, LocalDate startDate, int daysCount) {
        // Calculate and save dates for the medicine schedule
        for (int i = 0; i < daysCount; i++) {
            LocalDate scheduleDate = startDate.plusDays(i);

            for (String time : reminderTimes) {
                // Insert each medicine entry into the database
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                    String query = "INSERT INTO medicine (user_id, date, medName, time) VALUES (?, ?, ?, ?)";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setInt(1, userId);
                    ps.setString(2, scheduleDate.toString());
                    ps.setString(3, medName);
                    ps.setString(4, time);
                    ps.executeUpdate();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void showAddMedicinePage() {
        // Create the layout for adding a new medicine
        VBox layout = new VBox(15);
        layout.setStyle("-fx-background-color: #f2f9f9; -fx-padding: 20px;");
        layout.setAlignment(Pos.TOP_LEFT);

        // Medicine Name Field
        HBox medicineNameBox = new HBox(10);
        medicineNameBox.setAlignment(Pos.CENTER_LEFT);
        Label medicineNameLabel = new Label("Medicine Name: ");
        medicineNameField.setPromptText("Enter medicine name");
        medicineNameBox.getChildren().addAll(medicineNameLabel, medicineNameField);

        // Reminder Type ComboBox
        Label reminderTypeLabel = new Label("Reminder Type: ");
        reminderTypeComboBox.getItems().addAll("X times a day");
        reminderTypeComboBox.setValue("X times a day");

        // Default reminder options
        reminderTypeComboBox.setOnAction(e -> updateReminderFields());

        // Days Spinner
        HBox scheduleBox = new HBox(10);
        Label scheduleLabel = new Label("Schedule for how many days: ");
        scheduleBox.getChildren().addAll(scheduleLabel, daysSpinner);

        // Layout for displaying the reminder times
        reminderTimeBox.setStyle("-fx-padding: 10px;");
        updateReminderFields(); // Update the reminder fields based on the selection

        // Add medicine button
        Button addMedicineButton = new Button("Add Medicine");
        addMedicineButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10px;");
        addMedicineButton.setOnAction(e -> addMedicine());

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 10;");
        // Set action for the back button
        backButton.setOnAction(event -> {
            // Create a new MedicinePage instance
            MedicenePage medicenePage = new MedicenePage(primaryStage, userId);
            // Show the page with the current date
            medicenePage.showPage();
        });

        // Add elements to the layout
        layout.getChildren().addAll(
            medicineNameBox,
            reminderTypeLabel,
            reminderTypeComboBox,
            reminderTimeBox,
            scheduleBox,
            addMedicineButton,
            backButton
        );

        Scene scene = new Scene(layout, 600, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Add Medicine");
        primaryStage.show();
    }

    private void addReminderTime() {
        // Create a dialog for adding reminder time
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Reminder Time");
        dialog.setHeaderText("Enter Reminder Time (e.g., 1:30 or 2:45)");
    
        // Set up dialog buttons
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
    
        // Create layout for the dialog
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
    
        // Time input field for hour:minute format
        TextField timeField = new TextField();
        timeField.setPromptText("Enter time (hour:minute)");
    
        // Add input fields to grid
        grid.add(new Label("Time (hh:mm):"), 0, 0);
        grid.add(timeField, 1, 0);
    
        // Enable/disable add button based on valid input
        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);
    
        // Add input validation for time format
        timeField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean valid = newValue.matches("^([1-9]|1[0-2]):([0-5][0-9])$"); // Validation for "1:30" or "12:45"
            addButton.setDisable(!valid); // Enable Add button only if the format is correct
        });
    
        // Set the dialog content
        dialog.getDialogPane().setContent(grid);
    
        // Convert the result to a time string when the dialog is closed
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return timeField.getText(); // Return the entered time (e.g., "1:30")
            }
            return null;
        });
    
        // Show dialog and process result
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(time -> {
            // Create a label with the new time and add it to the list
            Label timeLabel = new Label(time);
    
            // Add a delete button for each time entry
            Button deleteButton = new Button("X");
            deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            deleteButton.setOnAction(e -> {
                reminderTimesList.getChildren().removeAll(timeLabel, deleteButton);
            });
    
            // Create an HBox to hold the time label and delete button
            HBox timeEntry = new HBox(10, timeLabel, deleteButton);
            reminderTimesList.getChildren().add(timeEntry);
        });
    }    
    

    private void updateReminderFields() {
        // Clear previous reminder fields
        reminderTimeBox.getChildren().clear();
    
        String reminderType = reminderTypeComboBox.getValue();
    
        if (reminderType.equals("X times a day")) {
            // Ask for time slots
            Label timeLabel = new Label("Set Reminder Time(s): ");
            reminderTimeBox.getChildren().add(timeLabel);
    
            // Add an input for setting the time
            addReminderButton.setOnAction(e -> addReminderTime());
            reminderTimeBox.getChildren().add(addReminderButton);
    
        } 
    }
    

    private void addMedicine() {
        // Retrieve values from the form
        String medName = medicineNameField.getText();
        List<String> reminderTimes = new ArrayList<>();
    
        // Extract time labels from the reminderTimesList
        for (javafx.scene.Node node : reminderTimesList.getChildren()) {
            if (node instanceof HBox) {
                HBox timeEntry = (HBox) node;
                Label timeLabel = (Label) timeEntry.getChildren().get(0);
                reminderTimes.add(timeLabel.getText());
            }
        }
    
        // Retrieve the start date from the datepicker in the main page (should be passed from the parent)
        LocalDate startDate = LocalDate.now(); // For now, use today's date
    
        // Retrieve the number of days to schedule
        int daysCount = daysSpinner.getValue();
    
        // Validate inputs
        if (medName.isEmpty()) {
            showAlert("Please enter a medicine name.");
            return;
        }
    
        if (reminderTimes.isEmpty()) {
            showAlert("Please add at least one reminder time.");
            return;
        }
    
        // Save the data to the database
        saveMedicineToDatabase(medName, reminderTimes, startDate, daysCount);
        primaryStage.close(); // Close the add medicine page
    }
    
    // Helper method to show alerts
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void show() {
        showAddMedicinePage();
    }
}