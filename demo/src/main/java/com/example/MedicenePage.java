package com.example;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MedicenePage {
    private Stage primaryStage;

    public MedicenePage(Stage stage) {
        this.primaryStage = stage;
    }

    public void showPage() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);

        Button addMediceneButton = new Button("Add Medicene");
        addMediceneButton.setOnAction(e -> {
            // Add functionality to add Medicene here
            // For now, just a placeholder
            System.out.println("Add Medicene Button clicked!");
        });

        layout.getChildren().addAll(addMediceneButton);

        Scene mediceneScene = new Scene(layout, 400, 300);
        primaryStage.setScene(mediceneScene);
        primaryStage.setTitle("Medicenes");
        primaryStage.show();
    }
}

