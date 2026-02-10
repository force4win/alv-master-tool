package com.alv.mastertools.controllers;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.alv.mastertools.App;

public class HierarchicalViewController {

    @FXML
    private ScrollPane mainScroll; // The root scrollpane (injected via fxml now)

    @FXML
    private HBox navigationContainer; // The "Accordion" / Navigation Columns

    @FXML
    private VBox contentArea; // Dedicated Area for Notes and Content

    @FXML
    public void initialize() {
        // Cargar datos de prueba
        Item root = createMockData();

        // Inicializar navegación (Lista de Temas Principales)
        addNavigationPanel(root.children, 0, "Menú Principal");

        // Inicialmente mostrar contenido vacío o bienvenida
        showContent(null);
    }

    private void addNavigationPanel(List<Item> items, int level, String title) {
        // Crear panel de navegación
        VBox panel = new VBox(0);
        panel.getStyleClass().add("hierarchical-panel");
        panel.setMinWidth(250);
        panel.setPrefWidth(250);

        // Guardar estado
        panel.setUserData(new PanelData(items, level, title));

        String colorStyle = getColorForLevel(level);
        panel.setStyle("-fx-background-color: " + colorStyle
                + "; -fx-border-color: rgba(0,0,0,0.15); -fx-border-width: 0 1 0 0;");

        // Header
        VBox headerBox = new VBox(10);
        headerBox.setStyle("-fx-padding: 20 20 15 20; -fx-background-color: rgba(0,0,0,0.1);");
        Label lblTitle = new Label(title);
        lblTitle.setWrapText(true);
        lblTitle.setStyle(
                "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 1, 0, 0, 1);");
        headerBox.getChildren().add(lblTitle);
        panel.getChildren().add(headerBox);

        // Lista
        VBox listContainer = new VBox(8);
        listContainer.setStyle("-fx-padding: 15;");
        for (Item item : items) {
            Button btn = createItemButton(item, level, panel);
            listContainer.getChildren().add(btn);
        }

        Button btnAdd = new Button("+ Nueva sección");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.setPrefHeight(35);
        btnAdd.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: rgba(255,255,255,0.6); -fx-border-color: rgba(255,255,255,0.3); -fx-border-style: dashed; -fx-border-radius: 4; -fx-cursor: hand; -fx-font-size: 11px;");
        btnAdd.setOnAction(e -> handleAddItem(items, listContainer, level, panel));
        btnAdd.setOnMouseEntered(e -> btnAdd.setStyle(btnAdd.getStyle().replace("-fx-background-color: transparent;",
                "-fx-background-color: rgba(255,255,255,0.1);")));
        btnAdd.setOnMouseExited(e -> btnAdd.setStyle(btnAdd.getStyle()
                .replace("-fx-background-color: rgba(255,255,255,0.1);", "-fx-background-color: transparent;")));

        listContainer.getChildren().add(btnAdd);

        ScrollPane scroll = new ScrollPane(listContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        panel.getChildren().add(scroll);

        TranslateTransition tt = new TranslateTransition(Duration.millis(250), panel);
        tt.setFromX(50);
        tt.setToX(0);
        tt.play();

        navigationContainer.getChildren().add(panel);

        // Scroll horizontal automático al final cuando se añade un panel nuevo
        // Esto asegura que el contenido siempre sea visible
        Platform.runLater(() -> {
            if (mainScroll != null) {
                mainScroll.setHvalue(1.0); // Scroll to far right
            }
            /*
             * Opcional: Centrar en el contenido si es posible, pero Hvalue=1 es suficiente
             * para mostrar lo nuevo
             */
        });
    }

    // --- CONTENT AREA LOGIC ---
    private void showContent(Item selectedItem) {
        contentArea.getChildren().clear();

        // Asegurarse de que el contentArea ocupe espacio
        HBox.setHgrow(contentArea, Priority.ALWAYS);

        if (selectedItem == null) {
            contentArea.setStyle("-fx-background-color: -color-bg-base; -fx-padding: 50; -fx-alignment: CENTER;");
            Label lblEmpty = new Label("Seleccione un tema para comenzar.");
            lblEmpty.setStyle("-fx-font-size: 18px; -fx-text-fill: -color-text-secondary;");
            contentArea.getChildren().add(lblEmpty);
            return;
        }

        contentArea.setStyle("-fx-background-color: -color-bg-default; -fx-padding: 30;");

        // Header Content
        Label lblTitle = new Label(selectedItem.title);
        lblTitle.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: -color-text-primary;");
        contentArea.getChildren().add(lblTitle);

        Label lblSubtitle = new Label("NOTAS Y ANOTACIONES");
        lblSubtitle.setStyle(
                "-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: -color-accent; -fx-padding: 5 0 15 0;");
        contentArea.getChildren().add(lblSubtitle);

        // Scroll Container for Notes
        VBox notesContainer = new VBox(15);
        renderNotes(selectedItem, notesContainer);

        ScrollPane notesScroll = new ScrollPane(notesContainer);
        notesScroll.setFitToWidth(true);
        notesScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        notesScroll.getStyleClass().add("edge-to-edge");
        VBox.setVgrow(notesScroll, Priority.ALWAYS);

        contentArea.getChildren().add(notesScroll);

        // Input Area
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);
        inputBox.setStyle(
                "-fx-padding: 20; -fx-background-color: -color-bg-base; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 0);");

        TextArea noteInput = new TextArea();
        noteInput.setPromptText("Escribir nueva nota...");
        noteInput.setPrefRowCount(2);
        noteInput.setWrapText(true);
        HBox.setHgrow(noteInput, Priority.ALWAYS);

        Button btnAddNote = new Button("Guardar Nota");
        btnAddNote.getStyleClass().add("button-success");
        btnAddNote.setPrefHeight(50);
        btnAddNote.setOnAction(e -> {
            String text = noteInput.getText();
            if (text != null && !text.trim().isEmpty()) {
                selectedItem.notes.add(text);
                renderNotes(selectedItem, notesContainer);
                noteInput.clear();
            }
        });

        inputBox.getChildren().addAll(noteInput, btnAddNote);
        contentArea.getChildren().add(inputBox);
    }

