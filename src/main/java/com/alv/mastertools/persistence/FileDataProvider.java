package com.alv.mastertools.persistence;

import com.alv.mastertools.models.TaskLog;
import com.alv.mastertools.models.TrackerSettings;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FileDataProvider implements IDataProvider {

    private static final String DIR_NAME_PROD = ".alv-master";
    private static final String DIR_NAME_DEV = "DEV_.alv-master";
    private static final String SETTINGS_FILE = "settings.properties";
    private static final String LOGS_FILE = "activity_logs.csv";
    private static final DateTimeFormatter DZF = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private Path storageDir;

    public FileDataProvider() {
        String userHome = System.getProperty("user.home");
        String dirName = com.alv.mastertools.App.IS_DEV_MODE ? DIR_NAME_DEV : DIR_NAME_PROD;
        this.storageDir = Paths.get(userHome, dirName);
        initialize();
    }

    private void initialize() {
        try {
            if (!Files.exists(storageDir)) {
                Files.createDirectories(storageDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveSettings(TrackerSettings settings) {
        Properties props = new Properties();
        props.setProperty("startTime", settings.getStartTime().toString());
        props.setProperty("endTime", settings.getEndTime().toString());
        props.setProperty("intervalMinutes", String.valueOf(settings.getIntervalMinutes()));
        props.setProperty("isActive", String.valueOf(settings.isActive()));
        props.setProperty("isActive", String.valueOf(settings.isActive()));
        props.setProperty("autoStartOnLogin", String.valueOf(settings.isAutoStartOnLogin()));
        props.setProperty("theme", settings.getTheme());
        props.setProperty("username", settings.getUsername());
        props.setProperty("password", settings.getPassword());

        try (OutputStream output = Files.newOutputStream(storageDir.resolve(SETTINGS_FILE))) {
            props.store(output, "AlvMasterTool Tracker Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TrackerSettings loadSettings() {
        // Defaults
        TrackerSettings settings = new TrackerSettings();
        Path file = storageDir.resolve(SETTINGS_FILE);

        if (Files.exists(file)) {
            Properties props = new Properties();
            try (InputStream input = Files.newInputStream(file)) {
                props.load(input);

                settings.setStartTime(LocalTime.parse(props.getProperty("startTime", "09:00")));
                settings.setEndTime(LocalTime.parse(props.getProperty("endTime", "18:00")));
                settings.setIntervalMinutes(Integer.parseInt(props.getProperty("intervalMinutes", "30")));
                settings.setActive(Boolean.parseBoolean(props.getProperty("isActive", "false")));
                settings.setActive(Boolean.parseBoolean(props.getProperty("isActive", "false")));
                settings.setAutoStartOnLogin(Boolean.parseBoolean(props.getProperty("autoStartOnLogin", "false")));
                settings.setTheme(props.getProperty("theme", "dark"));
                settings.setUsername(props.getProperty("username", "admin"));
                settings.setPassword(props.getProperty("password", "123"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return settings;
    }

    @Override
    public void saveLog(TaskLog log) {
        String line = log.getTimestamp().format(DZF) + "|" + log.getDescription() + System.lineSeparator();
        try {
            Files.write(storageDir.resolve(LOGS_FILE), line.getBytes(), java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<TaskLog> loadLogs() {
        List<TaskLog> logs = new ArrayList<>();
        Path file = storageDir.resolve(LOGS_FILE);

        if (Files.exists(file)) {
            try {
                List<String> lines = Files.readAllLines(file);
                for (String line : lines) {
                    if (line.trim().isEmpty())
                        continue;
                    String[] parts = line.split("\\|", 2);
                    if (parts.length == 2) {
                        LocalDateTime timestamp = LocalDateTime.parse(parts[0], DZF);
                        String description = parts[1];
                        logs.add(new TaskLog(timestamp, description));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return logs;
    }
}
