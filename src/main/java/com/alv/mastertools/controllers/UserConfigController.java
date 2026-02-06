package com.alv.mastertools.controllers;

import com.alv.mastertools.models.TrackerSettings;
import com.alv.mastertools.services.TrackerService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;

public class UserConfigController {

    @FXML
    private TextField currentUserField;
    @FXML
    private TextField newUserField;
    @FXML
    private PasswordField currentPassField;
    @FXML
    private PasswordField newPassField;
    @FXML
    private PasswordField confirmPassField;
    @FXML
    private Label statusLabel;

    // Campos gestión de datos
    @FXML
    private Label storagePathLabel;
    @FXML
    private Label dataStatusLabel;

    private TrackerService service;
    private final String DATA_DIR = System.getProperty("user.home") + File.separator + ".alv-master";
    private final String LOGS_FILE = "activity_logs.csv";

    @FXML
    public void initialize() {
        service = TrackerService.get();
        // Cargar usuario actual
        currentUserField.setText(service.getSettings().getUsername());
        statusLabel.setText("");

        // Mostrar ruta
        storagePathLabel.setText(DATA_DIR);
        dataStatusLabel.setText("");
    }

    @FXML
    private void handleSave() {
        String currentPassInput = currentPassField.getText();
        String storedPass = service.getSettings().getPassword();

        // 1. Validar contraseña actual
        if (!storedPass.equals(currentPassInput)) {
            statusLabel.setStyle("-fx-text-fill: -color-danger;");
            statusLabel.setText("Contraseña actual incorrecta.");
            return;
        }

        String newUser = newUserField.getText().trim();
        String newPass = newPassField.getText();
        String confirmPass = confirmPassField.getText();

        boolean isChanged = false;
        TrackerSettings settings = service.getSettings();

        // 2. Cambiar Usuario si se escribió algo
        if (!newUser.isEmpty()) {
            if (!newUser.equals(settings.getUsername())) {
                settings.setUsername(newUser);
                currentUserField.setText(newUser);
                isChanged = true;
            }
        }

        // 3. Cambiar Contraseña si se escribió algo
        if (!newPass.isEmpty()) {
            if (!newPass.equals(confirmPass)) {
                statusLabel.setStyle("-fx-text-fill: -color-danger;");
                statusLabel.setText("Las nuevas contraseñas no coinciden.");
                return;
            }
            if (newPass.length() < 4) {
                statusLabel.setStyle("-fx-text-fill: -color-danger;");
                statusLabel.setText("La contraseña es muy corta.");
                return;
            }
            settings.setPassword(newPass);
            isChanged = true;
        }

        if (isChanged) {
            service.saveConfiguration();
            statusLabel.setStyle("-fx-text-fill: -color-success;");
            statusLabel.setText("Credenciales actualizadas correctamente.");

            // Limpiar campos sensibles
            currentPassField.clear();
            newPassField.clear();
            confirmPassField.clear();
            newUserField.clear();
        } else {
            statusLabel.setStyle("-fx-text-fill: -color-text-muted;");
            statusLabel.setText("No se realizaron cambios.");
        }
    }

    @FXML
    private void handleExportData() {
        Path source = Paths.get(DATA_DIR, LOGS_FILE);
        if (!Files.exists(source)) {
            dataStatusLabel.setStyle("-fx-text-fill: -color-text-muted;");
            dataStatusLabel.setText("No hay datos para exportar.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Historial de Actividad");
        fileChooser.setInitialFileName("historial_actividad.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        // Obtener la ventana actual con seguridad
        Stage stage = (Stage) storagePathLabel.getScene().getWindow();
        File dest = fileChooser.showSaveDialog(stage);

        if (dest != null) {
            try {
                Files.copy(source, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                dataStatusLabel.setStyle("-fx-text-fill: -color-success;");
                dataStatusLabel.setText("Datos exportados exitosamente.");
            } catch (IOException e) {
                e.printStackTrace();
                dataStatusLabel.setStyle("-fx-text-fill: -color-danger;");
                dataStatusLabel.setText("Error al exportar: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClearData() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Borrar Historial");
        alert.setHeaderText("¿Estás seguro?");
        alert.setContentText("Esta acción eliminará TODO el historial de actividades y no se puede deshacer.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            Path file = Paths.get(DATA_DIR, LOGS_FILE);
            try {
                if (Files.deleteIfExists(file)) {
                    // Limpiar también en memoria del servicio
                    service.getLogs().clear();

                    dataStatusLabel.setStyle("-fx-text-fill: -color-success;");
                    dataStatusLabel.setText("Historial eliminado correctamente.");
                } else {
                    dataStatusLabel.setText("No había datos que borrar.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                dataStatusLabel.setStyle("-fx-text-fill: -color-danger;");
                dataStatusLabel.setText("Error al borrar: " + e.getMessage());
            }
        }
    }
}
