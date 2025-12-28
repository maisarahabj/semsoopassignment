/**
 *
 * @author maisarahabjalil
 */

package com.sems.dao;

import com.sems.model.ActivityLog;
import com.sems.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDAO {

    /**
     * Records a new activity into the database.
     * Call this from any Servlet after a successful database action.
     */
    public void recordLog(int userId, Integer targetId, String actionType, String description) {
        String sql = "INSERT INTO activity_logs (user_id, target_id, action_type, description) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            // Handle null for targetId (e.g., login actions might not have a target)
            if (targetId != null) {
                pstmt.setInt(2, targetId);
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            
            pstmt.setString(3, actionType);
            pstmt.setString(4, description);
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetches all logs with performer names for the Admin Dashboard.
     */
    public List<ActivityLog> getAllLogs() {
        List<ActivityLog> logs = new ArrayList<>();
        
        // This query joins logs with users and students to get names and roles
        String sql = "SELECT l.*, u.username, u.role, s.first_name, s.last_name " +
                     "FROM activity_logs l " +
                     "JOIN users u ON l.user_id = u.user_id " +
                     "LEFT JOIN students s ON u.user_id = s.user_id " +
                     "ORDER BY l.timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ActivityLog log = new ActivityLog();
                log.setLogId(rs.getInt("log_id"));
                log.setUserId(rs.getInt("user_id"));
                log.setTargetId((Integer) rs.getObject("target_id"));
                log.setActionType(rs.getString("action_type"));
                log.setDescription(rs.getString("description"));
                log.setTimestamp(rs.getTimestamp("timestamp"));
                log.setPerformerRole(rs.getString("role"));

                // If it's a student, we use their real name; otherwise, we use the username
                String firstName = rs.getString("first_name");
                if (firstName != null) {
                    log.setPerformerName(firstName + " " + rs.getString("last_name"));
                } else {
                    log.setPerformerName(rs.getString("username"));
                }
                
                logs.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
}