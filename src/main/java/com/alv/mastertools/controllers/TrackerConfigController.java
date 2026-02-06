package com.alv.mastertools.controllers;

import com.alv.mastertools.models.ActivitySummary;
import com.alv.mastertools.models.TaskLog;
import com.alv.mastertools.models.TrackerSettings;
import com.alv.mastertools.services.TrackerService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;
import java.util.stream.Collectors;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import java.util.List;

public class TrackerConfigController {

    @FXML
    private TextField startTimeField;
    @FXML
    private TextField endTimeField;
    @FXML
    private TextField intervalField;
    @FXML
    private CheckBox autoStartCheckbox;
    @FXML
    private Button toggleButton;
    @FXML
    private Label statusLabel;

    // Filtros de fecha
    @FXML
    private DatePicker filterStartDate;
    @FXML
    private DatePicker filterEndDate;

    // Tabla de Resumen
    @FXML
    private TableView<ActivitySummary> activitiesTable;
    @FXML
    private TableColumn<ActivitySummary, String> startCol;
    @FXML
    private TableColumn<ActivitySummary, String> endCol;
    @FXML
    private TableColumn<ActivitySummary, String> durationCol;
    @FXML
    private TableColumn<ActivitySummary, String> descCol;

    private TrackerService service;

    @FXML
    public void initialize() {
        service = TrackerService.get();
        TrackerSettings settings = service.getSettings();

        // Cargar valores actuales
        startTimeField.setText(settings.getStartTime().toString());
        endTimeField.setText(settings.getEndTime().toString());
        intervalField.setText(String.valueOf(settings.getIntervalMinutes()));
        if (autoStartCheckbox != null) {
            autoStartCheckbox.setSelected(settings.isAutoStartOnLogin());
        }

        // Inicializar fechas de filtro (Ayer y Hoy por defecto)
        if (filterStartDate != null && filterEndDate != null) {
            filterStartDate.setValue(LocalDate.now().minusDays(1));
            filterEndDate.setValue(LocalDate.now());
        }

        updateStatusUI();

        // Configurar Columnas
        startCol.setCellValueFactory(new PropertyValueFactory<>("formattedStartTime"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("formattedEndTime"));
        durationCol.setCellValueFactory(new PropertyValueFactory<>("formattedDuration"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Ajuste de columnas: Fijas para tiempos, dinámica para descripción
        startCol.setMinWidth(70);
        startCol.setMaxWidth(70);
        endCol.setMinWidth(70);
        endCol.setMaxWidth(70);
        durationCol.setMinWidth(100);
        durationCol.setMaxWidth(100);

        // La columna de descripción toma el resto del espacio (width total - anchos
        // fijos - buffer scrollbar)
        descCol.prefWidthProperty().bind(activitiesTable.widthProperty().subtract(240 + 20));

        // IMPORTANTE: Quitamos CONSTRAINED_RESIZE_POLICY para permitir anchos
        // manuales/bindeados
        activitiesTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Registrar callback para refresco automático (aseguramos hilo UI)
        service.setOnLogAdded(() -> javafx.application.Platform.runLater(this::refreshTable));

        refreshTable();
    }

    @FXML
    private void handleToggle() {
        if (service.getSettings().isActive()) {
            service.stopTracking();
        } else {
            if (saveSettingsFromUI()) {
                service.startTracking();
            }
        }
        updateStatusUI();
    }

    @FXML
    private void handleManualEntry() {
        service.openManualEntryPopup();
    }

    @FXML
    private void handleSaveConfig() {
        if (saveSettingsFromUI()) {
            statusLabel.setText("Configuración guardada.");
            if (service.getSettings().isActive()) {
                // Si ya está activo, reiniciamos para aplicar cambios de intervalo/hora
                service.startTracking();
            }
        }
    }

    private boolean saveSettingsFromUI() {
        try {
            LocalTime start = LocalTime.parse(startTimeField.getText());
            LocalTime end = LocalTime.parse(endTimeField.getText());
            int interval = Integer.parseInt(intervalField.getText());

            TrackerSettings s = service.getSettings();
            s.setStartTime(start);
            s.setEndTime(end);
            s.setIntervalMinutes(interval);
            if (autoStartCheckbox != null) {
                s.setAutoStartOnLogin(autoStartCheckbox.isSelected());
            }

            service.saveConfiguration(); // Persistir cambios

            return true;
        } catch (DateTimeParseException | NumberFormatException e) {
            statusLabel.setText("Error en formato. Usa HH:MM y números enteros.");
            return false;
        }
    }

    private void updateStatusUI() {
        boolean active = service.getSettings().isActive();

        // Limpiamos estilos anteriores para evitar conflictos
        toggleButton.getStyleClass().removeAll("button-success", "button-danger");

        if (active) {
            toggleButton.setText("DETENER SEGUIMIENTO");
            toggleButton.getStyleClass().add("button-danger");
            statusLabel.setText("Servicio ACTIVO. Próxima alerta en unos minutos...");
        } else {
            toggleButton.setText("INICIAR SEGUIMIENTO");
            toggleButton.getStyleClass().add("button-success");
            statusLabel.setText("Servicio DETENIDO.");
        }
    }

    @FXML
    private void refreshTable() {
        List<TaskLog> allLogs = service.getLogs();

        LocalDate start = (filterStartDate != null) ? filterStartDate.getValue() : LocalDate.MIN;
        LocalDate end = (filterEndDate != null) ? filterEndDate.getValue() : LocalDate.MAX;

        List<TaskLog> filteredLogs = allLogs.stream()
                .filter(log -> {
                    LocalDate logDate = log.getTimestamp().toLocalDate();
                    // !logDate.isBefore(start) equivale a logDate >= start
                    // !logDate.isAfter(end) equivale a logDate <= end
                    return !logDate.isBefore(start) && !logDate.isAfter(end);
                })
                .collect(Collectors.toList());

        ObservableList<ActivitySummary> summaries = calculateSummaries(filteredLogs);
        activitiesTable.setItems(summaries);
        activitiesTable.refresh();
    }

    private ObservableList<ActivitySummary> calculateSummaries(List<TaskLog> logs) {
        ObservableList<ActivitySummary> list = FXCollections.observableArrayList();
        if (logs == null || logs.isEmpty())
            return list;

        ActivitySummary currentSummary = null;

        for (TaskLog log : logs) {
            if (currentSummary == null) {
                // Primer elemento
                currentSummary = new ActivitySummary(log.getDescription(), log.getTimestamp(), log.getTimestamp());
            } else {
                if (log.getDescription().equals(currentSummary.getDescription())) {
                    // Misma actividad, extendemos el fin
                    currentSummary.setEndTime(log.getTimestamp());
                } else {
                    // Cambió la actividad, cerramos el anterior y guardamos
                    list.add(currentSummary);
                    currentSummary = new ActivitySummary(log.getDescription(), log.getTimestamp(), log.getTimestamp());
                }
            }
        }

        // Agregar el último
        if (currentSummary != null) {
            list.add(currentSummary);
        }

        // Orden inverso (lo más reciente arriba) si se desea, ahora está cronológico
        // FXCollections.reverse(list);

        return list;
    }
}
