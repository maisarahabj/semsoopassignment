/* 
    Created on : 21 Dec 2025, 2:58:21 pm
    Author     : maisarahabjalil
*/


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
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String pass = request.getParameter("password");

        UserDAO userDAO = new UserDAO();
        User user = userDAO.validateUser(username, pass); 

        if (user != null) {
            if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
                request.setAttribute("errorMessage", "Your account is pending admin approval.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return; 
            }

            HttpSession session = request.getSession();
            session.setAttribute("username", user.getUsername());
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("role", user.getRole());

            if ("admin".equalsIgnoreCase(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/DashboardServlet"); 
                
            } else if ("student".equalsIgnoreCase(user.getRole())) {
                StudentDAO studentDAO = new StudentDAO();
                int studentId = studentDAO.getStudentIdByUserId(user.getUserId());
                
                if (studentId != -1) {
                    session.setAttribute("studentId", studentId);
                    response.sendRedirect(request.getContextPath() + "/DashboardServlet"); 
                } else {
                    response.sendRedirect(request.getContextPath() + "/student/create_profile.jsp");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            }

        } else {
            request.setAttribute("errorMessage", "Invalid username or password.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles User Login Authentication";
    }
}