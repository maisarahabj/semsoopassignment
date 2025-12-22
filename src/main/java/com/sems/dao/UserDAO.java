/**
 *
 * @author maisarahabjalil
 */
package com.sems.dao;
import com.sems.model.User;
import com.sems.util.DatabaseConnection;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {
    
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    
    private static final String SELECT_USER_BY_USERNAME = 
            "SELECT * FROM users WHERE username = ?";

    public User validateUser(String username, String password) {
        // Updated SQL to match your table column 'password_hash'
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ? AND is_active = 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Use your existing helper method to build the User object
                    User user = extractUserFromResultSet(rs);
                    System.out.println("DEBUG (DAO): User validated successfully. Role: " + user.getRole());
                    return user; 
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error validating user credentials", e);
        }
        
        System.out.println("DEBUG (DAO): Validation failed for username: " + username);
        return null; // Return null if user not found or password incorrect
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
            LOGGER.log(Level.SEVERE, "Error fetching user", e);
        } finally {
            DatabaseConnection.closeResources(rs, pstmt, conn);
        }
        return null;
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setIsActive(rs.getBoolean("is_active"));
        return user;
    }
    
    public UserDAO() {}
}
