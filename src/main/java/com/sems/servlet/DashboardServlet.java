package com.sems.servlet; 

import com.sems.dao.CourseDAO;
import com.sems.model.Course;
import com.sems.model.Student;
import java.io.IOException;
import java.time.LocalDate;      
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
        
        // Session Security Check
        if (session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Get Student Data
        Student student = (Student) session.getAttribute("student");
        int studentId = (student != null) ? student.getStudentId() : 1; 
        
        // Logic for "Today's Day"
        CourseDAO courseDAO = new CourseDAO();
        String today = LocalDate.now().getDayOfWeek().name(); 
        today = today.substring(0, 1) + today.substring(1).toLowerCase(); 

        // fetching today's courses only.
        List<Course> displayCourses = courseDAO.getCoursesByDay(studentId, today);
        String sectionTitle = "Today's Classes";

        //ATTACH THE DATA to the request so the JSP can see it
        request.setAttribute("courses", displayCourses);
        request.setAttribute("sectionTitle", sectionTitle);
        
        //Forward to JSP
        request.getRequestDispatcher("/student/dashboard.jsp").forward(request, response);
        
        
        
        
    }
}