package com.alv.mastertools.models;

import java.time.LocalTime;

public class TrackerSettings {
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalMinutes;
    private boolean active;
    private boolean autoStartOnLogin = false;
    private String theme = "dark";

    public TrackerSettings(LocalTime startTime, LocalTime endTime, int intervalMinutes) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalMinutes = intervalMinutes;
        this.active = false;
    }

    // Constructor por defecto
    public TrackerSettings() {
        this.startTime = LocalTime.of(9, 0);
        this.endTime = LocalTime.of(18, 0);
        this.intervalMinutes = 15;
        this.active = false;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public int getIntervalMinutes() {
        return intervalMinutes;
    }

    public void setIntervalMinutes(int intervalMinutes) {
        this.intervalMinutes = intervalMinutes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isAutoStartOnLogin() {
        return autoStartOnLogin;
    }

    public void setAutoStartOnLogin(boolean autoStartOnLogin) {
        this.autoStartOnLogin = autoStartOnLogin;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    // Credenciales
    private String username = "admin";
    private String password = "123";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
