package com.sems.dao;

import com.sems.model.Student;
import com.sems.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentDAO {

    private static final Logger LOGGER = Logger.getLogger(StudentDAO.class.getName());

    // SQL Constants
    private static final String INSERT_STUDENT = "INSERT INTO students (user_id, first_name, last_name, email, phone, address, gpa, dob) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_STUDENT_WITH_USER_DATA = "SELECT s.*, u.created_at FROM students s JOIN users u ON s.user_id = u.user_id WHERE s.user_id = ?";
    private static final String SELECT_APPROVED_STUDENTS = "SELECT s.*, u.created_at FROM students s JOIN users u ON s.user_id = u.user_id WHERE u.status = 'ACTIVE' AND u.role = 'student' ORDER BY s.last_name, s.first_name";

    // --- 1. CORE STUDENT MANAGEMENT (Restoring missing methods) ---
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

    public int getStudentIdByUserId(int userId) {
        String sql = "SELECT student_id FROM students WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("student_id");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching student ID", e);
        }
        return -1;
    }

    public Student getStudentById(int studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractStudentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching student by ID", e);
        }
        return null;
    }

    public boolean updateStudentContactInfo(int userId, String email, String phone, String address) {
        String sql = "UPDATE students SET email = ?, phone = ?, address = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, phone);
            pstmt.setString(3, address);
            pstmt.setInt(4, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deleteStudent(int studentId, int userId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            // Update Course Capacities
            String updateCap = "UPDATE courses SET enrolled_count = GREATEST(0, enrolled_count - 1) WHERE course_id IN (SELECT course_id FROM enrollments WHERE student_id = ?)";
            try (PreparedStatement ps = conn.prepareStatement(updateCap)) {
                ps.setInt(1, studentId);
                ps.executeUpdate();
            }
            // Delete Enrollments
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM enrollments WHERE student_id = ?")) {
                ps.setInt(1, studentId);
                ps.executeUpdate();
            }
            // Delete Student
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM students WHERE student_id = ?")) {
                ps.setInt(1, studentId);
                ps.executeUpdate();
            }
            // Delete User
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
                ps.setInt(1, userId);
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
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

    public List<Student> getStudentsByCourseId(int courseId) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.student_id, s.first_name, s.last_name FROM students s JOIN enrollments e ON s.student_id = e.student_id WHERE e.course_id = ? AND e.status = 'Enrolled'";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Student s = new Student();
                    s.setStudentId(rs.getInt("student_id"));
                    s.setFirstName(rs.getString("first_name"));
                    s.setLastName(rs.getString("last_name"));
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean createStudentManually(Student student, String username, String password) {
        String userSql = "INSERT INTO users (username, password_hash, role, status) VALUES (?, ?, 'student', 'ACTIVE')";
        String studentSql = "INSERT INTO students (student_id, user_id, first_name, last_name, email, phone, address, dob) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            int generatedUserId = -1;
            try (PreparedStatement psUser = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, username);
                psUser.setString(2, password);
                psUser.executeUpdate();
                try (ResultSet rs = psUser.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedUserId = rs.getInt(1);
                    }
                }
            }
            try (PreparedStatement psStudent = conn.prepareStatement(studentSql)) {
                psStudent.setInt(1, student.getStudentId());
                psStudent.setInt(2, generatedUserId);
                psStudent.setString(3, student.getFirstName());
                psStudent.setString(4, student.getLastName());
                psStudent.setString(5, student.getEmail());
                psStudent.setString(6, student.getPhone());
                psStudent.setString(7, student.getAddress());
                psStudent.setDate(8, student.getDob());
                psStudent.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            return false;
        }
    }

    // --- 2. UNIVERSAL IMAGE METHODS (From our previous step) ---
    public byte[] getProfilePhoto(int userId) {
        String sql = "SELECT profile_pic FROM students WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Blob blob = rs.getBlob("profile_pic");
                    return (blob != null) ? blob.getBytes(1, (int) blob.length()) : null;
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    public boolean updateProfilePhoto(int userId, java.io.InputStream photoStream) {
        String sql = "UPDATE students SET profile_pic = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBlob(1, photoStream);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deleteProfilePhoto(int userId) {
        String sql = "UPDATE students SET profile_pic = NULL WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // --- 3. EXISTING HELPERS ---
    public boolean isUserExists(String username, String email) {
        String sql = "SELECT (SELECT count(*) FROM users WHERE username = ?) + (SELECT count(*) FROM students WHERE email = ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    public Student getStudentByUserId(int userId) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SELECT_STUDENT_WITH_USER_DATA)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Student s = extractStudentFromResultSet(rs);
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) {
                        s.setEnrollmentDate(new java.sql.Date(ts.getTime()));
                    }
                    return s;
                }
            }
        } catch (SQLException e) {
        }
        return null;
    }

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
        }
        return list;
    }

    public boolean hasProfilePhoto(int studentId) {
        String sql = "SELECT 1 FROM students WHERE student_id = ? AND profile_pic IS NOT NULL";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean hasProfilePhotoByUserId(int userId) {
        String sql = "SELECT 1 FROM students WHERE user_id = ? AND profile_pic IS NOT NULL";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

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
