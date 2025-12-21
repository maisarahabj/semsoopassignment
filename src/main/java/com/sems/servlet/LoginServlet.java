
/**
 *
 * @author maisarahabjalil
 */
package com.sems.servlet;

import com.sems.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;


 //Servlet to handle User Login authentication.

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
        
        //Capture the data from the JSP form
        String user = request.getParameter("username");
        String pass = request.getParameter("password");

        //Initialize the DAO to talk to SQL
        UserDAO userDAO = new UserDAO();
        
        // Check if the user exists and the password matches
        boolean isValid = userDAO.validateUser(user, pass); 

        if (isValid) {
            HttpSession session = request.getSession();
            session.setAttribute("username", user); 
            String path = request.getContextPath() + "/student/dashboard.jsp";
            System.out.println("Redirecting to: " + path); // This will show in your GlassFish logs
            response.sendRedirect(path); 
        } else {
            // FAILURE sending the user back to login.jsp
            request.setAttribute("errorMessage", "Invalid username or password.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }

    }
    
    @Override
    public String getServletInfo() {
        return "Handles User Login Authentication";
    }
}