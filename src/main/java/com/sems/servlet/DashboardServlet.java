package com.sems.servlet;

import com.sems.dao.CourseDAO;
import com.sems.model.Course;
import com.sems.model.Student;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Student student = (Student) session.getAttribute("student");

        if (student == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Fetch courses using the student's ID from the object
        CourseDAO courseDAO = new CourseDAO();
        List<Course> allCourses = courseDAO.getCoursesByStudentId(student.getStudentId());

        // Pass the list to the JSP
        request.setAttribute("allCourses", allCourses);
        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }
}