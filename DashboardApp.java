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
        setBackgroundStyle(root);

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

        Label medicalFactLabel = new Label(getRandomMedicalFact());
        medicalFactLabel.setFont(Font.font("Arial", FontPosture.ITALIC, 14));
        medicalFactLabel.setStyle("-fx-text-fill: #2C3E50; -fx-padding: 10px;");

        centerPane.getChildren().addAll(welcomeLabel, taglineLabel, loginButton, signupButton, medicalFactLabel);

        root.setCenter(centerPane);

        Scene welcomeScene = new Scene(root, 800, 500);
        primaryStage.setScene(welcomeScene);
        primaryStage.show();
    }

    private void showSignupScene() {
        showFormScene("Signup");
    }

    private void showLoginScene() {
        showFormScene("Login");
    }

    private void showFormScene(String formType) {
        BorderPane root = new BorderPane();
        setBackgroundStyle(root);

        GridPane grid = createFormGridPane();

        TextField usernameField = createTextField();
        PasswordField passwordField = createPasswordField();
        TextField dobField = null;
        TextField emailField = null;
        TextField mobileField = null;
        ComboBox<String> genderComboBox = null;

        if (formType.equals("Signup")) {
            dobField = createTextField();
            emailField = createTextField();
            mobileField = createTextField();
            genderComboBox = new ComboBox<>();
            genderComboBox.getItems().addAll("Male", "Female", "Other");
            genderComboBox.setPromptText("Select Gender");
        }

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);

        if (formType.equals("Signup")) {
            grid.add(new Label("DOB (YYYY-MM-DD):"), 0, 2);
            grid.add(dobField, 1, 2);
            grid.add(new Label("Email:"), 0, 3);
            grid.add(emailField, 1, 3);
            grid.add(new Label("Mobile No:"), 0, 4);
            grid.add(mobileField, 1, 4);
            grid.add(new Label("Gender:"), 0, 5);
            grid.add(genderComboBox, 1, 5);
        }

        Button actionButton = new Button(formType);
        actionButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 10;");
        actionButton.setOnAction(e -> handleFormSubmission(formType, usernameField.getText()));

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 10;");
        backButton.setOnAction(e -> showWelcomeScene());

        VBox layout = new VBox(15, grid, actionButton, backButton, createFactLabel());
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        root.setCenter(layout);

        Scene formScene = new Scene(root, 700, 500);
        primaryStage.setScene(formScene);
    }

    private void setBackgroundStyle(Pane root) {
        root.setStyle("-fx-background-color: #f0b36e;");
    }

    private GridPane createFormGridPane() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        return grid;
    }

    private TextField createTextField() {
        TextField textField = new TextField();
        textField.setPrefWidth(250);
        return textField;
    }

    private PasswordField createPasswordField() {
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(250);
        return passwordField;
    }

    private Label createFactLabel() {
        Label factLabel = new Label(getRandomMedicalFact());
        factLabel.setFont(Font.font("Arial", FontPosture.ITALIC, 14));
        factLabel.setStyle("-fx-text-fill: #2C3E50;");
        return factLabel;
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

    private void handleFormSubmission(String formType, String username) {
        if (formType.equals("Signup")) {
            showAlert(Alert.AlertType.INFORMATION, "Signup Successful", "Welcome, " + username + "!");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome back, " + username + "!");
        }
        showWelcomeScene();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
