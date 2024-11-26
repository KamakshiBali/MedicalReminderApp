package com.example;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EditMedicinePage {
    private Stage primaryStage;
    private int userId; // Logged-in user's ID
    private String medName;
    private LocalDate date;
    private String time;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/medicenes";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root123@123";

    public EditMedicinePage(Stage stage, int userId, String medName, LocalDate date, String time) {
        this.primaryStage = stage;
        this.userId = userId;
        this.medName = medName;
        this.date = date;
        this.time = time;
    }

    public void show() {
        // Create UI components for editing
        Label titleLabel = new Label("Edit Medicine");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label medNameLabel = new Label("Medicine Name:");
        TextField medNameField = new TextField(medName);

        Label dateLabel = new Label("Date:");
        DatePicker datePicker = new DatePicker(date);
        datePicker.setStyle("-fx-font-size: 14px;");

        Label timeLabel = new Label("Time:");
        TextField timeField = new TextField(time);

        // Save button to update the database
        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 8;");

        saveButton.setOnAction(event -> {
            // Update the medicine details in the database
            updateMedicineInDB(medNameField.getText(), datePicker.getValue(), timeField.getText());
        });

        VBox layout = new VBox(15);
        layout.setStyle("-fx-background-color: #f2f9f9; -fx-padding: 20px;");
        layout.setAlignment(Pos.TOP_CENTER);
        layout.getChildren().addAll(titleLabel, medNameLabel, medNameField, dateLabel, datePicker, timeLabel, timeField, saveButton);

        // Set the scene
        Scene editScene = new Scene(layout, 400, 300);
        primaryStage.setScene(editScene);
        primaryStage.setTitle("Edit Medicine");
        primaryStage.show();
    }

    private void updateMedicineInDB(String newMedName, LocalDate newDate, String newTime) {
        String formattedDate = newDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String query = "UPDATE medicine SET medName = ?, date = ?, time = ? WHERE user_id = ? AND medName = ? AND date = ? AND time = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, newMedName);
            ps.setString(2, formattedDate);
            ps.setString(3, newTime);
            ps.setInt(4, userId);
            ps.setString(5, medName);  // Old medicine name
            ps.setString(6, this.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))); // Old date
            ps.setString(7, time); // Old time

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Medicine details updated successfully!");
                successAlert.showAndWait();
                primaryStage.close(); // Close the edit page
                // Refresh the medicine page with updated data
                MedicenePage medicenePage = new MedicenePage(primaryStage, userId);
                medicenePage.showPage();
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Failed to update medicine details.");
                errorAlert.showAndWait();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Database error: " + ex.getMessage());
            errorAlert.showAndWait();
        }
    }
}
