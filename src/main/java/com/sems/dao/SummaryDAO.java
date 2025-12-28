/**
 *
 * @author maisarahabjalil
 */
package com.sems.dao;

import com.sems.util.DatabaseConnection;
import com.sems.util.DatabaseConnection;
import com.sems.model.Course;
import com.sems.model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import java.util.Map;

public class SummaryDAO {

    // 1. Campus-Wide Average GPA
    public double getCampusAvgGPA() {
        // Only average GPA for users who are ACTIVE and have the role 'student'
        String sql = "SELECT ROUND(AVG(s.gpa), 2) FROM students s "
                + "JOIN users u ON s.user_id = u.user_id "
                + "WHERE u.status = 'ACTIVE' AND u.role = 'student'";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // 2. Top-Rated Course (by Evaluation)
    public Map<String, Object> getTopRatedCourse() {
        Map<String, Object> result = new HashMap<>();
        String sql = "SELECT c.course_name, ROUND(AVG(e.rating), 1) as avg_rating "
                + "FROM evaluations e "
                + "JOIN courses c ON e.course_id = c.course_id "
                + "GROUP BY c.course_id, c.course_name "
                + "ORDER BY avg_rating DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                result.put("name", rs.getString("course_name"));
                result.put("rating", rs.getDouble("avg_rating"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 3. Total Active Enrollments
    public int getTotalActiveEnrollments() {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE status = 'enrolled'";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 4. Grade Distribution for Chart/Table
    public Map<String, Integer> getGradeDistribution() {
        Map<String, Integer> dist = new HashMap<>();
        String sql = "SELECT "
                + "CASE WHEN gpa >= 3.5 THEN '3.5 - 4.0' "
                + "WHEN gpa >= 3.0 THEN '3.0 - 3.49' "
                + "WHEN gpa >= 2.0 THEN '2.0 - 2.99' "
                + "ELSE 'Under 2.0' END as tier, COUNT(*) as count "
                + "FROM students GROUP BY tier";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                dist.put(rs.getString("tier"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dist;
    }

// 5. Course Popularity (Enrollment Counts)
    public List<Course> getCoursePopularity() {
        List<Course> list = new java.util.ArrayList<>();
        String sql = "SELECT course_name, enrolled_count, capacity FROM courses ORDER BY enrolled_count DESC";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Course c = new Course();
                c.setCourseName(rs.getString("course_name"));
                c.setEnrolledCount(rs.getInt("enrolled_count"));
                c.setCapacity(rs.getInt("capacity"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 6. Top 5 Students (Dean's List)
    public List<Student> getTopStudents() {
        List<Student> list = new ArrayList<>();

        // Match the column name 'first_name' from your StudentDAO constants
        String sql = "SELECT s.first_name, s.gpa FROM students s "
                + "JOIN users u ON s.user_id = u.user_id "
                + "WHERE u.status = 'ACTIVE' " // 1. Only Approved Users
                + "AND u.role = 'student' " // 2. No Admins
                + "AND s.gpa >= 3.5 " // 3. Logic: 3.5 and above
                + "ORDER BY s.gpa DESC LIMIT 5";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            // REMOVED the stray 's' that was here
            while (rs.next()) {
                Student s = new Student();
                // Map from 'first_name' column to the model
                s.setFirstName(rs.getString("first_name"));
                s.setGpa(rs.getDouble("gpa"));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 7. Force Refresh all GPAs in the student table
    public void recalculateAllGPAs() {
        String getIdsSql = "SELECT student_id FROM students";
        EnrollmentDAO enrollmentDAO = new EnrollmentDAO(); // We need this for the calculation logic

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(getIdsSql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int studentId = rs.getInt("student_id");
                // This calls your existing method in EnrollmentDAO to math the GPA 
                // and save it to the students table
                enrollmentDAO.updateAndGetStudentCGPA(studentId);
            }
            System.out.println("LOG: All student GPAs have been recalculated for the report.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
