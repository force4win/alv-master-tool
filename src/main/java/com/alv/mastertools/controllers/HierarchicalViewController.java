package com.alv.mastertools.controllers;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

public class HierarchicalViewController {

    @FXML
    private HBox container;

    @FXML
    public void initialize() {
        // Cargar datos de prueba
        Item root = createMockData();

        // Mostrar primer nivel (Lista de Temas Principales)
        addPanel(root.children, 0, "Menú Principal");
    }

    private void addPanel(List<Item> items, int level, String title) {
        // Crear el panel
        VBox panel = new VBox(15);
        panel.getStyleClass().add("hierarchical-panel");
        panel.setMinWidth(300);
        panel.setPrefWidth(300);

        // Guardar los datos en el panel para poder restaurarlo luego
        panel.setUserData(new PanelData(items, level, title));

        // Asignar color según nivel
        String colorStyle = getColorForLevel(level);
        panel.setStyle("-fx-padding: 30; -fx-background-color: " + colorStyle + "; -fx-background-radius: 0;");

        // Titulo del panel
        Label lblTitle = new Label(title);
        lblTitle.setStyle(
                "-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 2, 0, 0, 0);");
        panel.getChildren().add(lblTitle);

        // Separador
        Region line = new Region();
        line.setPrefHeight(2);
        line.setStyle("-fx-background-color: rgba(255,255,255,0.3);");
        panel.getChildren().add(line);

        // Lista de items
        VBox listContainer = new VBox(15);

        // 1. Agregar items existentes
        for (Item item : items) {
            Button btn = createItemButton(item, level, panel);
            listContainer.getChildren().add(btn);
        }

        // 2. Botón "Agregar" al final
        Button btnAdd = new Button("+ Agregar Tema");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.setPrefHeight(40);
        btnAdd.setStyle(
                "-fx-background-color: rgba(0,0,0,0.1); " +
                        "-fx-text-fill: rgba(255,255,255,0.7); " +
                        "-fx-border-color: rgba(255,255,255,0.5); " +
                        "-fx-border-style: dashed; " +
                        "-fx-border-radius: 4; " +
                        "-fx-cursor: hand;");
        btnAdd.setOnAction(e -> handleAddItem(items, listContainer, level, panel));

        // Hover effect para el boton agregar
        btnAdd.setOnMouseEntered(e -> btnAdd.setStyle(btnAdd.getStyle()
                .replace("-fx-background-color: rgba(0,0,0,0.1);", "-fx-background-color: rgba(255,255,255,0.2);")));
        btnAdd.setOnMouseExited(e -> btnAdd.setStyle(btnAdd.getStyle()
                .replace("-fx-background-color: rgba(255,255,255,0.2);", "-fx-background-color: rgba(0,0,0,0.1);")));

        listContainer.getChildren().add(btnAdd);

        // Scroll si hay muchos items
        ScrollPane scroll = new ScrollPane(listContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        panel.getChildren().add(scroll);

        // Animar entrada
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), panel);
        tt.setFromX(50);
        tt.setToX(0);
        tt.play();

