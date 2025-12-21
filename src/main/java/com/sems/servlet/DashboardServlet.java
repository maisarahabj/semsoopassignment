/**
 *
 * @author maisarahabjalil
 */

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
        
        // 1. Session Security Check
        if (session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 2. Get Student Data
        Student student = (Student) session.getAttribute("student");
        int studentId = (student != null) ? student.getStudentId() : 1; 
        
        // 3. Logic for "Today's Day" vs "Next Class"
        CourseDAO courseDAO = new CourseDAO();
        String today = LocalDate.now().getDayOfWeek().name(); 
        today = today.substring(0, 1) + today.substring(1).toLowerCase(); 

        List<Course> displayCourses = courseDAO.getCoursesByDay(studentId, today);
        String sectionTitle = "Today's Classes";

        if (displayCourses.isEmpty() || today.equals("Sunday") || today.equals("Saturday")) {
            displayCourses = courseDAO.getCoursesByDay(studentId, "Monday");
            sectionTitle = "Your Next Classes (Monday)";
        }

        // 4. ATTACH THE DATA to the request so the JSP can see it
        request.setAttribute("courses", displayCourses);
        request.setAttribute("sectionTitle", sectionTitle);
        
        // 5. Forward to JSP
        request.getRequestDispatcher("/student/dashboard.jsp").forward(request, response);
    }
}