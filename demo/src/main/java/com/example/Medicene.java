package com.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class Medicene {
    private final StringProperty mediceneName;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public Medicene(String mediceneName, LocalDate startDate, LocalDate endDate) {
        this.mediceneName = new SimpleStringProperty(mediceneName);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getMediceneName() {
        return mediceneName.get();
    }

    public StringProperty mediceneNameProperty() {
        return mediceneName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}