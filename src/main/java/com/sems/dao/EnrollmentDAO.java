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

    private static final String CHECK_PREREQUISITE
            = "SELECT p.prerequisite_course_id, "
            + "       (SELECT e.grade FROM enrollments e "
            + "        WHERE e.student_id = ? AND e.course_id = p.prerequisite_course_id LIMIT 1) as student_grade "
            + "FROM prerequisites p "
            + "WHERE p.course_id = ?";

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
        enrollment.setGrade(rs.getString("grade"));

        return enrollment;
    }

    /**
     * fetches the actual Course objects for a specific student used for both
     * admin/student view
     */
    public List<com.sems.model.Course> getEnrolledCourseDetails(int studentId) {
        List<com.sems.model.Course> courses = new ArrayList<>();

        // 1. Added c.credits to the SELECT statement
        String sql = "SELECT c.course_id, c.course_code, c.course_name, c.credits, c.course_day, c.course_time "
                + "FROM courses c "
                + "JOIN enrollments e ON c.course_id = e.course_id "
                + "WHERE e.student_id = ? AND (e.status = 'Enrolled' OR e.status = 'enrolled')";

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
                course.setCourseId(rs.getInt("course_id"));
                course.setCourseCode(rs.getString("course_code"));
                course.setCourseName(rs.getString("course_name"));

                // 2. Added this line so credits show up in your table!
                course.setCredits(rs.getInt("credits"));

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

    /**
     * Verifies if a student has met the prerequisites for a course. Logic:
     * Checks if any prerequisite exists, then ensures student has grade A, B,
     * or C.
     */
    public boolean isPrerequisiteSatisfied(int studentId, int courseId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(CHECK_PREREQUISITE);
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            rs = pstmt.executeQuery();

            // Iterate through all prerequisites for this course
            while (rs.next()) {
                int prereqId = rs.getInt("prerequisite_course_id");
                String grade = rs.getString("student_grade");

                // If grade is null, student never took the prereq.
                // If grade is FAIL or N/A, they haven't met the standard.
                if (grade == null || "FAIL".equals(grade) || "N/A".equals(grade)) {
                    LOGGER.warning("Prerequisite Check Failed: Student " + studentId
                            + " lacks valid grade for Prereq Course " + prereqId);
                    return false;
                }
            }

            // If we reach here, either there were no prereqs or all were passed.
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking prerequisites", e);
            return false;
        } finally {
            DatabaseConnection.closeResources(rs, pstmt, conn);
        }
    }

    /**
     * grabs every row in enrollment w the specific student filters without
     * grade N/A matches grade w course credits adds points and divide total
     * credits
     */
    public double calculateStudentCGPA(int studentId) {
        String sql = "SELECT e.grade, c.credits FROM enrollments e "
                + "JOIN courses c ON e.course_id = c.course_id "
                + "WHERE e.student_id = ? AND e.grade NOT IN ('N/A', 'exempted')";

        double totalPoints = 0;
        int totalCredits = 0;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String grade = rs.getString("grade");
                int credits = rs.getInt("credits");
                double points = 0;

                switch (grade) {
                    case "A":
                        points = 4.0;
                        break;
                    case "B":
                        points = 3.5;
                        break; // Based on your request
                    case "C":
                        points = 2.5;
                        break;
                    case "FAIL":
                        points = 0.0;
                        break;
                    default:
                        continue; // Skip others
                }
                
                totalPoints += (points * credits);
                totalCredits += credits;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        //cgpa
        return (totalCredits == 0) ? 0.0 : (totalPoints / totalCredits);
    }

    /**
     * calls calc method to get CGPA and writes in the student table for student
     * and admin view
     *
     */
    public double updateAndGetStudentCGPA(int studentId) {
        double cgpa = calculateStudentCGPA(studentId); // Run the math method we just made

        // Now, SAVE it to the student table so it shows up in the Directory
        String updateSql = "UPDATE students SET gpa = ? WHERE student_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setDouble(1, cgpa);
            ps.setInt(2, studentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cgpa;
    }

    /**
     * report card generator translates numbers in SQL into obj
     *
     */
    public List<Enrollment> getFullTranscript(int studentId) {
        List<Enrollment> transcript = new ArrayList<>();
        String sql = "SELECT e.*, c.course_code, c.course_name, c.credits "
                + "FROM enrollments e "
                + "JOIN courses c ON e.course_id = c.course_id "
                + "WHERE e.student_id = ? AND e.grade NOT IN ('N/A')";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Enrollment e = new Enrollment();
                e.setEnrollmentId(rs.getInt("enrollment_id"));
                e.setGrade(rs.getString("grade"));
                e.setStatus(rs.getString("status"));
                e.setCourseCode(rs.getString("course_code"));
                e.setCourseName(rs.getString("course_name"));
                e.setCredits(rs.getInt("credits"));

                transcript.add(e);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching transcript for student ID: " + studentId, e);
        }
        return transcript;
    }
}
