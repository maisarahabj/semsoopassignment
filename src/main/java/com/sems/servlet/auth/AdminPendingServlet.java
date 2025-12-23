/**
 *
 * @author maisarahabjalil
 */

package com.sems.servlet.auth;

import com.sems.dao.UserDAO;
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Map<String, Object>> pendingData = new ArrayList<>();
        
        // SQL JOIN to get student_id from students table while filtering pending users
        String sql = "SELECT u.user_id, u.username, u.role, s.student_id " +
                     "FROM users u " +
                     "JOIN students s ON u.user_id = s.user_id " +
                     "WHERE u.status = 'PENDING'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("userId", rs.getInt("user_id"));
                row.put("username", rs.getString("username"));
                row.put("role", rs.getString("role"));
                row.put("studentId", rs.getInt("student_id")); // New field to display
                pendingData.add(row);
            }
            
            request.setAttribute("pendingUsers", pendingData);
            request.getRequestDispatcher("/admin/adminpending.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String userIdStr = request.getParameter("userId");
        String action = request.getParameter("action"); 

        if (userIdStr != null && action != null) {
            int userId = Integer.parseInt(userIdStr);
            if ("APPROVE".equalsIgnoreCase(action)) {
                userDAO.updateUserStatus(userId, "ACTIVE");
            } else if ("REJECT".equalsIgnoreCase(action)) {
                userDAO.updateUserStatus(userId, "REJECTED");
            }
        }
        response.sendRedirect(request.getContextPath() + "/auth/AdminPendingServlet");
    }
}