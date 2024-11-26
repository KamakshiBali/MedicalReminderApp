package com.example;
import com.example.AddMedicinePage;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MedicenePage {
    private Stage primaryStage;
    private int userId; // Logged-in user's ID
    private VBox mediceneContainer;
    private VBox userInfoContainer;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/medicenes"; // Your DB URL
    private static final String DB_USERNAME = "root"; // Your DB username
    private static final String DB_PASSWORD = "root123@123"; // Your DB password

    public MedicenePage(Stage stage, int userId) {
        this.primaryStage = stage;
        this.userId = userId;
        this.mediceneContainer = new VBox(10); // Container to hold medicine boxes
        mediceneContainer.setAlignment(Pos.TOP_LEFT);
        this.userInfoContainer = new VBox(10); // Container to hold user info
        userInfoContainer.setAlignment(Pos.TOP_LEFT);
    }

    // Method to fetch user information (username, DOB)
    private String getUserDOB() {
        String dob = "";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String query = "SELECT dob FROM user WHERE iduser = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                dob = rs.getString("dob");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return dob;
    }

    // Method to get username
    private String getUsername() {
        String username = "";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String query = "SELECT username FROM user WHERE iduser = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                username = rs.getString("username");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return username;
    }

    // Method to calculate age from DOB
    private int calculateAge(String dob) {
        LocalDate birthDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int age = LocalDate.now().getYear() - birthDate.getYear();
        if (LocalDate.now().getDayOfYear() < birthDate.getDayOfYear()) {
            age--;
        }
        return age;
    }

    // Method to fetch medicines for the selected date
    private List<String> fetchMedicinesForDate(LocalDate selectedDate) {
        List<String> medicines = new ArrayList<>();
        String formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String query = "SELECT medName, time FROM medicine WHERE user_id = ? AND date = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setString(2, formattedDate);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String medName = rs.getString("medName");
                String time = rs.getString("time");
                if (medName != null && !medName.isEmpty()) {
                    medicines.add(medName + " at " + time);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return medicines;
    }

    // Method to display medicines in a container
    private void displayMedicines(LocalDate selectedDate) {
        mediceneContainer.getChildren().clear(); // Clear previous medicines
        List<String> medicines = fetchMedicinesForDate(selectedDate);

        if (medicines.isEmpty()) {
            mediceneContainer.getChildren().add(new Label("No medicines for the selected date."));
        } else {
            for (String medicine : medicines) {
                VBox medicineBox = new VBox(5);
                medicineBox.setStyle("-fx-border: 1px solid #ccc; -fx-padding: 10px; -fx-background-color: #f9f9f9; -fx-border-radius: 5px;");
                Label medicineLabel = new Label(medicine);
                medicineLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
                medicineBox.getChildren().add(medicineLabel);
                mediceneContainer.getChildren().add(medicineBox);
            }
        }
    }

    public void showPage() {
        // Fetch user info
        String username = getUsername();
        String dob = getUserDOB();
        int age = calculateAge(dob); // Calculate age

        // Create labels for the user information
        Label welcomeLabel = new Label("Welcome " + username);
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2E8B57;");
        
        Label dobLabel = new Label("Your DOB: " + dob + " | Age: " + age + " years");
        dobLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        // Add the user info labels to the user info container
        userInfoContainer.getChildren().addAll(welcomeLabel, dobLabel);

        // Create a CalendarPicker for selecting a date
        CalendarPicker calendarPicker = new CalendarPicker();

        // Fetch and display medicines when the date is selected
        calendarPicker.getDatePicker().setOnAction(event -> {
            LocalDate selectedDate = calendarPicker.getSelectedDate();
            displayMedicines(selectedDate); // Display the medicines for the selected date
        });

        // Create a main layout to hold both the user info and the medicine container
        VBox mainLayout = new VBox(20);
        mainLayout.setStyle("-fx-background-color: #f2f9f9; -fx-padding: 20px;");

        // Add Medicene Button
        Button addMediceneButton = new Button("Add Medicine");
        addMediceneButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 8;");
    
        // Set the action for the Add Medicine button
        addMediceneButton.setOnAction(event -> {
            // Open the AddMedicinePage when the button is clicked
            AddMedicinePage addMedicinePage = new AddMedicinePage(primaryStage, userId);
            addMedicinePage.show();  // Show the Add Medicine page
        });
        
        // Add the user info, calendar, and medicine container to the main layout
        mainLayout.getChildren().addAll(userInfoContainer, calendarPicker.createCalendarContainer(), mediceneContainer, addMediceneButton);

        // Create and set the scene
        Scene mediceneScene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(mediceneScene);
        primaryStage.setTitle("Medicenes");
        primaryStage.show();
    }
}
