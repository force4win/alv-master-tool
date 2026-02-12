package com.alv.mastertools.persistence;

import com.alv.mastertools.models.TaskLog;
import com.alv.mastertools.models.TrackerSettings;
import java.util.List;

public interface IDataProvider {
    void saveSettings(TrackerSettings settings);

    TrackerSettings loadSettings();

    void saveLog(TaskLog log);

    List<TaskLog> loadLogs();

    // Hierarchical Data persistence
    void saveTopics(com.alv.mastertools.models.TopicItem root);

    com.alv.mastertools.models.TopicItem loadTopics();
}
