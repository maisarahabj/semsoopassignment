package com.sems.dao;

import com.sems.model.Semester;
import com.sems.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Semester operations
 * @author SEMS Team
 */
public class SemesterDAO {
    
    private static final Logger LOGGER = Logger.getLogger(SemesterDAO.class.getName());
    
    private static final String INSERT_SEMESTER = 
        "INSERT INTO semesters (semester_name, start_date, end_date, status) VALUES (?, ?, ?, ?)";
    
    private static final String SELECT_ALL_SEMESTERS = 
        "SELECT * FROM semesters ORDER BY created_date DESC";
    
    private static final String SELECT_SEMESTER_BY_ID = 
        "SELECT * FROM semesters WHERE semester_id = ?";
    
    private static final String SELECT_ACTIVE_SEMESTER = 
        "SELECT * FROM semesters WHERE status = 'ACTIVE' LIMIT 1";
    
    private static final String UPDATE_SEMESTER_STATUS = 
        "UPDATE semesters SET status = ? WHERE semester_id = ?";
    
    private static final String UPDATE_SEMESTER = 
        "UPDATE semesters SET semester_name = ?, start_date = ?, end_date = ? WHERE semester_id = ?";
    
    /**
     * Creates a new semester
     */
    public boolean createSemester(Semester semester) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(INSERT_SEMESTER, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setString(1, semester.getSemesterName());
            pstmt.setDate(2, semester.getStartDate());
            pstmt.setDate(3, semester.getEndDate());
            pstmt.setString(4, semester.getStatus());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    semester.setSemesterId(rs.getInt(1));
                }
                LOGGER.info("Semester created successfully: " + semester.getSemesterName());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating semester", e);
        } finally {
            DatabaseConnection.closeResources(null, pstmt, conn);
        }
        return false;
    }
    
    /**
     * Retrieves all semesters
     */
    public List<Semester> getAllSemesters() {
        List<Semester> semesters = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_ALL_SEMESTERS);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                semesters.add(mapResultSetToSemester(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all semesters", e);
        } finally {
            DatabaseConnection.closeResources(rs, pstmt, conn);
        }
        return semesters;
    }
    
    /**
     * Retrieves a semester by ID
     */
    public Semester getSemesterById(int semesterId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_SEMESTER_BY_ID);
            pstmt.setInt(1, semesterId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToSemester(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving semester by ID: " + semesterId, e);
        } finally {
            DatabaseConnection.closeResources(rs, pstmt, conn);
        }
        return null;
    }
    
    /**
     * Retrieves the currently active semester
     */
    public Semester getActiveSemester() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_ACTIVE_SEMESTER);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToSemester(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving active semester", e);
        } finally {
            DatabaseConnection.closeResources(rs, pstmt, conn);
        }
        return null;
    }
    
    /**
     * Ends a semester by updating its status to ENDED
     */
    public boolean endSemester(int semesterId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(UPDATE_SEMESTER_STATUS);
            pstmt.setString(1, "ENDED");
            pstmt.setInt(2, semesterId);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Semester " + semesterId + " ended successfully");
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error ending semester: " + semesterId, e);
        } finally {
            DatabaseConnection.closeResources(null, pstmt, conn);
        }
        return false;
    }
    
    /**
     * Updates semester information
     */
    public boolean updateSemester(Semester semester) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(UPDATE_SEMESTER);
            
            pstmt.setString(1, semester.getSemesterName());
            pstmt.setDate(2, semester.getStartDate());
            pstmt.setDate(3, semester.getEndDate());
            pstmt.setInt(4, semester.getSemesterId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Semester updated successfully: " + semester.getSemesterId());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating semester", e);
        } finally {
            DatabaseConnection.closeResources(null, pstmt, conn);
        }
        return false;
    }
    
    /**
     * Maps ResultSet to Semester object
     */
    private Semester mapResultSetToSemester(ResultSet rs) throws SQLException {
        Semester semester = new Semester();
        semester.setSemesterId(rs.getInt("semester_id"));
        semester.setSemesterName(rs.getString("semester_name"));
        semester.setStartDate(rs.getDate("start_date"));
        semester.setEndDate(rs.getDate("end_date"));
        semester.setStatus(rs.getString("status"));
        semester.setCreatedDate(rs.getTimestamp("created_date"));
        return semester;
    }
}
