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
    

    private void displayMedicines(LocalDate selectedDate) {
        // Clear any previous medicines
        mediceneContainer.getChildren().clear();
    
        // Add a title for the selected date
        Label dateLabel = new Label("Medicines for " + selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        dateLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        mediceneContainer.getChildren().add(dateLabel);
    
        // Fetch medicines for the selected date
        List<String> medicines = fetchMedicinesForDate(selectedDate);
    
        if (medicines.isEmpty()) {
            // If no medicines found, show a message
            Label noMedicinesLabel = new Label("No medicines scheduled for this date.");
            noMedicinesLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #888;");
            mediceneContainer.getChildren().add(noMedicinesLabel);
        } else {
            // Display each medicine with Edit and Delete buttons
            for (String medicine : medicines) {
                String[] parts = medicine.split(" at ");
                String medName = parts[0];
                String time = parts[1];
    
                VBox medicineBox = new VBox(5);
                medicineBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1px; -fx-padding: 10px; -fx-background-color: #f9f9f9; -fx-border-radius: 5px;");
    
                Label medicineLabel = new Label(medName + " at " + time);
                medicineLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
    
                // Create the Edit button
                Button editButton = new Button("Edit");
                editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5px 10px; -fx-border-radius: 8;");
                editButton.setOnAction(event -> {
                    // Open the EditMedicinePage with the selected medicine details
                    EditMedicinePage editPage = new EditMedicinePage(primaryStage, userId, medName, selectedDate, time);
                    editPage.show();
                });
    
                // Create the Delete button
                Button deleteButton = new Button("Delete");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5px 10px; -fx-border-radius: 8;");
                deleteButton.setOnAction(event -> {
                    // Show confirmation dialog for deletion
                    Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this medicine?", ButtonType.YES, ButtonType.NO);
                    confirmDelete.setTitle("Confirm Deletion");
                    confirmDelete.setHeaderText(null);
                    confirmDelete.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            // If yes, delete the medicine
                            deleteMedicineFromDB(medName, selectedDate, time);
                        }
                    });
                });
    
                HBox buttonContainer = new HBox(10);
                buttonContainer.setAlignment(Pos.CENTER_RIGHT);
                buttonContainer.getChildren().addAll(editButton, deleteButton);
    
                medicineBox.getChildren().addAll(medicineLabel, buttonContainer);
                mediceneContainer.getChildren().add(medicineBox);
            }
        }
    }

    public void showPageWithNewDate(LocalDate selectedDate) {
    // Clear previous medicines from the display container
    mediceneContainer.getChildren().clear();
    
    // Add the new selected date medicines
    displayMedicines(selectedDate);  // This will show medicines for the selected date
}
    
    private void deleteMedicineFromDB(String medName, LocalDate date, String time) {
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            // Delete the medicine from the database
            String query = "DELETE FROM medicine WHERE user_id = ? AND medName = ? AND date = ? AND time = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);  // Ensure that 'userId' is available in this method
            ps.setString(2, medName);
            ps.setString(3, formattedDate);
            ps.setString(4, time);
    
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Medicine deleted successfully!");
                successAlert.showAndWait();
                // Refresh the page to reflect the changes
                displayMedicines(date); // Refresh the medicines for the selected date
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Failed to delete medicine.");
                errorAlert.showAndWait();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Database error: " + ex.getMessage());
            errorAlert.showAndWait();
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

        // Create a CalendarPicker instance with today's date as default
        CalendarPicker calendarPicker = new CalendarPicker();
        LocalDate initialDate = LocalDate.now(); // Set initial date to today
        calendarPicker.getDatePicker().setValue(initialDate);  // Default to today
        displayMedicines(initialDate); // Display medicines for today by default

        // Fetch and display medicines when the date is selected
        calendarPicker.getDatePicker().setOnAction(event -> {
            LocalDate selectedDate = calendarPicker.getSelectedDate();
            displayMedicines(selectedDate); // Display the medicines for the selected date
        });

        // Create a main layout to hold both the user info and the medicine container
        VBox mainLayout = new VBox(20);
        mainLayout.setStyle("-fx-background-color: #f2f9f9; -fx-padding: 20px;");

        // Add Medicine Button
        Button addMediceneButton = new Button("Add Medicine");
        addMediceneButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 8;");

        // Set the action for the Add Medicine button
        addMediceneButton.setOnAction(event -> {
            // Open the AddMedicinePage when the button is clicked
            AddMedicinePage addMedicinePage = new AddMedicinePage(primaryStage, userId);
            addMedicinePage.show();  // Show the Add Medicine page
        });

        // Add a button to change the date
        Button changeDateButton = new Button("Change Date");
        changeDateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 8;");

        // Set the action for the Change Date button
        changeDateButton.setOnAction(event -> {
            ChangeDatePage changeDatePage = new ChangeDatePage(primaryStage, userId, LocalDate.now()); // Pass the current date
            changeDatePage.showPage();  // Show the Change Date page
        });


        // Add the user info, calendar, and medicine container to the main layout
        mainLayout.getChildren().addAll(userInfoContainer, mediceneContainer, addMediceneButton, changeDateButton);

        // Create and set the scene
        Scene mediceneScene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(mediceneScene);
        primaryStage.setTitle("Medicines");
        primaryStage.show();
    }

    // Add this method to the MedicinePage class
        public void showPage(LocalDate selectedDate) {
            // Fetch user info
            String username = getUsername();
            String dob = getUserDOB();
            int age = calculateAge(dob); // Calculate age

            // Create labels for the user information
            Label welcomeLabel = new Label("Welcome " + username);
            welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2E8B57;");

            Label dobLabel = new Label("Your DOB: " + dob + " | Age: " + age + " years");
            dobLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

            // Clear previous user info
            userInfoContainer.getChildren().clear();

            // Add the user info labels to the user info container
            userInfoContainer.getChildren().addAll(welcomeLabel, dobLabel);

            // Create a CalendarPicker instance with the selected date
            CalendarPicker calendarPicker = new CalendarPicker();
            calendarPicker.getDatePicker().setValue(selectedDate);  // Set to the selected date

            // Display medicines for the selected date
            displayMedicines(selectedDate);

            // Fetch and display medicines when the date is selected
            calendarPicker.getDatePicker().setOnAction(event -> {
                LocalDate newSelectedDate = calendarPicker.getSelectedDate();
                displayMedicines(newSelectedDate);
            });

            // Create a main layout to hold both the user info and the medicine container
            VBox mainLayout = new VBox(20);
            mainLayout.setStyle("-fx-background-color: #f2f9f9; -fx-padding: 20px;");

            // Add Medicine Button
            Button addMediceneButton = new Button("Add Medicine");
            addMediceneButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 8;");

            // Set the action for the Add Medicine button
            addMediceneButton.setOnAction(event -> {
                // Open the AddMedicinePage when the button is clicked
                AddMedicinePage addMedicinePage = new AddMedicinePage(primaryStage, userId);
                addMedicinePage.show();  // Show the Add Medicine page
            });

            // Add a button to change the date
            Button changeDateButton = new Button("Change Date");
            changeDateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 8;");

            // Set the action for the Change Date button
            changeDateButton.setOnAction(event -> {
                ChangeDatePage changeDatePage = new ChangeDatePage(primaryStage, userId, selectedDate);
                changeDatePage.showPage();  // Show the Change Date page
            });

            // Add the user info, calendar, and medicine container to the main layout
            mainLayout.getChildren().addAll(userInfoContainer, mediceneContainer, addMediceneButton, changeDateButton);

            // Create and set the scene
            Scene mediceneScene = new Scene(mainLayout, 800, 600);
            primaryStage.setScene(mediceneScene);
            primaryStage.setTitle("Medicines");
            primaryStage.show();
        }
}
