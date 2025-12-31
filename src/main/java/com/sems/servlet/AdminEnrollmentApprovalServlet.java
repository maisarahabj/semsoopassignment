/**
 * Servlet for managing pending enrollment approvals
 * @author maisarahabjalil
 */
package com.sems.servlet;

import com.sems.dao.EnrollmentDAO;
import com.sems.dao.ActivityLogDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/AdminEnrollmentApprovalServlet")
public class AdminEnrollmentApprovalServlet extends HttpServlet {

    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final ActivityLogDAO logDAO = new ActivityLogDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get all pending enrollments
        List<Map<String, Object>> pendingEnrollments = enrollmentDAO.getPendingEnrollments();
        request.setAttribute("pendingEnrollments", pendingEnrollments);
        request.getRequestDispatcher("/admin/adminpendingEnrollments.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer adminUserId = (Integer) session.getAttribute("userId");

        String enrollmentIdStr = request.getParameter("enrollmentId");
        String action = request.getParameter("action");

        if (enrollmentIdStr != null && action != null && adminUserId != null) {
            int enrollmentId = Integer.parseInt(enrollmentIdStr);
            String newStatus = "";
            String logAction = "";

            if ("APPROVE".equalsIgnoreCase(action)) {
                newStatus = "Enrolled";
                logAction = "APPROVE_ENROLLMENT";
            } else if ("REJECT".equalsIgnoreCase(action)) {
                newStatus = "REJECTED";
                logAction = "REJECT_ENROLLMENT";
            }

            boolean success = enrollmentDAO.updateEnrollmentStatus(enrollmentId, newStatus);
            if (success) {
                // Log the action
                logDAO.recordLog(adminUserId, enrollmentId, logAction,
                        "Admin " + action.toLowerCase() + "d enrollment request #" + enrollmentId);
            }
        }

        response.sendRedirect(request.getContextPath() + "/AdminEnrollmentApprovalServlet");
    }
}
