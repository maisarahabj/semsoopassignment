/**
 *
 * @author maisarahabjalil
 *
 * for course table in workbench
 * extracts dbase rows n turn into Java objs for student n admin dash
 */
package com.sems.dao;

import com.sems.model.Course;
import com.sems.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CourseDAO {

    private static final Logger LOGGER = Logger.getLogger(CourseDAO.class.getName());

    // SQL Constants
    private static final String INSERT_COURSE
            = "INSERT INTO courses (course_code, course_name, credits, capacity, enrolled_count, course_day, course_time) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String BASE_COURSE_QUERY
            = "SELECT c.*, "
            + "       p_table.prereq_name as prerequisite_name, "
            + "       CASE WHEN p_table.course_id IS NOT NULL THEN 1 ELSE 0 END as has_prereq "
            + "FROM courses c "
            + "LEFT JOIN ("
            + "    SELECT p.course_id, c2.course_name as prereq_name "
            + "    FROM prerequisites p "
            + "    JOIN courses c2 ON p.prerequisite_course_id = c2.course_id"
            + ") p_table ON c.course_id = p_table.course_id ";

    private static final String SELECT_ALL_COURSES
            = BASE_COURSE_QUERY + " ORDER BY c.course_code";

    private static final String SELECT_COURSE_BY_ID
            = BASE_COURSE_QUERY + " WHERE c.course_id = ?";

    private static final String SELECT_ID_BY_CODE
            = "SELECT course_id FROM courses WHERE course_code = ?";

    private static final String UPDATE_INCREMENT_ENROLLED
            = "UPDATE courses SET enrolled_count = enrolled_count + 1 WHERE course_id = ? AND enrolled_count < capacity";

    private static final String UPDATE_DECREMENT_ENROLLED
            = "UPDATE courses SET enrolled_count = enrolled_count - 1 WHERE course_id = ? AND enrolled_count > 0";

    //finds PK of course table using course code name
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

    //ADMIN VIEW: creating course 
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
            pstmt.setInt(5, 0);
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

    //gets all courses including pre-req
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

    // STU VIEW: dashboard schedule - getting courses enrolled by specific student
    public List<Course> getCoursesByStudentId(int studentId) {
        List<Course> enrolledCourses = new ArrayList<>();

        String sql = "SELECT c.*, p_table.prereq_name as prerequisite_name, "
                + "CASE WHEN p_table.course_id IS NOT NULL THEN 1 ELSE 0 END as has_prereq "
                + "FROM courses c "
                + "LEFT JOIN (SELECT p.course_id, c2.course_name as prereq_name "
                + "           FROM prerequisites p "
                + "           JOIN courses c2 ON p.prerequisite_course_id = c2.course_id) p_table "
                + "ON c.course_id = p_table.course_id "
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

    // BOTH VIEW: increase/decrease enrollment count if a student is enrolled
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

    // STU VIEW: filters course by day for student timetable page
    public List<Course> getCoursesByDay(int studentId, String day) {
        List<Course> list = new ArrayList<>();

        String sql = "SELECT c.*, p_table.prereq_name as prerequisite_name, "
                + "CASE WHEN p_table.course_id IS NOT NULL THEN 1 ELSE 0 END as has_prereq "
                + "FROM courses c "
                + "LEFT JOIN (SELECT p.course_id, c2.course_name as prereq_name "
                + "           FROM prerequisites p "
                + "           JOIN courses c2 ON p.prerequisite_course_id = c2.course_id) p_table "
                + "ON c.course_id = p_table.course_id "
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

    //ADMIN: delete entire course completely
    public boolean deleteCourse(int courseId) {

        String deleteEnrollmentsSql = "DELETE FROM enrollments WHERE course_id = ?";
        String deleteCourseSql = "DELETE FROM courses WHERE course_id = ?";

        Connection conn = null;
        PreparedStatement pstmtEnroll = null;
        PreparedStatement pstmtCourse = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // remove up enrollments row
            pstmtEnroll = conn.prepareStatement(deleteEnrollmentsSql);
            pstmtEnroll.setInt(1, courseId);
            pstmtEnroll.executeUpdate();

            // delete the course row
            pstmtCourse = conn.prepareStatement(deleteCourseSql);
            pstmtCourse.setInt(1, courseId);
            boolean success = pstmtCourse.executeUpdate() > 0;

            conn.commit();
            return success;
        } catch (SQLException e) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            LOGGER.log(Level.SEVERE, "Error deleting course and its enrollments", e);
        } finally {
            DatabaseConnection.closeResources(null, pstmtEnroll, null);
            DatabaseConnection.closeResources(null, pstmtCourse, conn);
        }
        return false;
    }

    //ADMIMN dash todays course
    public List<Course> getTodayCourses(String dayName) {
        List<Course> list = new ArrayList<>();

        String sql = "SELECT * FROM courses WHERE course_day = ? ORDER BY course_time ASC";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

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

    //ADMIN VIEW: view student card
    public List<Course> getEnrolledCoursesByStudentId(int studentId) {
        List<Course> list = new ArrayList<>();

        String sql = "SELECT c.* FROM courses c "
                + "JOIN enrollments e ON c.course_id = e.course_id "
                + "WHERE e.student_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Course c = new Course();
                c.setCourseId(rs.getInt("course_id"));
                c.setCourseCode(rs.getString("course_code"));
                c.setCourseName(rs.getString("course_name"));
                c.setCourseDay(rs.getString("course_day"));
                c.setCourseTime(rs.getString("course_time"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * ADMIN: creating course with pre-req
     * creates a course and optionally links a prerequisite in the prerequisites
     * table. Uses a Transaction (commit/rollback) to ensure both succeed or
     * both fail.
     */
    public boolean createCourseWithPrereq(Course course, int prerequisiteId) {
        Connection conn = null;
        PreparedStatement pstmtCourse = null;
        PreparedStatement pstmtPrereq = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            // 1. Insert the Course - RETURN_GENERATED_KEYS is needed to get the new Course ID
            String sqlCourse = "INSERT INTO courses (course_code, course_name, credits, capacity, enrolled_count, course_day, course_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmtCourse = conn.prepareStatement(sqlCourse, Statement.RETURN_GENERATED_KEYS);
            pstmtCourse.setString(1, course.getCourseCode());
            pstmtCourse.setString(2, course.getCourseName());
            pstmtCourse.setInt(3, course.getCredits());
            pstmtCourse.setInt(4, course.getCapacity());
            pstmtCourse.setInt(5, 0);
            pstmtCourse.setString(6, course.getCourseDay());
            pstmtCourse.setString(7, course.getCourseTime());

            int affectedRows = pstmtCourse.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating course failed.");
            }

            // 2. Get the newly created Course ID
            int newCourseId = 0;
            rs = pstmtCourse.getGeneratedKeys();
            if (rs.next()) {
                newCourseId = rs.getInt(1);
            }

            // 3. If a prerequisite was selected, insert it into the prerequisites table
            if (prerequisiteId > 0 && newCourseId > 0) {
                String sqlPrereq = "INSERT INTO prerequisites (course_id, prerequisite_course_id) VALUES (?, ?)";
                pstmtPrereq = conn.prepareStatement(sqlPrereq);
                pstmtPrereq.setInt(1, newCourseId);
                pstmtPrereq.setInt(2, prerequisiteId);
                pstmtPrereq.executeUpdate();
            }

            conn.commit(); // Save everything
            return true;

        } catch (SQLException e) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            LOGGER.log(Level.SEVERE, "Error creating course with prereq", e);
            return false;
        } finally {
            DatabaseConnection.closeResources(rs, pstmtCourse, null);
            DatabaseConnection.closeResources(null, pstmtPrereq, conn);
        }
    }

    //mapping template - javas telling SQL which cell to look at
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

        try {
            course.setHasPrereq(rs.getInt("has_prereq") == 1);
            course.setPrerequisiteName(rs.getString("prerequisite_name"));
        } catch (SQLException e) {

            course.setHasPrereq(false);
            course.setPrerequisiteName(null);
        }

        return course;
    }
}