        container.getChildren().add(panel);
    }

    private Button createItemButton(Item item, int level, VBox panel) {
        Button btn = new Button(item.title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(50);

        // Estilo especifico botones
        btn.setStyle(
                "-fx-background-color: rgba(0,0,0,0.2); " +
                        "-fx-text-fill: white; " +
                        "-fx-alignment: CENTER-LEFT; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-border-color: rgba(255,255,255,0.4); " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 4; " +
                        "-fx-background-radius: 4; " +
                        "-fx-cursor: hand;");

        // Hover
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle().replace("-fx-background-color: rgba(0,0,0,0.2);",
                "-fx-background-color: rgba(255,255,255,0.3);")));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace("-fx-background-color: rgba(255,255,255,0.3);",
                "-fx-background-color: rgba(0,0,0,0.2);")));

        btn.setOnAction(e -> handleItemSelect(item, level, panel));
        return btn;
    }

    private void handleAddItem(List<Item> items, VBox listContainer, int level, VBox panel) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Nuevo Elemento");
        dialog.setHeaderText(null);
        dialog.setContentText("Nombre del nuevo tema:");
        // Estilo simple para el dialogo
        dialog.getDialogPane().setStyle("-fx-background-color: -color-bg-card; -fx-text-fill: -color-text-primary;");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (name.trim().isEmpty())
                return;

            // 1. Crear en Modelo
            Item newItem = new Item(name);
            items.add(newItem);

            // 2. Crear en Vista
            Button btn = createItemButton(newItem, level, panel);

            // Insertar antes del último elemento (que es el botón Agregar)
            int index = listContainer.getChildren().size() - 1;
            if (index < 0)
                index = 0;
            listContainer.getChildren().add(index, btn);
        });
    }

    private void handleItemSelect(Item item, int level, VBox currentPanel) {
        int currentIndex = container.getChildren().indexOf(currentPanel);

        // 1. Eliminar paneles a la derecha (futuro)
        if (currentIndex < container.getChildren().size() - 1) {
            container.getChildren().subList(currentIndex + 1, container.getChildren().size()).clear();
        }

        // 2. Colapsar panel actual
        collapsePanel(currentPanel, item.title, level);

        // 3. Mostrar siguiente nivel
        // Siempre permitir agregar hijos, incluso si estaba vacío antes, iniciamos
        // lista vacía si es null
        if (item.children == null) {
            item.children = new ArrayList<>();
        }

        // Mostrar panel de hijos (incluso si está vacío, para poder agregar)
        // OJO: Si es hoja (nivel muy profundo o intención explicita) se mostraría
        // contenido.
        // Aquí asumimos que siempre se pueden agregar hijos hasta que decidamos qué es
        // "contenido final".
        // Para simular "contenido" vs "categoría", podríamos chequear si tiene hijos o
        // no, pero ahora queremos CONSTRUIR.
        // Así que siempre abrimos panel de hijos para permitir agregar, EXCEPTO que sea
        // un nivel muy profundo o usemos un flag.
        // Usemso la lógica: Si tiene hijos O si el usuario acaba de crearlo (está
        // vacío), mostramos panel para agregar hijos.
        // ¿Cuándo mostramos contenido final? Quizás con un botón "Ver Detalle" dentro
        // del item, pero el clic principal navega.
        // Para este demo, asumiremos que siempre navegamos a hijos.

        addPanel(item.children, level + 1, item.title);
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
        rotLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
        rotLabel.setRotate(-90);
        rotLabel.setMinWidth(Region.USE_PREF_SIZE);

        StackPane labelContainer = new StackPane(rotLabel);

        panel.getChildren().add(labelContainer);

        panel.setOnMouseClicked(e -> restorePanel(panel));
        panel.setStyle(panel.getStyle() + " -fx-cursor: hand;");
    }

    private void restorePanel(VBox panel) {
        int index = container.getChildren().indexOf(panel);

        if (index < container.getChildren().size() - 1) {
            container.getChildren().subList(index + 1, container.getChildren().size()).clear();
        }

        PanelData data = (PanelData) panel.getUserData();
        if (data != null) {
            panel.getChildren().clear();
            container.getChildren().remove(panel);
            addPanel(data.items, data.level, data.title);
        }
    }

    private String getColorForLevel(int level) {
        switch (level % 5) {
            case 0:
                return "#9b59b6"; // Purple
            case 1:
                return "#e67e22"; // Orange
            case 2:
                return "#2980b9"; // Blue
            case 3:
                return "#27ae60"; // Green
            case 4:
                return "#c0392b"; // Red
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

        Item(String title) {
            this.title = title;
            this.children = new ArrayList<>();
        }

        void addChild(Item item) {
            children.add(item);
        }
    }

    private Item createMockData() {
        Item root = new Item("ROOT");

        Item t1 = new Item("Tema 1");
        root.addChild(t1);

        Item t2 = new Item("Tema 2");
        root.addChild(t2);
        t2.addChild(new Item("Tema 2.1"));
        t2.addChild(new Item("Tema 2.2"));

        return root;
    }
}
