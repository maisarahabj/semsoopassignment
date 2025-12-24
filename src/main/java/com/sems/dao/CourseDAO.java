/**
 *
 * @author maisarahabjalil
 */
package com.sems.dao;

import com.sems.model.Course;
import com.sems.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Course operations. Handles course creation, retrieval,
 * and seat count management.
 *
 * @author maisarahabjalil
 */
public class CourseDAO {

    private static final Logger LOGGER = Logger.getLogger(CourseDAO.class.getName());

    // SQL Constants
    private static final String INSERT_COURSE
            = "INSERT INTO courses (course_code, course_name, credits, capacity, enrolled_count, course_day, course_time) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ALL_COURSES
            = "SELECT * FROM courses ORDER BY course_code";

    private static final String SELECT_COURSE_BY_ID
            = "SELECT * FROM courses WHERE course_id = ?";

    private static final String SELECT_ID_BY_CODE
            = "SELECT course_id FROM courses WHERE course_code = ?";

    private static final String UPDATE_INCREMENT_ENROLLED
            = "UPDATE courses SET enrolled_count = enrolled_count + 1 WHERE course_id = ? AND enrolled_count < capacity";

    private static final String UPDATE_DECREMENT_ENROLLED
            = "UPDATE courses SET enrolled_count = enrolled_count - 1 WHERE course_id = ? AND enrolled_count > 0";

    /**
     * Helper method to find the course_id based on a course_code.
     */
    public int getCourseIdByCode(String courseCode) {
        int id = -1;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_ID_BY_CODE);
            pstmt.setString(1, courseCode);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                id = rs.getInt("course_id");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding course ID by code: " + courseCode, e);
        } finally {
            DatabaseConnection.closeResources(rs, pstmt, conn);
        }
        return id;
    }

    public boolean createCourse(Course course) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(INSERT_COURSE);

            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());

            // Mapping the new integer fields
            pstmt.setInt(3, course.getCredits());
            pstmt.setInt(4, course.getCapacity());

            pstmt.setInt(5, 0); // New courses start with 0 enrolled
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

    public List<Course> getCoursesByStudentId(int studentId) {
        List<Course> enrolledCourses = new ArrayList<>();
        String sql = "SELECT c.* FROM courses c "
                + "JOIN enrollments e ON c.course_id = e.course_id "
                + "WHERE e.student_id = ? AND e.status = 'enrolled'";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                enrolledCourses.add(extractCourseFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching student courses", e);
        }
        return enrolledCourses;
    }

    public boolean incrementEnrolledCount(int courseId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(UPDATE_INCREMENT_ENROLLED);
            pstmt.setInt(1, courseId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error incrementing count", e);
        } finally {
            DatabaseConnection.closeResources(null, pstmt, conn);
        }
        return false;
    }

    public boolean decrementEnrolledCount(int courseId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(UPDATE_DECREMENT_ENROLLED);
            pstmt.setInt(1, courseId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error decrementing count", e);
        } finally {
            DatabaseConnection.closeResources(null, pstmt, conn);
        }
        return false;
    }

    // students
    public List<Course> getCoursesByDay(int studentId, String day) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.* FROM courses c "
                + "JOIN enrollments e ON c.course_id = e.course_id "
                + "WHERE e.student_id = ? AND c.course_day = ? AND e.status = 'enrolled'";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setString(2, day);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractCourseFromResultSet(rs));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching courses by day", e);
        }
        return list;
    }

    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting course", e);
        } finally {
            DatabaseConnection.closeResources(null, pstmt, conn);
        }
        return false;
    }

    //ADMIMN dash todays course

    public List<Course> getTodayCourses(String dayName) {
        List<Course> list = new ArrayList<>();
        // Use the parameter in your SQL query
        String sql = "SELECT * FROM courses WHERE course_day = ? ORDER BY course_time ASC";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            // This binds "Wednesday" (or whatever currentDay is) to the '?'
            ps.setString(1, dayName);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Course c = new Course();
                    c.setCourseId(rs.getInt("course_id"));
                    c.setCourseCode(rs.getString("course_code"));
                    c.setCourseName(rs.getString("course_name"));
                    c.setCapacity(rs.getInt("capacity"));
                    c.setEnrolledCount(rs.getInt("enrolled_count")); // Matches your DB column
                    c.setCourseDay(rs.getString("course_day"));
                    c.setCourseTime(rs.getString("course_time"));
                    list.add(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        course.setCourseDay(rs.getString("course_day"));
        course.setCourseTime(rs.getString("course_time"));
        return course;
    }
}
