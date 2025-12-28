package com.sems.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Academic Calendar Event model
 * @author SEMS Team
 */
public class CalendarEvent {
    private int eventId;
    private int semesterId;
    private String eventTitle;
    private String eventType; // EXAM, REGISTRATION, HOLIDAY, DEADLINE, OTHER
    private Date eventDate;
    private Date endDate; // For multi-day events
    private String description;
    private Timestamp createdDate;
    
    // For display
    private String semesterName;

    // Constructors
    public CalendarEvent() {
    }

    public CalendarEvent(int semesterId, String eventTitle, String eventType, Date eventDate) {
        this.semesterId = semesterId;
        this.eventTitle = eventTitle;
        this.eventType = eventType;
        this.eventDate = eventDate;
    }

    // Getters and Setters
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }
    
    public boolean isMultiDay() {
        return endDate != null && !endDate.equals(eventDate);
    }

    @Override
    public String toString() {
        return "CalendarEvent{" +
                "eventId=" + eventId +
                ", semesterId=" + semesterId +
                ", eventTitle='" + eventTitle + '\'' +
                ", eventType='" + eventType + '\'' +
                ", eventDate=" + eventDate +
                '}';
    }
}
