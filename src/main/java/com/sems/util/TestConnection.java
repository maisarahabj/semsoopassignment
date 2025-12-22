package com.sems.util;

import com.sems.dao.*;
import com.sems.model.*;
import java.sql.*;
import java.util.List;
import java.util.Scanner;

public class TestConnection {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        UserDAO userDAO = new UserDAO();
        StudentDAO studentDAO = new StudentDAO();
        CourseDAO courseDAO = new CourseDAO();

        try {
            System.out.println("=== SIMULATED LOGIN & DASHBOARD FLOW ===");
            System.out.print("Username: ");
            String username = sc.nextLine();
            System.out.print("Password: ");
            String pass = sc.nextLine();

            // STEP 1: LOGIN (Simulating LoginServlet)
            User user = userDAO.validateUser(username, pass);

            if (user != null && "ACTIVE".equalsIgnoreCase(user.getStatus())) {
                System.out.println("\n[STEP 1 SUCCESS] User Authenticated. Role: " + user.getRole());

                if ("student".equalsIgnoreCase(user.getRole())) {
                    
                    // STEP 2: RESOLVE STUDENT ID (Simulating LoginServlet Logic)
                    int studentId = studentDAO.getStudentIdByUserId(user.getUserId());
                    
                    if (studentId != -1) {
                        System.out.println("[STEP 2 SUCCESS] Student Profile Found. ID: " + studentId);

                        // STEP 3: LOAD DATA (Simulating DashboardServlet Logic)
                        System.out.println("\n--- LOADING DASHBOARD DATA ---");
                        Student profile = studentDAO.getStudentByUserId(user.getUserId());
                        List<Course> enrolled = courseDAO.getCoursesByStudentId(studentId);

                        System.out.println("Welcome, " + profile.getFirstName() + " " + profile.getLastName());
                        System.out.println("Enrolled Courses count: " + enrolled.size());
                        
                        // Displaying the Grid Data
                        System.out.println("\nYour Weekly Schedule Data:");
                        for (Course c : enrolled) {
                            System.out.println(" > " + c.getCourseDay() + " at " + c.getCourseTime() + " : " + c.getCourseName());
                        }

                        System.out.println("\n[FINAL VERDICT] The Dashboard data is ready to be sent to JSP!");

                    } else {
                        System.out.println("[REDIRECT] No profile found. Redirecting to Profile Creation...");
                    }
                }
            } else {
                System.out.println("[FAILED] Invalid credentials or account NOT ACTIVE.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}