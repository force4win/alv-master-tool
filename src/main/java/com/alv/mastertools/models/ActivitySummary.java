package com.alv.mastertools.models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActivitySummary {
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public ActivitySummary(String description, LocalDateTime startTime, LocalDateTime endTime) {
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = Duration.between(startTime, endTime);
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getFormattedStartTime() {
        return startTime.format(TIME_FORMATTER);
    }

    public String getFormattedEndTime() {
        return endTime.format(TIME_FORMATTER);
    }

    public String getFormattedDuration() {
        long minutes = duration.toMinutes();

        if (minutes < 60) {
            return minutes + " min";
        } else {
            long hours = minutes / 60;
            long remMinutes = minutes % 60;
            return hours + " h " + remMinutes + " min";
        }
    }

    // Setters para actualizar el fin dinámicamente
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        this.duration = Duration.between(this.startTime, this.endTime);
    }
}
