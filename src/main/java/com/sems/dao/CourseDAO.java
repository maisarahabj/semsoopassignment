package com.sems.dao;
import com.sems.model.Course;
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

public class CourseDAO {
    
    private static final Logger LOGGER = Logger.getLogger(CourseDAO.class.getName());
    
    // SQL Constants
    private static final String INSERT_COURSE = 
            "INSERT INTO courses (course_code, course_name, credits, capacity, enrolled_count) VALUES (?, ?, ?, ?, ?)";
    
    private static final String SELECT_ALL_COURSES = 
            "SELECT * FROM courses ORDER BY course_code";
    
    private static final String SELECT_COURSE_BY_ID = 
            "SELECT * FROM courses WHERE course_id = ?";
    
    private static final String UPDATE_ENROLLED_COUNT = 
            "UPDATE courses SET enrolled_count = enrolled_count + 1 WHERE course_id = ? AND enrolled_count < capacity";


     //Adding new courses to the system (Admin feature)

    public boolean createCourse(Course course) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(INSERT_COURSE);
            
            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            pstmt.setInt(3, course.getCredits());
            pstmt.setInt(4, course.getCapacity());
            pstmt.setInt(5, 0); // New courses start with 0 students
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating course", e);
        } finally {
            DatabaseConnection.closeResources(null, pstmt, conn);
        }
        return false;
    }


     //Retrieve all available courses

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(SELECT_ALL_COURSES);
            
            while (rs.next()) {
                courses.add(extractCourseFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all courses", e);
        } finally {
            DatabaseConnection.closeResources(rs, stmt, conn);
        }
        return courses;
    }

 
     //Increment the enrollment count when a student successfully registers

    public boolean incrementEnrolledCount(int courseId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(UPDATE_ENROLLED_COUNT);
            pstmt.setInt(1, courseId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating course enrollment count", e);
        } finally {
            DatabaseConnection.closeResources(null, pstmt, conn);
        }
        return false;
    }

    private Course extractCourseFromResultSet(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setCourseId(rs.getInt("course_id"));
        course.setCourseCode(rs.getString("course_code"));
        course.setCourseName(rs.getString("course_name"));
        course.setCredits(rs.getInt("credits"));
        course.setCapacity(rs.getInt("capacity"));
        course.setEnrolledCount(rs.getInt("enrolled_count"));
        return course;
    }
}