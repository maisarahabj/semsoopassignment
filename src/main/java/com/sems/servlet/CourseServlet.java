/**
 *
 * @author maisarahabjalil
 * 
 * handles the display and management of the course data
 * addcourse jsp doGet to get list of courses
 * admincourse jsp doPost to add new course
 * 
 * student view: can ONLY VIEW the damn list
 * admin view: can view, add, remove any course
 */
package com.sems.servlet;

import com.sems.dao.CourseDAO;
import com.sems.model.Course;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/CourseServlet")
public class CourseServlet extends HttpServlet {
    private CourseDAO courseDAO = new CourseDAO();
    
    
    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    
    String action = request.getParameter("action");
    
    // --- ADMIN REMOVE COURSE ROW FOR ---
    if ("delete".equals(action)) {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        
        boolean isDeleted = courseDAO.deleteCourse(courseId);
        
        if (isDeleted) {
            response.sendRedirect("CourseServlet?action=manage&status=deleted");
        } else {
            response.sendRedirect("CourseServlet?action=manage&status=error");
        }
        return;
    }

    // --- ADMIN n STU DISPLAY COURSES ---
    List<Course> allCourses = courseDAO.getAllCourses();
    request.setAttribute("courses", allCourses);

    if ("manage".equals(action)) {
        request.getRequestDispatcher("/admin/admincourse.jsp").forward(request, response);
    } else {
        request.getRequestDispatcher("/addcourse.jsp").forward(request, response);
    }
}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // ADMIN FEATURE: Adding a brand new course to the system
        String courseCode = request.getParameter("courseCode");
        String courseName = request.getParameter("courseName");
        int credits = Integer.parseInt(request.getParameter("credits"));
        int capacity = Integer.parseInt(request.getParameter("capacity"));
        String day = request.getParameter("courseDay");
        String time = request.getParameter("courseTime");

        Course newCourse = new Course();
        newCourse.setCourseCode(courseCode);
        newCourse.setCourseName(courseName);
        newCourse.setCredits(credits);
        newCourse.setCapacity(capacity);
        newCourse.setCourseDay(day);
        newCourse.setCourseTime(time);

        boolean success = courseDAO.createCourse(newCourse);
        
        if (success) {
            response.sendRedirect("CourseServlet?action=manage&msg=added");
        } else {
            response.sendRedirect("CourseServlet?action=manage&msg=error");
        }
    }
}