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

    // SQL to fetch only students who have been approved by the admin
    // also to see timestamp
    private static final String SELECT_APPROVED_STUDENTS
            = "SELECT s.*, u.created_at FROM students s "
            + "JOIN users u ON s.user_id = u.user_id "
            + "WHERE u.status = 'ACTIVE' AND u.role = 'student' "
            + "ORDER BY s.last_name, s.first_name";

    // to get created_at time stamp - showing when registered
    private static final String SELECT_STUDENT_WITH_USER_DATA
            = "SELECT s.*, u.created_at FROM students s "
            + "JOIN users u ON s.user_id = u.user_id "
            + "WHERE s.user_id = ?";

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
    // Inside StudentDAO.java
    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET first_name = ?, last_name = ?, email = ?, phone = ?, address = ?, gpa = ?, dob = ? WHERE student_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getPhone());
            pstmt.setString(5, student.getAddress());
            pstmt.setDouble(6, student.getGpa());
            pstmt.setDate(7, student.getDob());
            pstmt.setInt(8, student.getStudentId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // getting student details using their User ID (useful for Dashboards)
    public Student getStudentByUserId(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_STUDENT_WITH_USER_DATA);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Student student = extractStudentFromResultSet(rs);
                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) {
                    student.setEnrollmentDate(new java.sql.Date(ts.getTime()));
                }

                return student;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching student by user ID", e);
        } finally {
            DatabaseConnection.closeResources(rs, pstmt, conn);
        }
        return null;
    }

    //showing student from user
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

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_APPROVED_STUDENTS); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Student s = extractStudentFromResultSet(rs);

                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) {
                    s.setEnrollmentDate(new java.sql.Date(ts.getTime()));
                }

                list.add(s);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching approved students", e);
        }
        return list;
    }

    // ADMIN DELETE STUDENT
    public boolean deleteStudent(int studentId, int userId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            System.out.println("DEBUG: Starting delete transaction for Student ID: " + studentId);

            // STEP 1: Fixed column name to 'enrolled_count' to match your SQL table
            String updateCap = "UPDATE courses SET enrolled_count = GREATEST(0, enrolled_count - 1) "
                    + "WHERE course_id IN (SELECT course_id FROM enrollments WHERE student_id = ?)";

            try (PreparedStatement ps = conn.prepareStatement(updateCap)) {
                ps.setInt(1, studentId);
                int rowsAffected = ps.executeUpdate();
                System.out.println("DEBUG: Capacity updated for " + rowsAffected + " courses.");
            }

            // STEP 2: Delete Enrollments
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM enrollments WHERE student_id = ?")) {
                ps.setInt(1, studentId);
                ps.executeUpdate();
                System.out.println("DEBUG: Enrollments cleared.");
            }

            // STEP 3: Delete Student Profile
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM students WHERE student_id = ?")) {
                ps.setInt(1, studentId);
                ps.executeUpdate();
                System.out.println("DEBUG: Student profile deleted.");
            }

            // STEP 4: Delete User account
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
                ps.setInt(1, userId);
                ps.executeUpdate();
                System.out.println("DEBUG: User account deleted.");
            }

            conn.commit(); // Finalize all changes
            System.out.println("DEBUG: Transaction committed successfully.");
            return true;

        } catch (SQLException e) {
            System.out.println("DEBUG: SQL ERROR: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // Undo everything if Step 1 (or any step) fails
                    System.out.println("DEBUG: Transaction rolled back.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //ADMINview: manual registration
    public boolean createStudentManually(Student student, String username, String password) {
        String userSql = "INSERT INTO users (username, password_hash, role, status) VALUES (?, ?, 'student', 'ACTIVE')";
        String studentSql = "INSERT INTO students (student_id, user_id, first_name, last_name, email, phone, address, dob) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            // 1. Insert into Users Table
            int generatedUserId = -1;
            try (PreparedStatement psUser = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, username);
                psUser.setString(2, password); // Note: In a real app, hash this!
                psUser.executeUpdate();

                try (ResultSet rs = psUser.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedUserId = rs.getInt(1);
                    }
                }
            }

            // 2. Insert into Students Table using the new User ID
            try (PreparedStatement psStudent = conn.prepareStatement(studentSql)) {
                psStudent.setInt(1, student.getStudentId()); // The manual ID from Admin
                psStudent.setInt(2, generatedUserId);
                psStudent.setString(3, student.getFirstName());
                psStudent.setString(4, student.getLastName());
                psStudent.setString(5, student.getEmail());
                psStudent.setString(6, student.getPhone());
                psStudent.setString(7, student.getAddress());
                psStudent.setDate(8, student.getDob());
                psStudent.executeUpdate();
            }

            conn.commit(); // Save everything
            return true;
        } catch (SQLException e) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // STUDENT VIEW: updating students own info
    public boolean updateStudentContactInfo(int userId, String email, String phone, String address) {
        String sql = "UPDATE students SET email = ?, phone = ?, address = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, phone);
            pstmt.setString(3, address);
            pstmt.setInt(4, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating student contact info", e);
            return false;
        }
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
