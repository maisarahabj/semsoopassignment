package com.sems.util;

import com.sems.dao.UserDAO;
import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("=== Starting Database Connection Test ===");
        
        // Test 1: Physical Connection
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                System.out.println("SUCCESS: Connected to the database successfully!");
            } else {
                System.out.println("FAILED: Connection is null.");
            }
        } catch (Exception e) {
            System.out.println("FAILED: Error during connection.");
            e.printStackTrace();
        }

        // Test 2: DAO Logic Test
        System.out.println("\n=== Testing UserDAO.validateUser ===");
        UserDAO dao = new UserDAO();
        // Use the exact dummy credentials you inserted in SQL
        boolean result = dao.validateUser("johndoe", "password123");
        
        if (result) {
            System.out.println("SUCCESS: User 'johndoe' validated correctly!");
        } else {
            System.out.println("FAILED: Could not validate 'johndoe'. Check column names or table data.");
        }
    }
}