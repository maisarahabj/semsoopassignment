package com.sems.dao;
import com.sems.model.User;
import com.sems.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author maisarahabjalil
 */

public class UserDAO {
    
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    
    // SQL Constants 
    private static final String INSERT_USER = 
            "INSERT INTO users (username, password_hash, role, is_active) VALUES (?, ?, ?, ?)";
    
    private static final String SELECT_ALL_USERS = 
            "SELECT * FROM users ORDER BY username";
    
    private static final String SELECT_USER_BY_ID = 
            "SELECT * FROM users WHERE user_id = ?";
    
    private static final String SELECT_USER_BY_USERNAME = 
            "SELECT * FROM users WHERE username = ?";

    private static final String DELETE_USER = 
            "DELETE FROM users WHERE user_id = ?";

    //Create a new user and return the generated ID
    public int createUser(User user) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getRole());
            pstmt.setBoolean(4, user.isIsActive()); // Using your isIsActive() method
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating user", e);
        } finally {
            DatabaseConnection.closeResources(rs, pstmt, conn);
        }
        return -1;
    }

    //Authenticate user (verify credentials)     
    public User authenticate(String username, String passwordHash) {
        User user = getUserByUsername(username);
                
        if (user != null && user.isIsActive() && user.getPasswordHash().equals(passwordHash)) {
            return user;
        }
        return null;
    }

    public User getUserByUsername(String username) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_USER_BY_USERNAME);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching user by username", e);
        } finally {
            DatabaseConnection.closeResources(rs, pstmt, conn);
        }
        return null;
    }


    //Helper to map SQL Result to  User Model
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setIsActive(rs.getBoolean("is_active"));
        return user;
    }
    
    //log in check - matches login.jsp
    public boolean validateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ? AND is_active = 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Returns true if a match is found (User exists and is active)
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error validating user credentials", e);
            return false;
        }
    }
    
    public UserDAO() {
    // This empty constructor allows the Test class to instantiate it
    }
      


}