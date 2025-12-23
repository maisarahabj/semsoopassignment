/**
 *
 * @author maisarahabjalil
 */

package com.sems.servlet.auth;

import com.sems.dao.UserDAO;
import com.sems.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/auth/AdminPendingServlet")
public class AdminPendingServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Use the DAO method that returns List<User>
        List<User> pendingUsers = userDAO.getPendingUsers();
        
        request.setAttribute("pendingUsers", pendingUsers);
        
        // Ensure path is correct based on your folder structure
        request.getRequestDispatcher("/admin/adminpending.jsp").forward(request, response);
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