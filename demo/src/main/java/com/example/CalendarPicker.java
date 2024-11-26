package com.example;

import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CalendarPicker {
    private LocalDate selectedDate;
    private DatePicker datePicker;

    public CalendarPicker() {
        // Initialize with the current date
        selectedDate = LocalDate.now();
        datePicker = new DatePicker(selectedDate);
    }

    public VBox createCalendarContainer() {
        VBox calendarContainer = new VBox(5);
        calendarContainer.setStyle("-fx-padding: 10px; -fx-alignment: top-left;");

        // Display selected date as a label
        Text selectedDateLabel = new Text("Date: " + selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        selectedDateLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Add event listener to update the label when a date is selected
        datePicker.setOnAction(event -> {
            selectedDate = datePicker.getValue();
            selectedDateLabel.setText("Date: " + selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        });

        calendarContainer.getChildren().addAll(selectedDateLabel, datePicker);
        return calendarContainer;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    // Add this method to expose the DatePicker
    public DatePicker getDatePicker() {
        return datePicker;
    }
}