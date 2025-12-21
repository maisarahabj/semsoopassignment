package com.sems.util;

import java.sql.*;
/**
 *
 * @author maisarahabjalil
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/sems_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "Rockie.69";

    public static Connection getConnection() throws SQLException {
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found: " + e.getMessage());
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    System.out.println("Error closing resource: " + e.getMessage());
                }
            }
        }
    }
}