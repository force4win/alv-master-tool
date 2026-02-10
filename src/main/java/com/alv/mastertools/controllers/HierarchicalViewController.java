package com.alv.mastertools.controllers;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

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
        for (Item item : items) {
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

            // Hover override manual (css es mejor, pero in-line para demo rapido)
            btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle().replace("-fx-background-color: rgba(0,0,0,0.2);",
                    "-fx-background-color: rgba(255,255,255,0.3);")));
            btn.setOnMouseExited(
                    e -> btn.setStyle(btn.getStyle().replace("-fx-background-color: rgba(255,255,255,0.3);",
                            "-fx-background-color: rgba(0,0,0,0.2);")));

            btn.setOnAction(e -> handleItemSelect(item, level, panel));
            listContainer.getChildren().add(btn);
        }

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

    private void handleItemSelect(Item item, int level, VBox currentPanel) {
        int currentIndex = container.getChildren().indexOf(currentPanel);

        // 1. Eliminar paneles a la derecha (futuro)
        if (currentIndex < container.getChildren().size() - 1) {
            container.getChildren().subList(currentIndex + 1, container.getChildren().size()).clear();
        }

        // 2. Colapsar panel actual
        collapsePanel(currentPanel, item.title, level);

        // 3. Mostrar siguiente nivel
        if (item.children != null && !item.children.isEmpty()) {
            addPanel(item.children, level + 1, item.title); // Titulo del nuevo panel es el item seleccionado
        } else {
            showContent(item, level + 1);
        }
    }

    private void collapsePanel(VBox panel, String selectedTitle, int level) {
        panel.getChildren().clear();

        // Animar reducción de ancho
        // (Nota: Animación real de ancho en layout HBox es compleja, saltamos a size
        // fijo directo)
        panel.setMinWidth(60);
        panel.setPrefWidth(60);
        panel.setMaxWidth(60);
        panel.setAlignment(Pos.CENTER);

        // Color ligeramente más oscuro para indicar inactivo/fondo
        String colorStyle = getColorForLevel(level);
        // Oscurecer un poco (manual hack: usar overlay negro)
        panel.setStyle("-fx-padding: 0; -fx-background-color: " + colorStyle
                + "; -fx-border-color: rgba(0,0,0,0.2); -fx-border-width: 0 1 0 0;");

        // Etiqueta vertical
        Label rotLabel = new Label(selectedTitle);
        rotLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
        rotLabel.setRotate(-90);
        rotLabel.setMinWidth(Region.USE_PREF_SIZE);

        // Contenedor para la etiqueta
        StackPane labelContainer = new StackPane(rotLabel);

        panel.getChildren().add(labelContainer);

        // Evento para restaurar
        panel.setOnMouseClicked(e -> restorePanel(panel));
        panel.setStyle(panel.getStyle() + " -fx-cursor: hand;");
    }

    private void restorePanel(VBox panel) {
        int index = container.getChildren().indexOf(panel);

        // 1. Eliminar todos los hijos posteriores
        if (index < container.getChildren().size() - 1) {
            container.getChildren().subList(index + 1, container.getChildren().size()).clear();
        }

        // 2. Restaurar contenido
        PanelData data = (PanelData) panel.getUserData();
        if (data != null) {
            // Limpiar visual light strip
            panel.getChildren().clear();

            // Re-eliminar y re-añadir (hack sucio pero efectivo para resetear propiedades)
            // container.getChildren().remove(index);
            // addPanel(data.items, data.level, data.title);
            // ^ Esto cambiaría el orden o animación, mejor reconstruir in-place:

            container.getChildren().remove(panel);
            // Recrear en la misma posición (será añadido al final pq borramos los
            // siguientes)
            addPanel(data.items, data.level, data.title);
        }
    }

    private void showContent(Item item, int level) {
        VBox contentPanel = new VBox(20);
        contentPanel.setStyle("-fx-background-color: derive(-color-bg-base, 10%); -fx-padding: 50;");
        HBox.setHgrow(contentPanel, Priority.ALWAYS); // Ocupar todo el resto

        Label title = new Label(item.title);
        title.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: -color-text-primary;");

        Label subtitle = new Label("Detalles y Configuración");
        subtitle.setStyle("-fx-font-size: 24px; -fx-text-fill: -color-accent;");

        Label body = new Label(
                "Aquí se muestra el contenido detallado para '" + item.title + "'.\n\n" +
                        "Esta vista se ha generado dinámicamente siguiendo la jerarquía seleccionada.\n" +
                        "Puedes volver atrás haciendo clic en las barras de colores a la izquierda.");
        body.setWrapText(true);
        body.setStyle("-fx-font-size: 18px; -fx-text-fill: -color-text-secondary; -fx-line-spacing: 5;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button actionBtn = new Button("Realizar Acción en " + item.title);
        actionBtn.setStyle("-fx-font-size: 16px; -fx-padding: 15 30;");
        actionBtn.getStyleClass().add("button-success");

        contentPanel.getChildren().addAll(title, subtitle, body, spacer, actionBtn);

        // Animar
        TranslateTransition tt = new TranslateTransition(Duration.millis(400), contentPanel);
        tt.setFromX(100);
        tt.setToX(0);
        tt.play();

        container.getChildren().add(contentPanel);
    }

    private String getColorForLevel(int level) {
        // Colores de referencia aproximados
        switch (level % 5) {
            case 0:
                return "#9b59b6"; // Purple (Tema 1-4 list)
            case 1:
                return "#e67e22"; // Orange (Tema 2 selected)
            case 2:
                return "#2980b9"; // Blue (Tema 2.4 selected)
            case 3:
                return "#27ae60"; // Green (Tema 2.4.3 selected)
            case 4:
                return "#c0392b"; // Red
            default:
                return "#7F8C8D";
        }
    }

    // --- DATA MODELS ---

    // Wrapper para guardar estado del panel y poder restaurar
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
        Item root = new Item("ROOT"); // Invisible root

        // Nivel 1
        Item t1 = new Item("Tema 1");
        root.addChild(t1);
        t1.addChild(new Item("Subtema 1.1"));
        t1.addChild(new Item("Subtema 1.2"));

        Item t2 = new Item("Tema 2");
        root.addChild(t2);
        Item t21 = new Item("Tema 2.1");
        t2.addChild(t21);
        Item t22 = new Item("Tema 2.2");
        t2.addChild(t22);
        Item t23 = new Item("Tema 2.3");
        t2.addChild(t23);
        Item t24 = new Item("Tema 2.4");
        t2.addChild(t24);
        Item t241 = new Item("Tema 2.4.1");
        t24.addChild(t241);
        Item t242 = new Item("Tema 2.4.2");
        t24.addChild(t242);
        Item t243 = new Item("Tema 2.4.3");
        t24.addChild(t243);
        Item t244 = new Item("Tema 2.4.4");
        t24.addChild(t244);

        Item t3 = new Item("Tema 3");
        root.addChild(t3);
        t3.addChild(new Item("Vista General 3"));

        Item t4 = new Item("Tema 4");
        root.addChild(t4);
        t4.addChild(new Item("Configuración 4"));

        return root;
    }
}
