package com.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MedicenePage {
    private Stage primaryStage;
    private int userId; // Logged-in user's ID
    private TableView<Medicene> mediceneTable = new TableView<>();

    private static final String DB_URL = "jdbc:mysql://localhost:3306/medicenes"; // Your DB URL
    private static final String DB_USERNAME = "root"; // Your DB username
    private static final String DB_PASSWORD = "root123@123"; // Your DB password

    public MedicenePage(Stage stage, int userId) {
        this.primaryStage = stage;
        this.userId = userId;
    }

    public void showPage() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f2f9f9; -fx-padding: 20px;");

        // Medicene Table
        mediceneTable.setItems(fetchMedicenes());
        setupTableColumns();

        // Add Medicene Button
        Button addMediceneButton = new Button("Add Medicene");
        addMediceneButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 8;");
        addMediceneButton.setOnAction(e -> showAddMediceneForm());

        layout.getChildren().addAll(mediceneTable, addMediceneButton);

        Scene mediceneScene = new Scene(layout, 800, 600);
        primaryStage.setScene(mediceneScene);
        primaryStage.setTitle("Medicenes");
        primaryStage.show();
    }

    private void setupTableColumns() {
        TableColumn<Medicene, String> nameColumn = new TableColumn<>("Medicene Name");
        nameColumn.setCellValueFactory(data -> data.getValue().mediceneNameProperty());
        nameColumn.setMinWidth(200);
        nameColumn.setMaxWidth(300);
        nameColumn.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
        mediceneTable.getColumns().add(nameColumn);

        TableColumn<Medicene, String> datesColumn = new TableColumn<>("Date");
        datesColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Medicene medicene = (Medicene) getTableRow().getItem();
                    VBox checkboxes = createCheckboxesForDates(medicene.getStartDate(), medicene.getEndDate());
                    setGraphic(checkboxes);
                }
            }
        });
        datesColumn.setMinWidth(300);
        datesColumn.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
        mediceneTable.getColumns().add(datesColumn);
    }

    private VBox createCheckboxesForDates(LocalDate startDate, LocalDate endDate) {
        VBox vbox = new VBox(5);
        vbox.setStyle("-fx-padding: 10px;");
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            dateList.add(current);
            current = current.plusDays(1);
        }
        for (LocalDate date : dateList) {
            CheckBox checkBox = new CheckBox(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            checkBox.setStyle("-fx-font-size: 12px;");
            vbox.getChildren().add(checkBox);
        }
        return vbox;
    }

    private void showAddMediceneForm() {
        Stage formStage = new Stage();
        formStage.initModality(Modality.APPLICATION_MODAL);
        formStage.setTitle("Add Medicene");
    
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 20px;");
    
        // Medicene Name
        TextField nameField = new TextField();
        nameField.setPromptText("Medicene Name");
        nameField.setStyle("-fx-padding: 10px; -fx-font-size: 14px;");
    
        // Recurrence Section
        Spinner<Integer> repeatEverySpinner = new Spinner<>(1, 100, 1);
        repeatEverySpinner.setStyle("-fx-font-size: 14px;");
        ComboBox<String> repeatTypeCombo = new ComboBox<>();
        repeatTypeCombo.getItems().addAll("Day", "Week");
        repeatTypeCombo.setValue("Day");
        repeatTypeCombo.setStyle("-fx-font-size: 14px;");
    
        // Repeats On Days (only enabled if Week is selected)
        HBox repeatDaysBox = new HBox(10);
        repeatDaysBox.setAlignment(Pos.CENTER);
        String[] days = {"S", "M", "T", "W", "T", "F", "S"};
        ToggleButton[] dayButtons = new ToggleButton[7];
        for (int i = 0; i < 7; i++) {
            ToggleButton dayButton = new ToggleButton(days[i]);
            dayButton.setStyle("-fx-font-size: 14px; -fx-shape: 'circle';");
            dayButton.setDisable(true);
            dayButtons[i] = dayButton;
            repeatDaysBox.getChildren().add(dayButton);
        }
        repeatTypeCombo.setOnAction(e -> {
            boolean isWeekly = "Week".equals(repeatTypeCombo.getValue());
            for (ToggleButton dayButton : dayButtons) {
                dayButton.setDisable(!isWeekly);
            }
        });
    
        // Daily Repeats
        Spinner<Integer> dailyRepeatSpinner = new Spinner<>(1, 10, 1);
        dailyRepeatSpinner.setStyle("-fx-font-size: 14px;");
        VBox timeInputBox = new VBox(5);
        timeInputBox.setAlignment(Pos.CENTER_LEFT);
        timeInputBox.setStyle("-fx-padding: 10px;");
        dailyRepeatSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            timeInputBox.getChildren().clear();
            for (int i = 0; i < newVal; i++) {
                TextField timeField = new TextField();
                timeField.setPromptText("Time " + (i + 1) + " (e.g., 08:00)");
                timeField.setStyle("-fx-padding: 5px; -fx-font-size: 14px;");
                timeInputBox.getChildren().add(timeField);
            }
        });
    
        // End Options
        ToggleGroup endGroup = new ToggleGroup();
        RadioButton neverEndButton = new RadioButton("Never");
        neverEndButton.setToggleGroup(endGroup);
        neverEndButton.setSelected(true);
        RadioButton endOnButton = new RadioButton("On");
        endOnButton.setToggleGroup(endGroup);
        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setDisable(true);
        endDatePicker.setStyle("-fx-font-size: 14px;");
        RadioButton endAfterButton = new RadioButton("After");
        endAfterButton.setToggleGroup(endGroup);
        Spinner<Integer> endAfterSpinner = new Spinner<>(1, 100, 1);
        endAfterSpinner.setDisable(true);
        endAfterSpinner.setStyle("-fx-font-size: 14px;");
    
        endGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            endDatePicker.setDisable(newToggle != endOnButton);
            endAfterSpinner.setDisable(newToggle != endAfterButton);
        });
    
        // Save and Cancel Buttons
        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 8;");
        saveButton.setOnAction(e -> {
            String mediceneName = nameField.getText();
            int repeatEvery = repeatEverySpinner.getValue();
            String repeatType = repeatTypeCombo.getValue();
            List<String> selectedDays = new ArrayList<>();
            for (ToggleButton dayButton : dayButtons) {
                if (dayButton.isSelected()) {
                    selectedDays.add(dayButton.getText());
                }
            }
            int dailyRepeats = dailyRepeatSpinner.getValue();
            List<String> reminderTimes = new ArrayList<>();
            for (javafx.scene.Node node : timeInputBox.getChildren()) {
                if (node instanceof TextField) {
                    TextField timeField = (TextField) node;
                    reminderTimes.add(timeField.getText());
                }
            }
            String endType = ((RadioButton) endGroup.getSelectedToggle()).getText();
            LocalDate endDate = endDatePicker.getValue();
            int endOccurrences = endAfterSpinner.getValue();
    
            if (mediceneName.isEmpty() || (endType.equals("On") && endDate == null)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill all required fields.");
            } else {
                // Save the data to the database here
                System.out.println("Saved Medicene:");
                System.out.println("Name: " + mediceneName);
                System.out.println("Repeats Every: " + repeatEvery + " " + repeatType);
                System.out.println("On Days: " + selectedDays);
                System.out.println("Daily Reminders: " + dailyRepeats + " times: " + reminderTimes);
                System.out.println("Ends: " + endType + (endType.equals("On") ? " " + endDate : "") + (endType.equals("After") ? " " + endOccurrences + " occurrences" : ""));
                formStage.close();
            }
        });
    
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 8;");
        cancelButton.setOnAction(e -> formStage.close());
    
        HBox buttonBox = new HBox(15, saveButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
    
        layout.getChildren().addAll(
                new Label("Medicene Name:"), nameField,
                new Label("Repeats Every:"), new HBox(10, repeatEverySpinner, repeatTypeCombo),
                new Label("Repeats On:"), repeatDaysBox,
                new Label("Daily Repeats:"), dailyRepeatSpinner, timeInputBox,
                new Label("Ends:"), new VBox(10, neverEndButton, new HBox(10, endOnButton, endDatePicker), new HBox(10, endAfterButton, endAfterSpinner)),
                buttonBox
        );
    
        Scene formScene = new Scene(layout, 500, 700);
        formStage.setScene(formScene);
        formStage.show();
    }
    

    private void saveMediceneToDatabase(String name, LocalDate startDate, LocalDate endDate) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String query = "INSERT INTO medicene (user_id, medicene_name, start_date, end_date) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setString(2, name);
            ps.setDate(3, Date.valueOf(startDate));
            ps.setDate(4, Date.valueOf(endDate));
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save medicene.");
        }
    }

    private ObservableList<Medicene> fetchMedicenes() {
        ObservableList<Medicene> medicenes = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String query = "SELECT medicene_name, start_date, end_date FROM medicene WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("medicene_name");
                Date startSqlDate = rs.getDate("start_date");
                Date endSqlDate = rs.getDate("end_date");
                
                // Handle potential NULL values
                LocalDate startDate = (startSqlDate != null) ? startSqlDate.toLocalDate() : null;
                LocalDate endDate = (endSqlDate != null) ? endSqlDate.toLocalDate() : null;
    
                if (startDate != null && endDate != null) {
                    medicenes.add(new Medicene(name, startDate, endDate));
                } else {
                    System.err.println("Skipping row with NULL start_date or end_date for medicene: " + name);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch medicene data.");
        }
        return medicenes;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
