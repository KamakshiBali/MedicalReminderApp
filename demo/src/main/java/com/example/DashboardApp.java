package com.example;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardApp extends Application {
    ImageView background = createBackgroundImage(getClass().getResource("medicare.jpg").toExternalForm());

    private ImageView createBackgroundImage(String imagePath) {
        ImageView background = new ImageView(new Image(imagePath));
        background.setFitWidth(600);
        background.setFitHeight(800);
        return background;
    }

    // Database credentials
    static final String DB_URL = "jdbc:mysql://localhost:3306/medicenes"; 
    static final String DB_USERNAME = "root";
    static final String DB_PASSWORD = "root123@123"; 

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
        layout.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8);");

        Label welcomeLabel = new Label("Welcome to Medi-Care!");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2E8B57;");

        Button signupButton = new Button("Signup");
        signupButton.setPrefWidth(150);
        signupButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 10;");
        signupButton.setOnAction(e -> showSignupScene());

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(150);
        loginButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 10;");
        loginButton.setOnAction(e -> showLoginScene());

        layout.getChildren().addAll(welcomeLabel, signupButton, loginButton);

        StackPane root = new StackPane(background, layout);
        Scene welcomeScene = new Scene(root, 600, 800);
        primaryStage.setScene(welcomeScene);
        primaryStage.show();
    }

    private void showSignupScene() {
        GridPane grid = createFormGridPane();
        grid.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8);");

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
        signupButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 10;");
        signupButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String hashedPassword = PasswordHasher.hashPassword(password); 
            String dob = dobField.getText();
            String email = emailField.getText();
            String mobile = mobileField.getText();
            String gender = genderComboBox.getValue();

            if (username.isEmpty() || password.isEmpty() || dob.isEmpty() || email.isEmpty() || mobile.isEmpty() || gender == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
                return;
            }

            // no weak password alloweddd
            if (password.length() < 8 || 
            !password.matches(".*\\d.*") ||    
            !password.matches(".*[!@#$%^&*()].*")){
            showAlert(Alert.AlertType.ERROR, "Weak Password!", 
                "Password should be at least 8 characters long \n Include at least one number and one special character.");
                return;
            }

            if (mobile.length()<10){
                showAlert(Alert.AlertType.ERROR, "Wrong Number!", 
                "Please enter valid mobile number");
                return;
            }

            // email validation regex
            if (!email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                showAlert(Alert.AlertType.ERROR, "Invalid Email!", 
                    "Email should be in the format: meow@gmail.com.");
                    return;
            }


            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                String query = "INSERT INTO user (username, password, dob, email, mobile, gender) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS); // Retrieve the generated user ID
                ps.setString(1, username);
                ps.setString(2, hashedPassword);
                ps.setDate(3, Date.valueOf(dob));
                ps.setString(4, email);
                ps.setString(5, mobile);
                ps.setString(6, gender);

                ps.executeUpdate();

                // get the generated user ID
                ResultSet generatedKeys = ps.getGeneratedKeys();
                int userId = -1;
                if (generatedKeys.next()) {
                    userId = generatedKeys.getInt(1); 
                }

                if (userId != -1) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Signup successful!");
                    showMedicenePage(userId); // this line redirects to Medicene page with the new user's ID
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

        StackPane root = new StackPane(background, grid);
        Scene signupScene = new Scene(root, 600, 800);
        primaryStage.setScene(signupScene);
    }

    private void showLoginScene() {
        GridPane grid = createFormGridPane();
        grid.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8);");

        // Form Fields
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();

        // Add fields to grid
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 10;");
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter username and password.");
                return;
            }

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                String query = "SELECT * FROM user WHERE username = ?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, username);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int userId = rs.getInt("iduser"); // Assuming "id" is the user ID column in the database
                    String storedHashedPassword = rs.getString("password");
                    if (PasswordHasher.verifyPassword(password, storedHashedPassword)) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Login successful!");
                        showMedicenePage(userId); // Pass userId to MedicenePage
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Incorrect password.");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "User not found.");
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

        StackPane root = new StackPane(background, grid);
        Scene loginScene = new Scene(root, 600, 800);
        primaryStage.setScene(loginScene);
    }

    private void showMedicenePage(int userId) {
        MedicenePage medicenePage = new MedicenePage(primaryStage, userId);
        medicenePage.showPage();
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