    private void renderNotes(Item item, VBox container) {
        container.getChildren().clear();
        if (item.notes.isEmpty()) {
            Label empty = new Label("No hay notas registradas para este tema.");
            empty.setStyle("-fx-text-fill: -color-text-muted; -fx-font-style: italic;");
            container.getChildren().add(empty);
            return;
        }

        for (String note : item.notes) {
            Label lbl = new Label(note);
            lbl.setWrapText(true);
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setStyle(
                    "-fx-background-color: #fff9c4; " +
                            "-fx-text-fill: #3e2723; " +
                            "-fx-padding: 15; " +
                            "-fx-background-radius: 4; " +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 2, 2); " +
                            "-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 14px;");
            container.getChildren().add(lbl);
        }
    }

    private Button createItemButton(Item item, int level, VBox panel) {
        Button btn = new Button(item.title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setMinHeight(45);
        btn.setStyle(
                "-fx-background-color: rgba(0,0,0,0.2); -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 15; -fx-cursor: hand; -fx-background-radius: 4;");
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle().replace("-fx-background-color: rgba(0,0,0,0.2);",
                "-fx-background-color: rgba(255,255,255,0.25);")));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace("-fx-background-color: rgba(255,255,255,0.25);",
                "-fx-background-color: rgba(0,0,0,0.2);")));
        btn.setOnAction(e -> handleItemSelect(item, level, panel));
        return btn;
    }

    private void handleAddItem(List<Item> items, VBox listContainer, int level, VBox panel) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Nuevo");
        dialog.setHeaderText(null);
        dialog.setContentText("Nombre:");
        try {
            dialog.getDialogPane().setStyle("-fx-background-color: #3B4252; -fx-text-fill: white;");
        } catch (Exception e) {
        }
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (name.trim().isEmpty())
                return;
            Item newItem = new Item(name);
            items.add(newItem);
            Button btn = createItemButton(newItem, level, panel);
            int index = listContainer.getChildren().size() - 1;
            if (index < 0)
                index = 0;
            listContainer.getChildren().add(index, btn);
        });
    }

    private void handleItemSelect(Item item, int level, VBox currentPanel) {
        int currentIndex = navigationContainer.getChildren().indexOf(currentPanel);
        if (currentIndex < navigationContainer.getChildren().size() - 1) {
            navigationContainer.getChildren().subList(currentIndex + 1, navigationContainer.getChildren().size())
                    .clear();
        }

        collapsePanel(currentPanel, item.title, level);

        if (item.children == null)
            item.children = new ArrayList<>();
        addNavigationPanel(item.children, level + 1, item.title);
        showContent(item);
    }

    private void collapsePanel(VBox panel, String selectedTitle, int level) {
        panel.getChildren().clear();
        panel.setMinWidth(60);
        panel.setPrefWidth(60);
        panel.setMaxWidth(60);
        panel.setAlignment(Pos.CENTER);
        String colorStyle = getColorForLevel(level);
        panel.setStyle("-fx-padding: 0; -fx-background-color: " + colorStyle
                + "; -fx-border-color: rgba(0,0,0,0.2); -fx-border-width: 0 1 0 0;");
        Label rotLabel = new Label(selectedTitle);
        rotLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.9); -fx-font-weight: bold; -fx-font-size: 16px;");
        rotLabel.setRotate(-90);
        rotLabel.setMinWidth(Region.USE_PREF_SIZE);
        StackPane labelContainer = new StackPane(rotLabel);
        panel.getChildren().add(labelContainer);
        panel.setOnMouseClicked(e -> restorePanel(panel));
        panel.setStyle(panel.getStyle() + " -fx-cursor: hand;");
    }

    private void restorePanel(VBox panel) {
        int index = navigationContainer.getChildren().indexOf(panel);
        if (index < navigationContainer.getChildren().size() - 1) {
            navigationContainer.getChildren().subList(index + 1, navigationContainer.getChildren().size()).clear();
        }
        PanelData data = (PanelData) panel.getUserData();
        if (data != null) {
            panel.getChildren().clear();
            navigationContainer.getChildren().remove(panel);
            addNavigationPanel(data.items, data.level, data.title);
            // showContent(null); // Optional: clear content area
        }
    }

    private String getColorForLevel(int level) {
        switch (level % 5) {
            case 0:
                return "#8e44ad";
            case 1:
                return "#d35400";
            case 2:
                return "#2980b9";
            case 3:
                return "#27ae60";
            case 4:
                return "#c0392b";
            default:
                return "#7F8C8D";
        }
    }

    private static class PanelData {
        List<Item> items;
        int level;
        String title;

        PanelData(List<Item> items, int level, String title) {
            this.items = items;
            this.level = level;
            this.title = title;
        }
    }

    private static class Item {
        String title;
        List<Item> children;
        List<String> notes;

        Item(String title) {
            this.title = title;
            this.children = new ArrayList<>();
            this.notes = new ArrayList<>();
        }

        void addChild(Item item) {
            children.add(item);
        }

        Item withNote(String note) {
            this.notes.add(note);
            return this;
        }
    }

    private Item createMockData() {
        Item root = new Item("ROOT");
        Item t1 = new Item("Planificación").withNote("MVP objetivos");
        root.addChild(t1);
        Item t2 = new Item("Desarrollo");
        root.addChild(t2);
        t2.addChild(new Item("Backend").withNote("API REST"));
        t2.addChild(new Item("Frontend"));
        return root;
    }
}
