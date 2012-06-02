package com.skymobi.monitor.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author Hill.Hu
 */
@Document
public class Log {
    @Id
    private String id;
    private String message,level;
    private Date timestamp;
    private LoggerName loggerName;
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LoggerName getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(LoggerName loggerName) {
        this.loggerName = loggerName;
    }
}
