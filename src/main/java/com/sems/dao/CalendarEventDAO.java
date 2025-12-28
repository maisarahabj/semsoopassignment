package com.sems.dao;

import com.sems.model.CalendarEvent;
import com.sems.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Calendar Event operations
 * @author SEMS Team
 */
public class CalendarEventDAO {
    
    private static final Logger LOGGER = Logger.getLogger(CalendarEventDAO.class.getName());
    
    /**
     * Create a new calendar event
     */
    public boolean createEvent(CalendarEvent event) {
        String sql = "INSERT INTO calendar_events (semester_id, event_title, event_type, " +
                    "event_date, end_date, description) VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setInt(1, event.getSemesterId());
            pstmt.setString(2, event.getEventTitle());
            pstmt.setString(3, event.getEventType());
            pstmt.setDate(4, event.getEventDate());
            pstmt.setDate(5, event.getEndDate());
            pstmt.setString(6, event.getDescription());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating calendar event", e);
        } finally {
            DatabaseConnection.closeResources(null, pstmt, conn);
        }
        return false;
    }
    
    /**
     * Get all events for a specific semester
     */
    public List<CalendarEvent> getEventsBySemester(int semesterId) {
        List<CalendarEvent> events = new ArrayList<>();
        String sql = "SELECT ce.*, s.semester_name " +
                    "FROM calendar_events ce " +
                    "JOIN semesters s ON ce.semester_id = s.semester_id " +
                    "WHERE ce.semester_id = ? " +
                    "ORDER BY ce.event_date";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, semesterId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching events for semester " + semesterId, e);
        } finally {
            DatabaseConnection.closeResources(rs, pstmt, conn);
        }
        
        return events;
    }
    
    /**
     * Get all upcoming events
     */
    public List<CalendarEvent> getUpcomingEvents() {
        List<CalendarEvent> events = new ArrayList<>();
        String sql = "SELECT ce.*, s.semester_name " +
                    "FROM calendar_events ce " +
                    "JOIN semesters s ON ce.semester_id = s.semester_id " +
                    "WHERE ce.event_date >= CURDATE() " +
                    "ORDER BY ce.event_date " +
                    "LIMIT 10";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching upcoming events", e);
        } finally {
            DatabaseConnection.closeResources(rs, pstmt, conn);
        }
        
        return events;
    }
    
    /**
     * Get all events
     */
    public List<CalendarEvent> getAllEvents() {
        List<CalendarEvent> events = new ArrayList<>();
        String sql = "SELECT ce.*, s.semester_name " +
                    "FROM calendar_events ce " +
                    "JOIN semesters s ON ce.semester_id = s.semester_id " +
                    "ORDER BY ce.event_date DESC";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all events", e);
        } finally {
            DatabaseConnection.closeResources(rs, pstmt, conn);
        }
        
        return events;
    }
    
    /**
     * Delete an event
     */
    public boolean deleteEvent(int eventId) {
        String sql = "DELETE FROM calendar_events WHERE event_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, eventId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting event " + eventId, e);
        } finally {
            DatabaseConnection.closeResources(null, pstmt, conn);
        }
        return false;
    }
    
    /**
     * Map ResultSet to CalendarEvent object
     */
    private CalendarEvent mapResultSetToEvent(ResultSet rs) throws SQLException {
        CalendarEvent event = new CalendarEvent();
        event.setEventId(rs.getInt("event_id"));
        event.setSemesterId(rs.getInt("semester_id"));
        event.setEventTitle(rs.getString("event_title"));
        event.setEventType(rs.getString("event_type"));
        event.setEventDate(rs.getDate("event_date"));
        event.setEndDate(rs.getDate("end_date"));
        event.setDescription(rs.getString("description"));
        event.setCreatedDate(rs.getTimestamp("created_date"));
        event.setSemesterName(rs.getString("semester_name"));
        return event;
    }
}
