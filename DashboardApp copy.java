package com.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DashboardApp extends Application {
    static final String DB_URL = "jdbc:mysql://localhost:3306/medicenes"; // Replace with your database URL
    static final String DB_USERNAME = "root"; // Replace with your MySQL username
    static final String DB_PASSWORD = "root"; // Replace with your MySQL password

    private Stage primaryStage;
    private Label medicalFactLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Medical Reminder App");

        showWelcomeScene();
    }

    private void showWelcomeScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f0b36e;");

        // Left Pane
        AnchorPane leftPane = new AnchorPane();
        leftPane.setPrefWidth(300);
        leftPane.setStyle("-fx-background-color: #ffffff;");
        ImageView imageView = new ImageView(new Image("file:bgcentre.png"));
        imageView.setFitWidth(490);
        imageView.setPreserveRatio(true);
        imageView.setLayoutX(-190);
        leftPane.getChildren().add(imageView);

        root.setLeft(leftPane);

        // Center Pane
        VBox centerPane = new VBox(20);
        centerPane.setPadding(new Insets(50, 0, 0, 0));
        centerPane.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Welcome to the MedBuddy");
        welcomeLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        welcomeLabel.setStyle("-fx-text-fill: #2C3E50;");

        Label taglineLabel = new Label("Your everyday health companion app!");
        taglineLabel.setFont(Font.font("Georgia", FontPosture.ITALIC, 16));
        taglineLabel.setStyle("-fx-text-fill: #34495E;");

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(200);
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        loginButton.setOnAction(e -> showLoginScene());

        Button signupButton = new Button("Signup");
        signupButton.setPrefWidth(200);
        signupButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        signupButton.setOnAction(e -> showSignupScene());

        medicalFactLabel = new Label(getRandomMedicalFact());
        medicalFactLabel.setFont(Font.font("Arial", FontPosture.ITALIC, 14));
        medicalFactLabel.setStyle("-fx-text-fill: #2C3E50; -fx-padding: 10px;");

        centerPane.getChildren().addAll(welcomeLabel, taglineLabel, loginButton, signupButton, medicalFactLabel);

        root.setCenter(centerPane);

        Scene welcomeScene = new Scene(root, 800, 500);
        primaryStage.setScene(welcomeScene);
        primaryStage.show();
    }

    private void showSignupScene() {
        showFormScene("Signup", "Signup", false);
    }

    private void showLoginScene() {
        showFormScene("Login", "Login", true);
    }

    private void showFormScene(String title, String buttonText, boolean isLogin) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f0b36e;");

        VBox centerPane = new VBox(15);
        centerPane.setPadding(new Insets(30, 0, 0, 0));
        centerPane.setAlignment(Pos.CENTER);

        Label formLabel = new Label(title);
        formLabel.setFont(Font.font("System Bold", 24));

        // Username and password fields
        Label usernameLabel = new Label("Username:");
        usernameLabel.setFont(Font.font("Arial", 14));
        TextField usernameField = new TextField();
        usernameField.setPrefWidth(250);
        usernameField.setStyle("-fx-font-size: 14px;");

        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(Font.font("Arial", 14));
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(250);
        passwordField.setStyle("-fx-font-size: 14px;");

        // Additional fields for Signup
        Label emailLabel = new Label("Email:");
        emailLabel.setFont(Font.font("Arial", 14));
        TextField emailField = new TextField();
        emailField.setPrefWidth(250);
        emailField.setStyle("-fx-font-size: 14px;");

        Label dobLabel = new Label("Date of Birth:");
        dobLabel.setFont(Font.font("Arial", 14));
        DatePicker dobPicker = new DatePicker();
        dobPicker.setPrefWidth(250);
        
        Label mobileLabel = new Label("Mobile:");
        mobileLabel.setFont(Font.font("Arial", 14));
        TextField mobileField = new TextField();
        mobileField.setPrefWidth(250);
        mobileField.setStyle("-fx-font-size: 14px;");
        
        Label genderLabel = new Label("Gender:");
        genderLabel.setFont(Font.font("Arial", 14));
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        genderComboBox.setPrefWidth(250);

        // Action button
        Button actionButton = new Button(buttonText);
        actionButton.setPrefWidth(250);
        actionButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        
        actionButton.setOnAction(e -> {
            if (isLogin) {
                String username = usernameField.getText();
                // For demo purposes, using the username as userId. Normally, you'd fetch this from the DB
                int userId = 1;  // Assuming user ID after successful login
                showMedicenePage(userId);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Signup successful!");
                showWelcomeScene();
            }
        });

        Button backButton = new Button("Back");
        backButton.setPrefWidth(250);
        backButton.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white;");
        backButton.setOnAction(e -> showWelcomeScene());

        // Add the fields to the layout
        centerPane.getChildren().addAll(formLabel, usernameLabel, usernameField, passwordLabel, passwordField, emailLabel, emailField);
        
        // Only add the additional fields for signup
        if (title.equals("Signup")) {
            centerPane.getChildren().addAll(dobLabel, dobPicker, mobileLabel, mobileField, genderLabel, genderComboBox);
        }
        
        centerPane.getChildren().addAll(actionButton, backButton);

        root.setCenter(centerPane);

        Scene formScene = new Scene(root, 700, 500);
        primaryStage.setScene(formScene);
    }

    private void showMedicenePage(int userId) {
        MedicenePage medicenePage = new MedicenePage(primaryStage, userId);
        medicenePage.showPage();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String getRandomMedicalFact() {
        List<String> medicalFacts = Arrays.asList(
                "Did you know? The average human body has 37.2 trillion cells.",
                "Did you know? Your body has more bacteria than human cells.",
                "Did you know? Your brain generates about 23 watts of power while awake.",
                "Did you know? The human heart beats around 100,000 times a day.",
                "Did you know? Humans have the ability to distinguish over 1 trillion smells."
        );

        Random random = new Random();
        return medicalFacts.get(random.nextInt(medicalFacts.size()));
    }
}
