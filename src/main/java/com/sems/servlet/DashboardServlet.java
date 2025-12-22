package com.sems.servlet; // Move this to com.sems.servlet.student if you want to be consistent!

import com.sems.dao.CourseDAO;
import com.sems.model.Course;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/student/dashboard")
public class DashboardServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        Integer studentId = (Integer) session.getAttribute("studentId");

        if (studentId == null) {            
            response.sendRedirect("../login.jsp");
            return;
        }

        CourseDAO courseDAO = new CourseDAO();
        List<Course> allCourses = courseDAO.getCoursesByStudentId(studentId);

        request.setAttribute("allCourses", allCourses);
        

        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }
}