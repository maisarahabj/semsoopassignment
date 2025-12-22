package com.sems.servlet.auth;

import com.sems.dao.UserDAO;
import com.sems.dao.StudentDAO; // New Import!
import com.sems.model.User;     // New Import!
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

// 1. Updated URL to match your folder structure
@WebServlet(name = "LoginServlet", urlPatterns = {"/auth/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("../login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String pass = request.getParameter("password");

        System.out.println("DEBUG: Servlet received Username: [" + username + "]");

        UserDAO userDAO = new UserDAO();
        
        User user = userDAO.validateUser(username, pass); 
        
        if (user != null) {
            System.out.println("DEBUG: Login Success! Role: " + user.getRole());

            HttpSession session = request.getSession();
            session.setAttribute("username", user.getUsername());
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("role", user.getRole());

            // ROLE-BASED LOGIC
            if ("admin".equalsIgnoreCase(user.getRole())) {
                // Admin goes to their dashboard
                response.sendRedirect("../admin/dashboard"); 
                
            } else if ("student".equalsIgnoreCase(user.getRole())) {
                // STUDENT SPECIFIC LOGIC: Get their Student ID for enrollments
                StudentDAO studentDAO = new StudentDAO();
                int studentId = studentDAO.getStudentIdByUserId(user.getUserId());
                
                if (studentId != -1) {
                    session.setAttribute("studentId", studentId);
                    System.out.println("DEBUG: Student ID " + studentId + " stored in session.");
                    response.sendRedirect("../student/dashboard"); // Assumes web/student/dashboard.jsp exists
                } else {
                    // Profile exists in User table but NOT in Student table yet
                    System.out.println("DEBUG: User is student but has no profile yet.");
                    response.sendRedirect("../student/create_profile.jsp");
                }
            } else {
                // Unknown role fallback
                response.sendRedirect("../login.jsp");
            }

        } else {
            System.out.println("DEBUG: Validation failed.");
            request.setAttribute("errorMessage", "Invalid username or password.");
            request.getRequestDispatcher("../login.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles User Login Authentication with Role Support";
    }
}