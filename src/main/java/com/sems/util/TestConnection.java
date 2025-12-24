package com.sems.util;

import com.sems.dao.UserDAO;
import com.sems.dao.EnrollmentDAO;
import com.sems.model.User;
import java.util.List;
import java.util.Scanner;

public class TestConnection {

    public static void main(String[] args) {
        EnrollmentDAO dao = new EnrollmentDAO();
        
        // --- TEST SCENARIO: ENROLLING IN A FULL COURSE ---
        // Assume Student ID 1 and Course ID 1 (Make sure Course 1 exists in DB)
        int testStudentId = 1; 
        int testCourseId = 1;

        System.out.println("=== STARTING ENROLLMENT TEST ===");
        
        boolean result = dao.adminEnrollStudentInCourse(testStudentId, testCourseId);
        
        if (result) {
            System.out.println("RESULT: Success! (Student was enrolled)");
        } else {
            System.out.println("RESULT: Failed! (Logic correctly blocked enrollment or DB error occurred)");
        }
        
        System.out.println("=== TEST FINISHED ===");
    }
}