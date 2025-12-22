package com.sems.servlet.auth;

import com.sems.dao.UserDAO;
import com.sems.dao.StudentDAO; 
import com.sems.model.User;     
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/auth/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // If they try to access /auth/LoginServlet via URL, send them to login page
        response.sendRedirect("../login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String pass = request.getParameter("password");

        System.out.println("DEBUG: Login attempt for username: [" + username + "]");

        UserDAO userDAO = new UserDAO();
        User user = userDAO.validateUser(username, pass); 

        if (user != null) {
            // ACCOUNT STATUS CHECK
            // This prevents PENDING or REJECTED users from logging in even with correct passwords
            if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
                System.out.println("DEBUG: Login blocked. User status is: " + user.getStatus());
                request.setAttribute("errorMessage", "Your account is pending admin approval.");
                request.getRequestDispatcher("../login.jsp").forward(request, response);
                return; 
            }

            // SUCCESSFUL LOGIN - Create Session
            System.out.println("DEBUG: Login Success! Role: " + user.getRole());
            HttpSession session = request.getSession();
            session.setAttribute("username", user.getUsername());
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("role", user.getRole());

            //ROLE-BASED REDIRECTS
            if ("admin".equalsIgnoreCase(user.getRole())) {
                // Admin dashboard
                response.sendRedirect("../admin/dashboard"); 
                
            } else if ("student".equalsIgnoreCase(user.getRole())) {
                // STUDENT LOGIC: We MUST get the student_id to show their specific courses
                StudentDAO studentDAO = new StudentDAO();
                int studentId = studentDAO.getStudentIdByUserId(user.getUserId());
                
                System.out.println("DEBUG: Fetching profile for User ID " + user.getUserId());

                if (studentId != -1) {
                    // Profile exists: Store ID and go to dashboard
                    session.setAttribute("studentId", studentId);
                    System.out.println("DEBUG: Student ID " + studentId + " found. Going to dashboard.");
                    response.sendRedirect("../student/dashboard"); 
                } else {
                    // No profile: User exists in 'users' but needs to fill 'students' table
                    System.out.println("DEBUG: User has no student record. Going to profile creation.");
                    response.sendRedirect("../student/create_profile.jsp");
                }
            } else {
                // Fallback for unknown roles
                response.sendRedirect("../login.jsp");
            }

        } else {
            //INVALID CREDENTIALS
            System.out.println("DEBUG: Validation failed for " + username);
            request.setAttribute("errorMessage", "Invalid username or password.");
            request.getRequestDispatcher("../login.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles User Login Authentication, Status Checks, and Role-Based Redirects";
    }
}