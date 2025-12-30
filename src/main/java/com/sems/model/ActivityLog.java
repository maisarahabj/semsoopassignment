/**
 *
 * @author maisarahabjalil
 */

package com.sems.model;

import java.sql.Timestamp;

public class ActivityLog {
    private int logId;
    private int userId;
    private Integer targetId; 
    private String actionType;
    private String description;
    private Timestamp timestamp;

    // fields for "Display Logic" in the Admin table
    // fill these using a SQL JOIN in the DAO
    private String performerName;
    private String performerRole;

    // Constructor
    public ActivityLog() {}

    // Getters and Setters
    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Integer getTargetId() { return targetId; }
    public void setTargetId(Integer targetId) { this.targetId = targetId; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public String getPerformerName() { return performerName; }
    public void setPerformerName(String performerName) { this.performerName = performerName; }

    public String getPerformerRole() { return performerRole; }
    public void setPerformerRole(String performerRole) { this.performerRole = performerRole; }
}