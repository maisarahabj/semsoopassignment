/**
 *
 * @author maisarahabjalil
 */
package com.sems.servlet;

import com.sems.dao.StudentDAO;
import com.sems.dao.ActivityLogDAO;
import com.sems.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/ProfileServlet")
public class ProfileServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();
    private final ActivityLogDAO logDAO = new ActivityLogDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId != null) {
            Student userDetails = studentDAO.getStudentByUserId(userId);
            request.setAttribute("student", userDetails);

            if ("admin".equals(role)) {
                request.getRequestDispatcher("/admin/adminprofile.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/student/viewprofile.jsp").forward(request, response);
            }
        } else {
            response.sendRedirect("login.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        Integer studentId = (Integer) session.getAttribute("studentId");
        String role = (String) session.getAttribute("role"); // FIXED: Added this line to define 'role'

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        boolean success = studentDAO.updateStudentContactInfo(userId, email, phone, address);

        if (success) {
            // Determine log description based on role
            String logDesc = "admin".equals(role)
                    ? "Admin updated their own profile information."
                    : "Student updated their personal contact information.";

            logDAO.recordLog(
                    userId,
                    (studentId != null ? studentId : 0), // Use 0 if admin has no studentId
                    "UPDATE_PROFILE",
                    logDesc
            );

            response.sendRedirect(request.getContextPath() + "/ProfileServlet?status=success");
        } else {
            response.sendRedirect(request.getContextPath() + "/ProfileServlet?error=1");
        }
    }
}
