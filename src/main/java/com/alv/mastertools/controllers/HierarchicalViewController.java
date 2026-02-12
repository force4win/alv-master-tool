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
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
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
        addNavigationPanel(root.children, 0, "Menú Principal", null);

        // Inicialmente mostrar contenido vacío o bienvenida
        showContent(null);
    }

    private void addNavigationPanel(List<Item> items, int level, String title, Item parentItem) {
        // Crear panel de navegación
        VBox panel = new VBox(0);
        panel.getStyleClass().add("hierarchical-panel");
        panel.setMinWidth(250);
        panel.setPrefWidth(250);

        // Guardar estado
        panel.setUserData(new PanelData(items, level, title, parentItem));

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

        // Free-form Container for Notes (Canvas)
        Pane notesContainer = new Pane();
        // Visual indicator: Dashed border and subtle background to mark the workspace
        notesContainer.setStyle(
                "-fx-background-color: rgba(0,0,0,0.03); -fx-border-color: rgba(128,128,128,0.3); -fx-border-style: dashed; -fx-border-width: 2;");
        notesContainer.setPrefSize(2000, 2000);
        notesContainer.setMinSize(2000, 2000); // Force scrollbars to appear

        renderNotes(selectedItem, notesContainer);

        ScrollPane notesScroll = new ScrollPane(notesContainer);
        // Disable fit-to to allow 2D scrolling over the large canvas
        notesScroll.setFitToWidth(false);
        notesScroll.setFitToHeight(false);
        notesScroll.setPannable(true); // Allow panning with mouse (grab and move)
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
                // Add new note at default position
                selectedItem.notes.add(new NoteData(text, 50, 50, 200, 150));
                renderNotes(selectedItem, notesContainer);
                noteInput.clear();
            }
        });

        Button btnClearNotes = new Button("Borrar Todas");
        btnClearNotes.getStyleClass().add("button-danger"); // Assuming a danger class exists or default style
        btnClearNotes.setStyle(
                "-fx-background-color: #ffcdd2; -fx-text-fill: #c62828; -fx-border-color: #ef9a9a; -fx-border-radius: 4; -fx-background-radius: 4;");
        btnClearNotes.setPrefHeight(50);
        btnClearNotes.setOnAction(e -> {
            if (!selectedItem.notes.isEmpty()) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmar eliminación");
                alert.setHeaderText("¿Borrar todas las notas?");
                alert.setContentText("Esta acción no se puede deshacer.");

                alert.showAndWait().ifPresent(response -> {
                    if (response == javafx.scene.control.ButtonType.OK) {
                        selectedItem.notes.clear();
                        renderNotes(selectedItem, notesContainer);
                    }
                });
            }
        });

        inputBox.getChildren().addAll(noteInput, btnAddNote, btnClearNotes);
        contentArea.getChildren().add(inputBox);
    }

    private void renderNotes(Item item, Pane container) {
        container.getChildren().clear();
        if (item.notes.isEmpty()) {
            Label empty = new Label("No hay notas registradas. Agrega una abajo.");
            empty.setStyle("-fx-text-fill: -color-text-muted; -fx-font-style: italic;");
            empty.setLayoutX(20);
            empty.setLayoutY(20);
            container.getChildren().add(empty);
            return;
        }

        for (NoteData note : item.notes) {
            Node noteNode = createDraggableNote(note, item, container);
            container.getChildren().add(noteNode);
        }
    }

    private Node createDraggableNote(NoteData data, Item parentItem, Pane container) {
        // Main Note Container
        VBox noteBox = new VBox();
        noteBox.setPrefSize(data.width, data.height);
        // Removed fixed min size to allow free resizing,
        // relying on resize logic and component min sizes to prevent disappearance
        noteBox.setMinSize(40, 40);
        noteBox.setLayoutX(data.x);
        noteBox.setLayoutY(data.y);
        noteBox.setStyle(
                "-fx-background-color: #fff9c4; " +
                        "-fx-border-color: #fbc02d; " +
                        "-fx-border-width: 1; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 3, 3);");

        // Drag Handle (Top Bar)
        HBox dragHandle = new HBox();
        dragHandle.setPrefHeight(20);
        dragHandle.setMinHeight(20); // Always keep this height
        dragHandle.setMaxHeight(20);
        dragHandle.setAlignment(Pos.CENTER_RIGHT);
        dragHandle.setStyle("-fx-background-color: rgba(251, 192, 45, 0.3); -fx-cursor: move;");

        // Close Button
        // Close Button (Red X)
        Label closeBtn = new Label("✕");
        closeBtn.setStyle(
                "-fx-text-fill: #b71c1c; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 0 8 0 8;");

        // Prevent Drag when clicking close button
        closeBtn.setOnMousePressed(e -> e.consume());

        closeBtn.setOnMouseClicked(e -> {
            parentItem.notes.remove(data);
            container.getChildren().remove(noteBox);
            e.consume();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        // Important: spacer allows dragging from the empty area, button allows closing
        // Since the HBox has the drag listener, we need to ensure the spacer passes it
        // through or the HBox handles it.
        // Actually, the HBox itself has the listener. Elements inside like Label might
        // block it if they consume events.
        // Label defaults to not consuming unless we set a handler.
        // Spacer is transparent to hits mostly unless configured otherwise, but HBox
        // catches the background.

        dragHandle.getChildren().addAll(spacer, closeBtn);

        // Content (TextArea for editing or Label)
        TextArea contentObj = new TextArea(data.content);
        contentObj.setWrapText(true);
        contentObj.setStyle(
                "-fx-control-inner-background: #fff9c4; -fx-background-color: transparent; -fx-text-fill: #3e2723; -fx-border-width: 0; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
        VBox.setVgrow(contentObj, Priority.ALWAYS);

        // Update content on change
        contentObj.textProperty().addListener((obs, old, newVal) -> data.content = newVal);

        // Resize Handle
        Label resizeHandle = new Label("◢");
        resizeHandle.setStyle("-fx-text-fill: #f9a825; -fx-cursor: se-resize; -fx-font-size: 10px;");
        HBox resizeBox = new HBox(resizeHandle);
        resizeBox.setAlignment(Pos.BOTTOM_RIGHT);
        resizeBox.setPadding(new javafx.geometry.Insets(2));
        resizeBox.setStyle("-fx-background-color: transparent;");

        noteBox.getChildren().addAll(dragHandle, contentObj, resizeBox);

        // --- DRAG LOGIC ---
        final Delta dragDelta = new Delta();
        dragHandle.setOnMousePressed(e -> {
            dragDelta.x = noteBox.getLayoutX() - e.getSceneX();
            dragDelta.y = noteBox.getLayoutY() - e.getSceneY();
            noteBox.toFront(); // Move to top
            e.consume();
        });
        dragHandle.setOnMouseDragged(e -> {
            double newX = e.getSceneX() + dragDelta.x;
            double newY = e.getSceneY() + dragDelta.y;
            // Boundaries check could go here
            noteBox.setLayoutX(newX);
            noteBox.setLayoutY(newY);
            data.x = newX;
            data.y = newY;
            e.consume();
        });

        // --- RESIZE LOGIC ---
        resizeHandle.setOnMousePressed(e -> {
            dragDelta.x = e.getX(); // Store offset within handle
            dragDelta.y = e.getY();
            e.consume();
        });
        resizeHandle.setOnMouseDragged(e -> {
            // Calculate new size based on mouse position

            double mouseX = e.getSceneX();
            double mouseY = e.getSceneY();
            double noteX = noteBox.localToScene(0, 0).getX();
            double noteY = noteBox.localToScene(0, 0).getY();

            double newWidth = mouseX - noteX;
            double newHeight = mouseY - noteY;

            // Allow resizing down to a very small size (30px)
            if (newWidth > 30) {
                noteBox.setPrefWidth(newWidth);
                data.width = newWidth;
            }
            if (newHeight > 30) {
                noteBox.setPrefHeight(newHeight);
                data.height = newHeight;
            }
            e.consume();
        });

        return noteBox;
    }

    private static class Delta {
        double x, y;
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
        addNavigationPanel(item.children, level + 1, item.title, item);
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
            addNavigationPanel(data.items, data.level, data.title, data.parentItem);
            showContent(data.parentItem);
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
        Item parentItem;

        PanelData(List<Item> items, int level, String title, Item parentItem) {
            this.items = items;
            this.level = level;
            this.title = title;
            this.parentItem = parentItem;
        }
    }

    private static class Item {
        String title;
        List<Item> children;
        List<NoteData> notes;

        Item(String title) {
            this.title = title;
            this.children = new ArrayList<>();
            this.notes = new ArrayList<>();
        }

        void addChild(Item item) {
            children.add(item);
        }

        Item withNote(String content) {
            // Default Post-It Style
            this.notes.add(new NoteData(content, 50 + (notes.size() * 30), 50 + (notes.size() * 30), 220, 180));
            return this;
        }
    }

    private static class NoteData {
        String content;
        double x, y, width, height;

        NoteData(String content, double x, double y, double width, double height) {
            this.content = content;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
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
