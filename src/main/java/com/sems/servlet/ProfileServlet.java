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
    private final ActivityLogDAO logDAO = new ActivityLogDAO(); // Added

    // 1. VIEW PROFILE (GET) - No logging needed for just viewing
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId != null) {
            Student student = studentDAO.getStudentByUserId(userId);
            request.setAttribute("student", student);
            request.getRequestDispatcher("/student/viewprofile.jsp").forward(request, response);
        } else {
            response.sendRedirect("login.jsp");
        }
    }

    // 2. EDIT PROFILE (POST) - Log the update action
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        Integer studentId = (Integer) session.getAttribute("studentId"); // Get studentId for target tracking

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Get updated fields
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        boolean success = studentDAO.updateStudentContactInfo(userId, email, phone, address);

        if (success) {
            // TRIGGER LOG: Record that the student updated their profile
            logDAO.recordLog(
                    userId,
                    studentId,
                    "UPDATE_PROFILE",
                    "Student updated their personal contact information (Email/Phone/Address)."
            );

            response.sendRedirect(request.getContextPath() + "/ProfileServlet?status=success");
        } else {
            response.sendRedirect(request.getContextPath() + "/ProfileServlet?error=1");
        }
    }
}
