package com.sems.model;

import java.sql.Timestamp;
/**
 *
 * @author maisarahabjalil
 */
public class User {
    // Private Fields from SQL
    private int userId;
    private String username;
    private String passwordHash;
    private String role; 
    private Timestamp createdAt;
    private boolean isActive;

    // Constructors
   
    public User() {
        this.isActive = true; 
    }
    
    public User(String username, String passwordHash, String role) {
        this();
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }

    
    // Checks if user is admin - used for Role-Based Access Control
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(this.role);
    }

    // Overriding toString 
    @Override
    public String toString() {
        return "User [ID=" + userId + ", Username=" + username + ", Role=" + role + "]";
    }
}

