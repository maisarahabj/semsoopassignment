package com.sems.util;

import com.sems.dao.*;
import com.sems.model.*;
import java.sql.*;

public class TestConnection {
    public static void main(String[] args) {
        // Initialize DAOs
        EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
        CourseDAO courseDAO = new CourseDAO();
        
        // Configuration
        int testStudentId = 5; // The ID you just verified in SQL
        int testCourseId = 1;  // Ensure this ID exists in your 'courses' table
        
        System.out.println("--- STARTING ENROLLMENT SYNC TEST ---");
        System.out.println("Target: Student " + testStudentId + " -> Course " + testCourseId);

        try {
            // STEP 1: Attempt Enrollment
            // This checks the 'enrollments' table
            boolean enrollmentSuccess = enrollmentDAO.enrollStudent(testStudentId, testCourseId);

            if (enrollmentSuccess) {
                System.out.println("Step 1 SUCCESS: Student added to enrollment table.");
                
                // STEP 2: Only increment if Step 1 worked
                // This updates the 'courses' table
                boolean countUpdated = courseDAO.incrementEnrolledCount(testCourseId);
                
                if (countUpdated) {
                    System.out.println("Step 2 SUCCESS: Course enrolled_count increased by 1.");
                    System.out.println("\n--- FULL TEST PASSED ---");
                } else {
                    System.out.println("Step 2 FAILED: Enrollment succeeded but count failed to update.");
                }
            } else {
                System.out.println("Step 1 FAILED: Likely a duplicate enrollment or missing Student/Course ID.");
                System.out.println("RESULT: Step 2 was skipped automatically. Database is safe!");
            }

        } catch (Exception e) {
            System.err.println("TEST ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}