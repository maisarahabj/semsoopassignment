/**
 *
 * @author maisarahabjalil
 */
package com.sems.servlet;

import com.sems.dao.StudentDAO;
import com.sems.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;

@WebServlet("/ProfileServlet")
public class ProfileServlet extends HttpServlet {

    private StudentDAO studentDAO = new StudentDAO();

    // 1. VIEW PROFILE (GET)
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

    // 2. EDIT PROFILE (POST)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Get only the fields the student is allowed to change
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        boolean success = studentDAO.updateStudentContactInfo(userId, email, phone, address);

        if (success) {
            // Redirect back to GET to show the updated data
            response.sendRedirect(request.getContextPath() + "/ProfileServlet?status=success");
        } else {
            response.sendRedirect(request.getContextPath() + "/ProfileServlet?error=1");
        }
    }
}
