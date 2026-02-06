package com.alv.mastertools.services;

import com.alv.mastertools.models.TaskLog;
import com.alv.mastertools.models.TrackerSettings;
import com.alv.mastertools.persistence.IDataProvider;
import com.alv.mastertools.persistence.RepositoryFactory;
import com.alv.mastertools.App;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TrackerService {

    private static TrackerService instance;
    private TrackerSettings settings;
    private List<TaskLog> logs;
    private Timer timer;
    private String lastTaskDescription = "";
    private boolean isPopupOpen = false;

    private IDataProvider dataProvider;

    private TrackerService() {
        this.dataProvider = RepositoryFactory.getProvider();
        this.settings = this.dataProvider.loadSettings();
        this.logs = this.dataProvider.loadLogs();
        // Filtrar logs para la sesión actual?
        // Por ahora cargamos todos, pero quizás la UI quiera filtrar.
        // O mejor: mantenemos los logs cargados en memoria.

        // Si estaba activo al cargar, quizás queramos reanudar o no.
        // Por seguridad, al iniciar la app, mejor que arranque detenido o pregunte.
        // Pero cargamos el estado guardado. Si era true, lo dejamos en true?
        // Mejor forzamos false al inicio de la app para evitar pops inesperados,
        // o respetamos la config. Respetémosla pero sin timer iniciado aún.
        if (this.settings.isActive()) {
            // Si estaba activo, deberíamos iniciarlo?
            // El usuario tendrá que darle click de nuevo o lo iniciamos automático?
            // Vamos a dejarlo en pausa visualmente pero con el flag cargado,
            // O mejor, forzamos false para que el usuario inicie conscientemente.
            this.settings.setActive(false);
        }
    }

    public static TrackerService get() {
        if (instance == null) {
            instance = new TrackerService();
        }
        return instance;
    }

    // Método explícito para guardar configuración
    public void saveConfiguration() {
        this.dataProvider.saveSettings(this.settings);
    }

    // ... rest of code ...

    public void startTracking() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer(true);
        settings.setActive(true);
        saveConfiguration(); // Guardar estado activo

        // Disparar inmediatamente al iniciar (si es horario válido)
        Platform.runLater(this::checkAndTrigger);

        // Timer que revisa CADA MINUTO si debe disparar
        long period = settings.getIntervalMinutes() * 60 * 1000L;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkAndTrigger();
            }
        }, period, period);
    }

    public void stopTracking() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        settings.setActive(false);
        saveConfiguration(); // Guardar estado detenido
    }

    // ...

    public void logTask(String description) {
        this.lastTaskDescription = description;
        // Asumimos segundo 0 para que los cálculos de duración en minutos sean exactos
        // (resta directa)
        LocalDateTime timestamp = LocalDateTime.now().withSecond(0).withNano(0);
        TaskLog newLog = new TaskLog(timestamp, description);
        this.logs.add(newLog);

        this.dataProvider.saveLog(newLog); // Persistir

        System.out.println("LOG GUARDADO: " + description);

        if (onLogAddedCallback != null) {
            Platform.runLater(onLogAddedCallback);
        }
    }

    public TrackerSettings getSettings() {
        return settings;
    }

    public List<TaskLog> getLogs() {
        return logs;
    }

    public String getLastTaskDescription() {
        return lastTaskDescription;
    }

    private Runnable onLogAddedCallback;

    public void setOnLogAdded(Runnable callback) {
        this.onLogAddedCallback = callback;
    }

    private void checkAndTrigger() {
        if (!settings.isActive())
            return;
        if (isPopupOpen)
            return; // Ya hay uno abierto

        LocalTime now = LocalTime.now();
        if (now.isBefore(settings.getStartTime()) || now.isAfter(settings.getEndTime())) {
            // Fuera de horario laboral
            return;
        }

        // DISPARAR POPUP EN HILO UI
        Platform.runLater(this::showPopup);
    }

    /**
     * Permite abrir el popup de registro de tarea manualmente,
     * ignorando las restricciones de horario/estado activo,
     * pero respetando si ya hay un popup abierto.
     */
    public void openManualEntryPopup() {
        Platform.runLater(this::showPopup);
    }

    private void showPopup() {
        if (isPopupOpen)
            return; // Prevent duplicates

        try {
            isPopupOpen = true;
            FXMLLoader loader = new FXMLLoader(App.class.getResource("views/reminder_popup.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UTILITY); // Menos bordes
            stage.setAlwaysOnTop(true);
            stage.setTitle("¿Qué estás haciendo?");
            stage.setScene(new Scene(root));

            // Cuando se cierre, marcamos como cerrado
            stage.setOnHidden(e -> isPopupOpen = false);

            stage.show();
            stage.toFront();
            stage.requestFocus(); // Intentar robar foco

        } catch (IOException e) {
            e.printStackTrace();
            isPopupOpen = false;
        }
    }

}
