package com.sems.util;

import com.sems.dao.*;
import com.sems.model.*;
import java.sql.*;
import java.util.Scanner;

public class TestConnection {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        // Initialize DAOs
        StudentDAO studentDAO = new StudentDAO();
        EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
        CourseDAO courseDAO = new CourseDAO();
        
        // Database credentials
        String url = "jdbc:mysql://localhost:3306/sems_db?useSSL=false&allowPublicKeyRetrieval=true";
        String dbUser = "root";
        String dbPass = "Rockie.69";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, dbUser, dbPass);
            
            System.out.println("=== STUDENT ENROLLMENT LIFECYCLE TEST ===");
            System.out.print("Student Username: ");
            String user = sc.nextLine();
            System.out.print("Student Password: ");
            String pass = sc.nextLine();

            // 1. LOGIN
            String authSql = "SELECT * FROM users WHERE username=? AND password_hash=? AND role='student'";
            PreparedStatement pstmt = conn.prepareStatement(authSql);
            pstmt.setString(1, user);
            pstmt.setString(2, pass);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                Student s = studentDAO.getStudentByUserId(userId);
                System.out.println("\n[SUCCESS] Logged in as: " + s.getFullName());

                // 2. INPUT COURSE CODE
                System.out.print("\nEnter Course CODE to test (e.g., CS01): ");
                String inputCode = sc.nextLine();
                int resolvedId = courseDAO.getCourseIdByCode(inputCode);

                if (resolvedId != -1) {
                    System.out.println("--- PHASE 1: ENROLLMENT ---");
                    int countBeforeAdd = getCount(conn, resolvedId);
                    
                    if (enrollmentDAO.enrollStudent(s.getStudentId(), resolvedId)) {
                        courseDAO.incrementEnrolledCount(resolvedId);
                        int countAfterAdd = getCount(conn, resolvedId);
                        System.out.println("[OK] Enrolled. Count changed: " + countBeforeAdd + " -> " + countAfterAdd);

                        // 3. IMMEDIATELY DROP
                        System.out.println("\n--- PHASE 2: DROPPING ---");
                        System.out.println("Press Enter to drop the subject you just added...");
                        sc.nextLine();

                        if (enrollmentDAO.dropCourse(s.getStudentId(), resolvedId)) {
                            courseDAO.decrementEnrolledCount(resolvedId);
                            int countAfterDrop = getCount(conn, resolvedId);
                            System.out.println("[OK] Dropped. Count changed: " + countAfterAdd + " -> " + countAfterDrop);
                            
                            if(countBeforeAdd == countAfterDrop) {
                                System.out.println("\n[FINAL VERDICT] Transaction logic is 100% accurate!");
                            }
                        }
                    } else {
                        System.out.println("[!] Enrollment failed. You might already be in this class.");
                    }
                } else {
                    System.out.println("[ERROR] Course code not found.");
                }

            } else {
                System.out.println("[FAILED] Login failed. Use a Student account.");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getCount(Connection conn, int id) throws SQLException {
        String sql = "SELECT enrolled_count FROM courses WHERE course_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("enrolled_count");
        }
        return 0;
    }
}