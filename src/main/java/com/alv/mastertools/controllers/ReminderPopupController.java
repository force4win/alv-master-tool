package com.alv.mastertools.controllers;

import com.alv.mastertools.services.TrackerService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class ReminderPopupController {

    @FXML
    private TextArea taskDescriptionArea;

    @FXML
    private Label timeLabel;

    @FXML
    public void initialize() {
        // Pre-llenar con la última tarea
        String lastTask = TrackerService.get().getLastTaskDescription();
        if (lastTask != null && !lastTask.isEmpty()) {
            taskDescriptionArea.setText(lastTask);
            taskDescriptionArea.selectAll(); // Seleccionar todo para facilitar sobreescritura
        }

        // Enfocar el área de texto inmediatamente
        taskDescriptionArea.requestFocus();

        // Manejar atajos de teclado
        taskDescriptionArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                event.consume();
                handleSavePrevious();
            } else if (event.getCode() == KeyCode.F12) {
                event.consume();
                handleSave();
            }
            // Enter ahora funciona nativamente como salto de línea
        });
    }

    @FXML
    private void handleSave() {
        String description = taskDescriptionArea.getText().trim();
        if (!description.isEmpty()) {
            TrackerService.get().logTask(description);
        }
        closeWindow();
    }

    private void handleSavePrevious() {
        // Guardar lo que se venía haciendo (última tarea conocida)
        String lastTask = TrackerService.get().getLastTaskDescription();
        if (lastTask != null && !lastTask.isEmpty()) {
            TrackerService.get().logTask(lastTask);
        } else {
            // Si no hay tarea previa, intentamos guardar lo actual o cerramos
            String current = taskDescriptionArea.getText().trim();
            if (!current.isEmpty())
                TrackerService.get().logTask(current);
        }
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) taskDescriptionArea.getScene().getWindow();
        stage.close();
    }
}
