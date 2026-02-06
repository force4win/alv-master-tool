package com.alv.mastertools.controllers;

import java.io.IOException;

import com.alv.mastertools.App;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import com.alv.mastertools.services.TrackerService;
import com.alv.mastertools.models.TrackerSettings;
import javafx.application.Platform;

public class PrimaryController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        // Cargar vista inicio
        loadView("home");

        // Aplicar tema guardado (pequeño delay para asegurar que scene está lista o
        // usar listener)
        Platform.runLater(this::applySavedTheme);
    }

    private void applySavedTheme() {
        if (contentArea.getScene() != null) {
            String theme = TrackerService.get().getSettings().getTheme();
            Parent root = contentArea.getScene().getRoot();

            // Limpiar temas anteriores
            root.getStyleClass().removeAll("light-theme", "kids-theme");

            if ("light".equals(theme)) {
                root.getStyleClass().add("light-theme");
            } else if ("kids".equals(theme)) {
                root.getStyleClass().add("kids-theme");
            }
            // "dark" es el default, así que no agregamos clase extra
        }
    }

    @FXML
    private void showHome() {
        loadView("home");
    }

    @FXML
    private void showTracker() {
        loadView("tracker_config");
    }

    @FXML
    private void showConfig() {
        loadView("user_config");
    }

    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("views/" + fxml + ".fxml"));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleToggleTheme() {
        if (contentArea.getScene() != null) {
            Parent root = contentArea.getScene().getRoot();
            TrackerSettings settings = TrackerService.get().getSettings();
            String currentTheme = settings.getTheme(); // "dark", "light", "kids"

            // Limpiar clases
            root.getStyleClass().removeAll("light-theme", "kids-theme");

            String newTheme = "dark";

            // Ciclo: dark -> light -> kids -> dark
            if (currentTheme == null || "dark".equals(currentTheme)) {
                newTheme = "light";
                root.getStyleClass().add("light-theme");
            } else if ("light".equals(currentTheme)) {
                newTheme = "kids";
                root.getStyleClass().add("kids-theme");
            } else {
                newTheme = "dark";
                // No class needed for dark
            }

            settings.setTheme(newTheme);
            TrackerService.get().saveConfiguration();
        }
    }

    @FXML
    private void handleLogout() throws IOException {
        App.setRoot("login");
    }
}
