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
    // Updated INSERT_COURSE to include course_day and course_time
    private static final String INSERT_COURSE = 
            "INSERT INTO courses (course_code, course_name, credits, capacity, enrolled_count, course_day, course_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
    
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
            // Added parameters for schedule
            pstmt.setString(6, course.getCourseDay());
            pstmt.setString(7, course.getCourseTime());
            
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
    
    // Retrieve courses for a specific student via Enrollment table
    public List<Course> getCoursesByStudentId(int studentId) {
    List<Course> enrolledCourses = new ArrayList<>();
    
    String sql = "SELECT c.* FROM courses c " +
                 "JOIN enrollments e ON c.course_id = e.course_id " +
                 "WHERE e.student_id = ? AND e.status = 'enrolled'";
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, studentId);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            // Reusing your existing helper method!
            enrolledCourses.add(extractCourseFromResultSet(rs));
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error fetching student courses", e);
    }
    return enrolledCourses;
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
    
    // UPDATED Course by day
    public List<Course> getCoursesByDay(int studentId, String day) {
    List<Course> list = new ArrayList<>();
    String sql = "SELECT c.* FROM courses c " +
                 "JOIN enrollments e ON c.course_id = e.course_id " +
                 "WHERE e.student_id = ? AND c.course_day = ? AND e.status = 'enrolled'";
    try (Connection conn = com.sems.util.DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, studentId);
        ps.setString(2, day);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(extractCourseFromResultSet(rs));
        }
    } catch (Exception e) { e.printStackTrace(); }
    return list;
}
    
    private Course extractCourseFromResultSet(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setCourseId(rs.getInt("course_id"));
        course.setCourseCode(rs.getString("course_code"));
        course.setCourseName(rs.getString("course_name"));
        course.setCredits(rs.getInt("credits"));
        course.setCapacity(rs.getInt("capacity"));
        course.setEnrolledCount(rs.getInt("enrolled_count"));
        // Added mapping for new columns
        course.setCourseDay(rs.getString("course_day"));
        course.setCourseTime(rs.getString("course_time"));
        return course;
    }
}