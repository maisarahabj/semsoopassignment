package com.sems.util;

import com.sems.dao.UserDAO;
import com.sems.model.User;
import java.util.List;
import java.util.Scanner;

public class TestConnection {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UserDAO userDAO = new UserDAO();

        System.out.println("=== SEMS ADMIN CONSOLE LOGIN ===");
        System.out.print("Enter Admin Username: ");
        String username = sc.nextLine();
        System.out.print("Enter Admin Password: ");
        String password = sc.nextLine();

        // 1. Simulate LoginServlet Authentication
        User adminUser = userDAO.validateUser(username, password);

        if (adminUser != null && "admin".equalsIgnoreCase(adminUser.getRole())) {
            System.out.println("\n[SUCCESS] Welcome, Admin: " + adminUser.getUsername());
            System.out.println("Status: " + adminUser.getStatus());
            
            // 2. Simulate AdminPendingServlet Data Loading
            System.out.println("\n--- Fetching Registration Queue (Pending Users) ---");
            List<User> pendingList = userDAO.getPendingUsers();

            if (pendingList.isEmpty()) {
                System.out.println("Queue is empty. No pending registrations found.");
            } else {
                System.out.println("Found " + pendingList.size() + " users waiting for approval:");
                System.out.println("--------------------------------------------------");
                System.out.printf("%-10s | %-15s | %-10s%n", "User ID", "Username", "Role");
                System.out.println("--------------------------------------------------");
                
                for (User u : pendingList) {
                    System.out.printf("%-10d | %-15s | %-10s%n", 
                        u.getUserId(), u.getUsername(), u.getRole());
                }
            }
        } else if (adminUser != null) {
            System.out.println("[DENIED] User authenticated but is NOT an admin. Current role: " + adminUser.getRole());
        } else {
            System.out.println("[FAILED] Invalid admin credentials.");
        }
        
        sc.close();
    }
}