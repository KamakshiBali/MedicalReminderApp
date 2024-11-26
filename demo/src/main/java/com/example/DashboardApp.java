package com.example;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class DashboardApp extends Application {
    // Database credentials
    static final String DB_URL = "jdbc:mysql://localhost:3306/medicenes"; // Replace with your database URL
    static final String DB_USERNAME = "root"; // Replace with your MySQL username
    static final String DB_PASSWORD = "root123@123"; // Replace with your MySQL password

    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Medical Reminder App");

        // Show Welcome Scene
        showWelcomeScene();
    }

    private void showWelcomeScene() {
        VBox layout = new VBox(30);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: #f2f9f9;");

        Label welcomeLabel = new Label("Welcome to Medical Reminder App");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2E8B57;");

        Button signupButton = new Button("Signup");
        signupButton.setPrefWidth(150);
        signupButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 10;");
        signupButton.setOnAction(e -> showSignupScene());

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(150);
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 10;");
        loginButton.setOnAction(e -> showLoginScene());

        layout.getChildren().addAll(welcomeLabel, signupButton, loginButton);

        Scene welcomeScene = new Scene(layout, 500, 400);
        primaryStage.setScene(welcomeScene);
        primaryStage.show();
    }

    private void showSignupScene() {
        GridPane grid = createFormGridPane();
        grid.setStyle("-fx-background-color: #f9f9f9;");

        // Form Fields
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField dobField = new TextField();
        TextField emailField = new TextField();
        TextField mobileField = new TextField();
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        genderComboBox.setPromptText("Select Gender");

        // Add fields to grid
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("DOB (YYYY-MM-DD):"), 0, 2);
        grid.add(dobField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Mobile No:"), 0, 4);
        grid.add(mobileField, 1, 4);
        grid.add(new Label("Gender:"), 0, 5);
        grid.add(genderComboBox, 1, 5);

        Button signupButton = new Button("Signup");
        signupButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 10;");
        signupButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String dob = dobField.getText();
            String email = emailField.getText();
            String mobile = mobileField.getText();
            String gender = genderComboBox.getValue();

            if (username.isEmpty() || password.isEmpty() || dob.isEmpty() || email.isEmpty() || mobile.isEmpty() || gender == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
                return;
            }

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                // Insert user into database
                String query = "INSERT INTO user (username, password, dob, email, mobile, gender) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS); // Retrieve the generated user ID
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setDate(3, Date.valueOf(dob));
                ps.setString(4, email);
                ps.setString(5, mobile);
                ps.setString(6, gender);

                ps.executeUpdate();

                // Get the generated user ID
                ResultSet generatedKeys = ps.getGeneratedKeys();
                int userId = -1;
                if (generatedKeys.next()) {
                    userId = generatedKeys.getInt(1); // Retrieve the auto-generated ID
                }

                if (userId != -1) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Signup successful!");
                    showMedicenePage(userId); // Redirect to Medicene page with the new user's ID
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not retrieve user ID.");
                }
            } catch (SQLIntegrityConstraintViolationException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Username already exists.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Database error occurred.");
            }
        });

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 10;");
        backButton.setOnAction(e -> showWelcomeScene());

        HBox buttonBox = new HBox(20, signupButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        grid.add(buttonBox, 0, 6, 2, 1);

        Scene signupScene = new Scene(grid, 500, 400);
        primaryStage.setScene(signupScene);
    }

    private void showLoginScene() {
        GridPane grid = createFormGridPane();
        grid.setStyle("-fx-background-color: #f9f9f9;");

        // Form Fields
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();

        // Add fields to grid
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 10;");
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter username and password.");
                return;
            }

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                String query = "SELECT * FROM user WHERE username = ? AND password = ?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, username);
                ps.setString(2, password);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int userId = rs.getInt("iduser"); // Assuming "id" is the user ID column in the database
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Login successful!");
                    showMedicenePage(userId); // Pass userId to MedicenePage
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "User not found or incorrect password.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Database error occurred.");
            }
        });

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 10;");
        backButton.setOnAction(e -> showWelcomeScene());

        HBox buttonBox = new HBox(20, loginButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        grid.add(buttonBox, 0, 2, 2, 1);

        Scene loginScene = new Scene(grid, 500, 400);
        primaryStage.setScene(loginScene);
    }

    private void showMedicenePage(int userId) {
        MedicenePage medicenePage = new MedicenePage(primaryStage, userId);
        medicenePage.showPage(); // Switch to MedicenePage
    }

    private GridPane createFormGridPane() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        return grid;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
