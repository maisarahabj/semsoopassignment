package com.sems.servlet;

import com.sems.dao.*;
import com.sems.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {
    private StudentDAO studentDAO = new StudentDAO();
    private CourseDAO courseDAO = new CourseDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if ("admin".equalsIgnoreCase(role)) {
            // Updated path based on your folder structure
            request.getRequestDispatcher("/admin/admindash.jsp").forward(request, response);
            
        } else if ("student".equalsIgnoreCase(role)) {
            Student student = studentDAO.getStudentByUserId(userId);
            
            if (student != null) {
                List<Course> enrolledCourses = courseDAO.getCoursesByStudentId(student.getStudentId());
                
                request.setAttribute("student", student);
                request.setAttribute("enrolledCourses", enrolledCourses);
                
                // FIXED PATH: Points to the student folder as seen in your file tree
                request.getRequestDispatcher("/student/dashboard.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/student/create_profile.jsp");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}