package com.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
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

        // Medicene Table
        mediceneTable.setItems(fetchMedicenes());
        setupTableColumns();

        // Add Medicene Button
        Button addMediceneButton = new Button("Add Medicene");
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
        mediceneTable.getColumns().add(datesColumn);
    }

    private VBox createCheckboxesForDates(LocalDate startDate, LocalDate endDate) {
        VBox vbox = new VBox();
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            dateList.add(current);
            current = current.plusDays(1);
        }
        for (LocalDate date : dateList) {
            CheckBox checkBox = new CheckBox(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            vbox.getChildren().add(checkBox);
        }
        return vbox;
    }

    private void showAddMediceneForm() {
        Stage formStage = new Stage();
        formStage.initModality(Modality.APPLICATION_MODAL);
        formStage.setTitle("Add Medicene");

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);

        TextField nameField = new TextField();
        nameField.setPromptText("Medicene Name");

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            String mediceneName = nameField.getText();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (mediceneName.isEmpty() || startDate == null || endDate == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "All fields are required.");
            } else {
                saveMediceneToDatabase(mediceneName, startDate, endDate);
                mediceneTable.setItems(fetchMedicenes()); // Refresh the table
                formStage.close();
            }
        });

        layout.getChildren().addAll(
                new Label("Medicene Name:"), nameField,
                new Label("Start Date:"), startDatePicker,
                new Label("End Date:"), endDatePicker,
                saveButton
        );

        Scene formScene = new Scene(layout, 400, 300);
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
