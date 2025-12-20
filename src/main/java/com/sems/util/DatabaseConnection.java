/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sems.util;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author maisarahabjalil
 */
public class DatabaseConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/sems_db";
        String user = "root";
        String password = "Rockie.69";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("MySQL JDBC Driver is working!");
            conn.close();
        } catch (Exception e) {
            System.out.println("Connection failed");
            e.printStackTrace();
        }
    }
    
}

///hi