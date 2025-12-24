/**
 * Data Access Object for Enrollment operations.
 * Handles adding, listing, and dropping course enrollments.
 *
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

    private static final String INSERT_ENROLLMENT
            = "INSERT INTO enrollments (student_id, course_id, status) VALUES (?, ?, ?)";

    private static final String SELECT_BY_STUDENT
            = "SELECT * FROM enrollments WHERE student_id = ?";

    private static final String DELETE_BY_STUDENT_COURSE
            = "DELETE FROM enrollments WHERE student_id = ? AND course_id = ?";

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

    /**
     * Fetches the actual Course objects for a specific student. Useful for
     * displaying the course list on the dashboard.
     */
    public List<com.sems.model.Course> getEnrolledCourseDetails(int studentId) {
        List<com.sems.model.Course> courses = new ArrayList<>();
        // 1. SQL Query (Make sure all columns you want to display are listed here)
        String sql = "SELECT c.course_id, c.course_code, c.course_name, c.course_day, c.course_time "
                + "FROM courses c "
                + "JOIN enrollments e ON c.course_id = e.course_id "
                + "WHERE e.student_id = ? AND e.status = 'enrolled'";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                com.sems.model.Course course = new com.sems.model.Course();
                // 2. Mapping ResultSet to the Course Object
                course.setCourseId(rs.getInt("course_id"));
                course.setCourseCode(rs.getString("course_code"));
                course.setCourseName(rs.getString("course_name"));

                // CRITICAL: These two lines fix the "TBA" issue!
                course.setCourseDay(rs.getString("course_day"));
                course.setCourseTime(rs.getString("course_time"));

                courses.add(course);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching enrolled course details", e);
        } finally {
            DatabaseConnection.closeResources(rs, pstmt, conn);
        }
        return courses;
    }

    /**
     * -------ADMIN VIEW: Dropping / adding a student manualllllyyy ------ Drops
     * a student from a course and opens up a seat in the courses table. This is
     * transactional: both must succeed or both will fail.
     */
    public boolean adminDropStudentFromCourse(int studentId, int courseId) {
        String deleteSql = "DELETE FROM enrollments WHERE student_id = ? AND course_id = ?";
        String updateCourseSql = "UPDATE courses SET enrolled_count = enrolled_count - 1 "
                + "WHERE course_id = ? AND enrolled_count > 0";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            // 1. Remove the enrollment
            try (PreparedStatement ps1 = conn.prepareStatement(deleteSql)) {
                ps1.setInt(1, studentId);
                ps1.setInt(2, courseId);
                ps1.executeUpdate();
            }

            // 2. Decrement the count in courses table
            try (PreparedStatement ps2 = conn.prepareStatement(updateCourseSql)) {
                ps2.setInt(1, courseId);
                ps2.executeUpdate();
            }

            conn.commit(); // Success!
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            LOGGER.log(Level.SEVERE, "Error dropping student " + studentId + " from course " + courseId, e);
            return false;
        } finally {
            DatabaseConnection.closeResources(null, null, conn);
        }

    }

    /**
     * Admin manually enrolls a student and increments course count.
     */
    public boolean adminEnrollStudentInCourse(int studentId, int courseId) {
        String insertSql = "INSERT INTO enrollments (student_id, course_id, status) VALUES (?, ?, 'Enrolled')";
        String updateCourseSql = "UPDATE courses SET enrolled_count = enrolled_count + 1 "
                + "WHERE course_id = ? AND enrolled_count < capacity";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(insertSql)) {
                ps1.setInt(1, studentId);
                ps1.setInt(2, courseId);
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = conn.prepareStatement(updateCourseSql)) {
                ps2.setInt(1, courseId);
                int rowsAffected = ps2.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Course is full!");
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            return false;
        } finally {
            DatabaseConnection.closeResources(null, null, conn);
        }
    }
}
