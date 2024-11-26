package com.example;
import javafx.beans.property.*;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;

public class MediceneRecord {
    private final int id;
    private final SimpleStringProperty medicineName;
    private final SimpleBooleanProperty taken;
    private final HBox daysBox;

    public MedicineRecord(int id, String medicineName, boolean taken, String days) {
        this.id = id;
        this.medicineName = new SimpleStringProperty(medicineName);
        this.taken = new SimpleBooleanProperty(taken);
        this.daysBox = createDaysBox(days);
    }

    public int getId() {
        return id;
    }

    public String getMedicineName() {
        return medicineName.get();
    }

    public SimpleStringProperty medicineNameProperty() {
        return medicineName;
    }

    public boolean isTaken() {
        return taken.get();
    }

    public void setTaken(boolean taken) {
        this.taken.set(taken);
    }

    public SimpleBooleanProperty takenProperty() {
        return taken;
    }

    public HBox getDaysBox() {
        return daysBox;
    }

    public ObjectProperty<HBox> daysProperty() {
        return new SimpleObjectProperty<>(daysBox);
    }

    public String getDaysAsString() {
        StringBuilder daysString = new StringBuilder();
        for (CheckBox cb : daysBox.getChildren().filtered(node -> node instanceof CheckBox)) {
            daysString.append(((CheckBox) cb).isSelected() ? "Y" : "N");
        }
        return daysString.toString();
    }

    private HBox createDaysBox(String days) {
        HBox daysBox = new HBox(5);
        daysBox.setAlignment(Pos.CENTER);
        String[] dayLabels = {"S", "M", "T", "W", "T", "F", "S"};

        for (int i = 0; i < 7; i++) {
            CheckBox cb = new CheckBox(dayLabels[i]);
            if (i < days.length() && days.charAt(i) == 'Y') {
                cb.setSelected(true);
            }
            daysBox.getChildren().add(cb);
        }

        return daysBox;
    }
}

