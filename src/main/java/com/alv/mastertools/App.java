package com.alv.mastertools;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    public static final boolean IS_DEV_MODE = true; // CAMBIAR A 'false' PARA PRODUCCION

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Cargar el icono de la aplicación
        try {
            javafx.scene.image.Image icon = new javafx.scene.image.Image(
                    App.class.getResourceAsStream("AlvMasterTool.png"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el icono: " + e.getMessage());
        }

        // Carga el archivo FXML 'login' desde los recursos
        scene = new Scene(loadFXML("login"), 640, 480);
        stage.setScene(scene);
        stage.setTitle("Master Tool Login");
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    // Método helper para cargar el FXML
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("views/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void maximizeWindow() {
        if (scene != null && scene.getWindow() instanceof Stage) {
            ((Stage) scene.getWindow()).setMaximized(true);
        }
    }

    public static void main(String[] args) {
        launch();
    }

}
