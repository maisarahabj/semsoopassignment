/**
 *
 * @author maisarahabjalil
 */
package com.sems.servlet;

import com.sems.dao.ActivityLogDAO;
import com.sems.model.ActivityLog;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/ActivityServlet")
public class ActivityServlet extends HttpServlet {

    private final ActivityLogDAO logDAO = new ActivityLogDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : "";

        // Security: Only allow admins to view logs
        if (!"admin".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 1. Fetch all logs from the database
        List<ActivityLog> logs = logDAO.getAllLogs();

        // 2. Pass the list to the JSP
        request.setAttribute("logs", logs);
        request.getRequestDispatcher("/admin/adminlogs.jsp").forward(request, response);
    }
}
