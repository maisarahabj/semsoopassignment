/**
 *
 * @author maisarahabjalil
 */

package com.sems.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/LogoutServlet"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        //Get current session without creating a new one
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            //See who is logging out in the GlassFish logs
            System.out.println("DEBUG: Logging out user: " + session.getAttribute("username"));
            
            //Specifically remove the attribute you set in LoginServlet
            session.removeAttribute("username");
            
            //Destroy the session
            session.invalidate();
        }

        // Redirect to login page
        // Since we haven't built the UI yet, this will 404 for now, 
        response.sendRedirect("login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}