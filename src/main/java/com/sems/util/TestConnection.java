package com.sems.util;

import com.sems.dao.UserDAO;
import com.sems.dao.EnrollmentDAO;
import com.sems.model.User;
import java.util.List;
import java.util.Scanner;

public class TestConnection {

    public static void main(String[] args) {
        // Manually setting the IDs from your screenshot
        int studentIdToDelete = 101;
        int userIdToDelete = 25; // Associated user_id for 'flut shy'

        com.sems.dao.StudentDAO dao = new com.sems.dao.StudentDAO();

        System.out.println("--- STARTING CONSOLE DELETE TEST ---");
        System.out.println("Target: Student ID " + studentIdToDelete + ", User ID " + userIdToDelete);

        boolean result = dao.deleteStudent(studentIdToDelete, userIdToDelete);

        if (result) {
            System.out.println("SUCCESS: Student and User deleted from database.");
        } else {
            System.out.println("FAILURE: Check the IDE console for SQL Error logs.");
        }
        System.out.println("--- TEST FINISHED ---");
    }
}
