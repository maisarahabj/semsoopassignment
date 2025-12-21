package com.sems.servlet;

import com.sems.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String user = request.getParameter("username");
        String pass = request.getParameter("password");

        //CHANGES HERE: Added server-side logging to see exactly what is being received
        System.out.println("DEBUG: Servlet received Username: [" + user + "]");
        System.out.println("DEBUG: Servlet received Password: [" + pass + "]");

        UserDAO userDAO = new UserDAO();
        boolean isValid = userDAO.validateUser(user, pass); 
        
        //CHANGES HERE: Logging the result of the database check
        System.out.println("DEBUG: Database validation result: " + isValid);

        if (isValid) {
            HttpSession session = request.getSession();
            session.setAttribute("username", user);
            String contextPath = request.getContextPath();
            String target = contextPath + "/student/dashboard.jsp";
            System.out.println("DEBUG: Redirecting to " + target); // Check this in GlassFish logs!
            response.sendRedirect(target);
        } else {
            //CHANGES HERE: Logging the failure for debugging
            System.out.println("DEBUG: Validation failed. Forwarding back to login.jsp");
            request.setAttribute("errorMessage", "Invalid username or password.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles User Login Authentication";
    }
}