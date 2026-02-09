package com.alv.mastertools.controllers;

import java.io.IOException;

import com.alv.mastertools.App;
import com.alv.mastertools.services.TrackerService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PrimaryController {

    @FXML
    private StackPane contentArea;

    @FXML
    private ComboBox<String> themeSelector;

    // Sidebar Components
    @FXML
    private VBox sidebar;
    @FXML
    private Button btnToggle;
    @FXML
    private Label lblMenu;
    @FXML
    private Label lblSystem;

    @FXML
    private Button btnHome;
    @FXML
    private Button btnTracker;
    @FXML
    private Button btnConfig;
    @FXML
    private Button btnLogout;

    private boolean isSidebarCollapsed = false;

    @FXML
    public void initialize() {
        // Inicializar ComboBox
        ObservableList<String> options = FXCollections.observableArrayList(
                "Dark", "Light", "Kids", "Matrix", "Synthwave", "Dracula", "Coffee");
        themeSelector.setItems(options);

        // Cargar vista inicio
        loadView("home");

        // Aplicar tema guardado y sincronizar combo
        Platform.runLater(this::initTheme);

        // Listener para cambios
        themeSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                changeTheme(newVal.toLowerCase());
            }
        });
    }

    private void initTheme() {
        String savedTheme = TrackerService.get().getSettings().getTheme();
        String comboValue = "Dark";
        if ("light".equals(savedTheme)) {
            comboValue = "Light";
        } else if ("kids".equals(savedTheme)) {
            comboValue = "Kids";
        } else if ("matrix".equals(savedTheme)) {
            comboValue = "Matrix";
        } else if ("synthwave".equals(savedTheme)) {
            comboValue = "Synthwave";
        } else if ("dracula".equals(savedTheme)) {
            comboValue = "Dracula";
        } else if ("coffee".equals(savedTheme)) {
            comboValue = "Coffee";
        }
        themeSelector.setValue(comboValue);
    }

    private void changeTheme(String newTheme) {
        if (contentArea.getScene() != null) {
            Parent root = contentArea.getScene().getRoot();

            root.getStyleClass().removeAll(
                    "light-theme", "kids-theme", "matrix-theme",
                    "synthwave-theme", "dracula-theme", "coffee-theme");

            if ("light".equals(newTheme)) {
                root.getStyleClass().add("light-theme");
            } else if ("kids".equals(newTheme)) {
                root.getStyleClass().add("kids-theme");
            } else if ("matrix".equals(newTheme)) {
                root.getStyleClass().add("matrix-theme");
            } else if ("synthwave".equals(newTheme)) {
                root.getStyleClass().add("synthwave-theme");
            } else if ("dracula".equals(newTheme)) {
                root.getStyleClass().add("dracula-theme");
            } else if ("coffee".equals(newTheme)) {
                root.getStyleClass().add("coffee-theme");
            }

            TrackerService.get().getSettings().setTheme(newTheme);
            TrackerService.get().saveConfiguration();
        }
    }

    @FXML
    private void handleToggleSidebar() {
        isSidebarCollapsed = !isSidebarCollapsed;

        if (isSidebarCollapsed) {
            // Colapsar
            sidebar.setPrefWidth(60);
            lblMenu.setVisible(false);
            lblMenu.setManaged(false);
            lblSystem.setVisible(false);
            lblSystem.setManaged(false);

            setButtonsContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        } else {
            // Expandir
            sidebar.setPrefWidth(250);
            lblMenu.setVisible(true);
            lblMenu.setManaged(true);
            lblSystem.setVisible(true);
            lblSystem.setManaged(true);

            setButtonsContentDisplay(ContentDisplay.LEFT);
        }
    }

    private void setButtonsContentDisplay(ContentDisplay display) {
        if (btnHome != null)
            btnHome.setContentDisplay(display);
        if (btnTracker != null)
            btnTracker.setContentDisplay(display);
        if (btnConfig != null)
            btnConfig.setContentDisplay(display);
        if (btnLogout != null)
            btnLogout.setContentDisplay(display);
    }

    @FXML
    public void showHome() {
        loadView("home");
    }

    @FXML
    public void showTracker() {
        loadView("tracker_config");
    }

    @FXML
    public void showConfig() {
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
    private void handleLogout() throws IOException {
        App.setRoot("login");
    }
}
