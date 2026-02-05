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
    private void handleLogout() throws IOException {
        App.setRoot("login");
    }
}
