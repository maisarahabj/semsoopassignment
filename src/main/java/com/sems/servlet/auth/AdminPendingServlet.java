/**
 *
 * @author maisarahabjalil
 */
package com.sems.servlet.auth;

import com.sems.dao.UserDAO;
import com.sems.dao.ActivityLogDAO; // 1. Added ActivityLogDAO
import com.sems.util.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/auth/AdminPendingServlet")
public class AdminPendingServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private ActivityLogDAO logDAO = new ActivityLogDAO(); // 2. Initialized logDAO

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Map<String, Object>> pendingData = new ArrayList<>();
        List<Map<String, Object>> rejectedData = new ArrayList<>();

        // Query both statuses
        String sql = "SELECT u.user_id, u.username, u.role, u.status, u.rejection_reason, s.student_id "
                + "FROM users u "
                + "JOIN students s ON u.user_id = s.user_id "
                + "WHERE u.status IN ('PENDING', 'REJECTED')";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("userId", rs.getInt("user_id"));
                row.put("username", rs.getString("username"));
                row.put("role", rs.getString("role"));
                row.put("status", rs.getString("status"));
                row.put("reason", rs.getString("rejection_reason"));
                row.put("studentId", rs.getInt("student_id"));

                if ("PENDING".equals(rs.getString("status"))) {
                    pendingData.add(row);
                } else {
                    rejectedData.add(row);
                }
            }

            request.setAttribute("pendingUsers", pendingData);
            request.setAttribute("rejectedUsers", rejectedData);
            request.getRequestDispatcher("/admin/adminpending.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 3. Get the Admin's User ID from the session for logging
        HttpSession session = request.getSession();
        Integer adminUserId = (Integer) session.getAttribute("userId");

        String userIdStr = request.getParameter("userId");
        String action = request.getParameter("action");
        String reason = request.getParameter("reason");

        if (userIdStr != null && action != null && adminUserId != null) {
            int targetUserId = Integer.parseInt(userIdStr);

            if ("APPROVE".equalsIgnoreCase(action)) {
                userDAO.updateUserStatus(targetUserId, "ACTIVE");
                logDAO.recordLog(adminUserId, targetUserId, "APPROVE_USER", "Admin approved User #" + targetUserId);
            } else if ("REJECT".equalsIgnoreCase(action)) {
                // Use the new DAO method with reason
                boolean success = userDAO.rejectUserWithReason(targetUserId, reason);
                if (success) {
                    logDAO.recordLog(adminUserId, targetUserId, "REJECT_USER",
                            "Admin rejected User #" + targetUserId + ". Reason: " + reason);
                }
            }
        }
        response.sendRedirect(request.getContextPath() + "/auth/AdminPendingServlet");
    }
}
