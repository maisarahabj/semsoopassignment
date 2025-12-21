package com.sems.util;

import com.sems.dao.UserDAO;
import java.sql.Connection;
import java.util.Scanner; // CHANGES HERE: Added Scanner for input

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("=== Starting Database Connection Test ===");
        
        // 1. Physical Connection Test
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                System.out.println("SUCCESS: Connected to the database!");
            }
        } catch (Exception e) {
            System.out.println("FAILED: Connection Error: " + e.getMessage());
            return; // Stop if connection fails
        }

        // 2. Interactive Login Test
        // CHANGES HERE: Simulating the Login Form behavior in the console
        UserDAO dao = new UserDAO();
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n--- SIMULATING LOGIN FORM ---");
        System.out.print("Enter Username: ");
        String testUser = scanner.nextLine();
        
        System.out.print("Enter Password: ");
        String testPass = scanner.nextLine();

        boolean isValid = dao.validateUser(testUser, testPass);

        if (isValid) {
            System.out.println("\nSUCCESS: User validated! The Servlet SHOULD be redirecting.");
        } else {
            System.out.println("\nFAILED: Invalid credentials. Check MySQL table data.");
        }
    }
}