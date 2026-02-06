package com.alv.mastertools.models;

import java.time.LocalDateTime;

public class TaskLog {
    private LocalDateTime timestamp;
    private String description;

    public TaskLog(LocalDateTime timestamp, String description) {
        this.timestamp = timestamp;
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }
}
