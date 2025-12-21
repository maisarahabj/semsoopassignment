package com.sems.dao;
import com.sems.model.Enrollment;
import com.sems.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author maisarahabjalil
 */

public class EnrollmentDAO {
    
    private static final Logger LOGGER = Logger.getLogger(EnrollmentDAO.class.getName());
    
    // SQL Constants
    private static final String INSERT_ENROLLMENT = 
            "INSERT INTO enrollments (student_id, course_id, status) VALUES (?, ?, ?)";
    
    private static final String SELECT_BY_STUDENT = 
            "SELECT * FROM enrollments WHERE student_id = ?";
    
    private static final String DELETE_ENROLLMENT = 
            "DELETE FROM enrollments WHERE enrollment_id = ?";

    //Enrolling in a course
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


     //Getting  all enrollments for a specific student

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