package com.example;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class ChangeDatePage {
    private Stage primaryStage;
    private int userId;  // Logged-in user's ID
    private LocalDate currentDate;  // Current date selected in the MedicenePage

    public ChangeDatePage(Stage stage, int userId, LocalDate currentDate) {
        this.primaryStage = stage;
        this.userId = userId;
        this.currentDate = currentDate;
    }

    public void showPage() {
        // Create a date picker to allow the user to select a new date
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(currentDate);  // Set the default value to the current date
        datePicker.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");

        // Create a button to confirm the date change
        Button confirmButton = new Button("Confirm Date");
        confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 8;");

        // Create a button to go back to the Medicine Page
        Button backButton = new Button("Back to Medicines");
        backButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 8;");

        // Set action for the confirm button
        confirmButton.setOnAction(event -> {
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate != null) {
                // Create a new MedicinePage instance
                MedicenePage medicenePage = new MedicenePage(primaryStage, userId);
                // Call the method to show the page with the selected date
                medicenePage.showPage(selectedDate);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a valid date.", ButtonType.OK);
                alert.showAndWait();
            }
        });

        // Set action for the back button
        backButton.setOnAction(event -> {
            // Create a new MedicinePage instance
            MedicenePage medicenePage = new MedicenePage(primaryStage, userId);
            // Show the page with the current date
            medicenePage.showPage();
        });

        // Create a layout for the page
        VBox layout = new VBox(20);
        layout.setStyle("-fx-background-color: #f2f9f9; -fx-padding: 20px;");
        layout.setAlignment(Pos.CENTER);
        
        // Add components to the layout
        layout.getChildren().addAll(
            new Label("Select a Date to View Medicines:"), 
            datePicker, 
            confirmButton,
            backButton
        );

        // Create the scene and set the stage
        Scene changeDateScene = new Scene(layout, 400, 350);
        primaryStage.setScene(changeDateScene);
        primaryStage.setTitle("Change Date");
        primaryStage.show();
    }
}