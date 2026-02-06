package com.alv.mastertools.controllers;

import java.io.IOException;

import com.alv.mastertools.App;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class PrimaryController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        // Cargar la vista de inicio por defecto
        loadView("home");
    }

    @FXML
    private void showHome() {
        loadView("home");
    }

    @FXML
    private void showTracker() {
        loadView("tracker_config");
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
            if (root.getStyleClass().contains("light-theme")) {
                root.getStyleClass().remove("light-theme");
            } else {
                root.getStyleClass().add("light-theme");
            }
        }
    }

    @FXML
    private void handleLogout() throws IOException {
        App.setRoot("login");
    }
}
