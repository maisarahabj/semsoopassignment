/**
 * Data Access Object for Enrollment operations.
 * Handles adding, listing, and dropping course enrollments.
 * * @author maisarahabjalil
 */
package com.sems.dao;

import com.sems.model.Enrollment;
import com.sems.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnrollmentDAO {
    
    private static final Logger LOGGER = Logger.getLogger(EnrollmentDAO.class.getName());
    

    private static final String INSERT_ENROLLMENT = 
            "INSERT INTO enrollments (student_id, course_id, status) VALUES (?, ?, ?)";
    
    private static final String SELECT_BY_STUDENT = 
            "SELECT * FROM enrollments WHERE student_id = ?";

    private static final String DELETE_BY_STUDENT_COURSE = 
            "DELETE FROM enrollments WHERE student_id = ? AND course_id = ?";

    /**
     * Enrolls a student in a specific course
     */
    public boolean enrollStudent(int studentId, int courseId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(INSERT_ENROLLMENT);
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.setString(3, "Enrolled"); // Default status
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Student " + studentId + " successfully enrolled in course " + courseId);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during enrollment process", e);
        } finally {
            DatabaseConnection.closeResources(null, pstmt, conn);
        }
        return false;
    }

    /**
     * Remove enrollment record - Drop course
     */
    public boolean dropCourse(int studentId, int courseId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(DELETE_BY_STUDENT_COURSE);
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Student " + studentId + " successfully dropped course " + courseId);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during drop course process", e);
        } finally {
            DatabaseConnection.closeResources(null, pstmt, conn);
        }
        return false;
    }

    /**
     * Gets all enrollments for a specific student.
     */
    public List<Enrollment> getStudentEnrollments(int studentId) {
        List<Enrollment> enrollments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_BY_STUDENT);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                enrollments.add(extractEnrollmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching student enrollments", e);
        } finally {
            DatabaseConnection.closeResources(rs, pstmt, conn);
        }
        return enrollments;
    }

    /**
     * Helper method to map a ResultSet row to an Enrollment object.
     */
    private Enrollment extractEnrollmentFromResultSet(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(rs.getInt("enrollment_id"));
        enrollment.setStudentId(rs.getInt("student_id"));
        enrollment.setCourseId(rs.getInt("course_id"));
        enrollment.setEnrollmentDate(rs.getTimestamp("enrollment_date"));
        
        enrollment.setStatus(rs.getString("status"));
        return enrollment;
    }
}