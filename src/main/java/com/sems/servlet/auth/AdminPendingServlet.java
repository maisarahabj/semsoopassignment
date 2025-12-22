/**
 *
 * @author maisarahabjalil
 */

package com.sems.servlet.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/auth/AdminPendingServlet")
public class AdminPendingServlet extends HttpServlet {

    // 1. GET Method: Fetches the list to display on the JSP
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Map<String, Object>> pendingUsers = new ArrayList<>();
        String url = "jdbc:mysql://localhost:3306/sems_db?useSSL=false&allowPublicKeyRetrieval=true";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, "root", "Rockie.69")) {
                // JOIN so we see the names from students table too
                String sql = "SELECT u.user_id, u.username, u.role, s.first_name, s.last_name, s.email " +
                             "FROM users u JOIN students s ON u.user_id = s.user_id " +
                             "WHERE u.status = 'PENDING'";
                
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("userId", rs.getInt("user_id"));
                    user.put("username", rs.getString("username"));
                    user.put("role", rs.getString("role"));
                    user.put("fullName", rs.getString("first_name") + " " + rs.getString("last_name"));
                    user.put("email", rs.getString("email"));
                    pendingUsers.add(user);
                    

                }
            }
            request.setAttribute("pendingUsers", pendingUsers);
            request.getRequestDispatcher("/adminpending.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // 2. POST Method: Handles Approve/Reject button clicks
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String userId = request.getParameter("userId");
        String action = request.getParameter("action"); // "approve" or "reject"
        String url = "jdbc:mysql://localhost:3306/sems_db?useSSL=false&allowPublicKeyRetrieval=true";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, "root", "Rockie.69")) {
                String sql;
                if ("approve".equals(action)) {
                    // Update both columns to be safe as we discussed
                    sql = "UPDATE users SET is_active = 1, status = 'ACTIVE' WHERE user_id = ?";
                } else {
                    sql = "DELETE FROM users WHERE user_id = ?"; // Rejecting deletes the request
                }

                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, Integer.parseInt(userId));
                pstmt.executeUpdate();
            }
            // Refresh the page to show the updated list
            response.sendRedirect("AdminPendingServlet");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("AdminPendingServlet?error=true");
        }
    }
}