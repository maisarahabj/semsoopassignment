package com.sems.dao;

import com.sems.model.Student;
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
public class StudentDAO {

    private static final Logger LOGGER = Logger.getLogger(StudentDAO.class.getName());

    // SQL Constants matching your specific Students Table
    private static final String INSERT_STUDENT
            = "INSERT INTO students (user_id, first_name, last_name, email, phone, address, gpa, dob) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ALL_STUDENTS
            = "SELECT * FROM students ORDER BY last_name, first_name";

    private static final String SELECT_STUDENT_BY_USER_ID
            = "SELECT * FROM students WHERE user_id = ?";

    private static final String UPDATE_STUDENT
            = "UPDATE students SET first_name = ?, last_name = ?, email = ?, phone = ?, address = ?, gpa = ?, dob = ? WHERE student_id = ?";

    // check if username or email is already taken in the system
    public boolean isUserExists(String username, String email) {
        String sql = "SELECT (SELECT count(*) FROM users WHERE username = ?) + "
                + "(SELECT count(*) FROM students WHERE email = ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, email);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                // If the sum of counts from both tables is > 0, the user/email exists
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if user exists", e);
        } finally {
            DatabaseConnection.closeResources(rs, pstmt, conn);
        }
        return false;
    }

    //new student profile linked to a user account
    public boolean createStudent(Student student) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(INSERT_STUDENT);

            pstmt.setInt(1, student.getUserId());
            pstmt.setString(2, student.getFirstName());
            pstmt.setString(3, student.getLastName());
            pstmt.setString(4, student.getEmail());
            pstmt.setString(5, student.getPhone());
            pstmt.setString(6, student.getAddress());
            pstmt.setDouble(7, student.getGpa());
            pstmt.setDate(8, student.getDob());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating student profile", e);
        } finally {
            DatabaseConnection.closeResources(null, pstmt, conn);
        }
        return false;
    }

    //updating existing student
    public boolean updateStudent(Student student) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(UPDATE_STUDENT);

            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getPhone());
            pstmt.setString(5, student.getAddress());
            pstmt.setDouble(6, student.getGpa());
            pstmt.setDate(7, student.getDob());
            pstmt.setInt(8, student.getStudentId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("Student ID " + student.getStudentId() + " updated successfully.");
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating student profile", e);
        } finally {
            DatabaseConnection.closeResources(null, pstmt, conn);
        }
        return false;
    }

    // getting student details using their User ID (useful for Dashboards)
    public Student getStudentByUserId(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_STUDENT_BY_USER_ID);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractStudentFromResultSet(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching student by user ID", e);
        } finally {
            DatabaseConnection.closeResources(rs, pstmt, conn);
        }
        return null;
    }

    public int getStudentIdByUserId(int userId) {
        int studentId = -1; // Default to -1 (not found)
        String sql = "SELECT student_id FROM students WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                studentId = rs.getInt("student_id");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching student ID", e);
        } finally {
            DatabaseConnection.closeResources(rs, pstmt, conn);
        }
        return studentId;
    }

    //fetching everything for ADMIN only
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_ALL_STUDENTS); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractStudentFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all students", e);
        }
        return list;
    }

    // map SQL Result to Student Model
    private Student extractStudentFromResultSet(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        student.setUserId(rs.getInt("user_id"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setAddress(rs.getString("address"));
        student.setGpa(rs.getDouble("gpa"));
        student.setDob(rs.getDate("dob"));
        return student;
    }
}
