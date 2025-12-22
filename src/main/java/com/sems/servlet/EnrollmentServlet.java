/**
 *
 * @author maisarahabjalil
 * 
 * REGISTERING FOR A COURSE
 * 
 * student view: can enroll/drop themselves in a course
 * admin view: can enroll/drop a student in a course
 * 
 */
package com.sems.servlet;

import com.sems.dao.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/EnrollmentServlet")
public class EnrollmentServlet extends HttpServlet {
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private StudentDAO studentDAO = new StudentDAO();
    private CourseDAO courseDAO = new CourseDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String role = (String) session.getAttribute("role");
        Integer sessionUserId = (Integer) session.getAttribute("userId");

        if (sessionUserId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action"); 
        String courseCode = request.getParameter("courseCode"); // User inputs "CS01"
        
        // 1. TRANSLATION: Find the ID for the provided Code
        int courseId = courseDAO.getCourseIdByCode(courseCode);
        
        if (courseId == -1) {
            // Error handling if the course code doesn't exist
            response.sendRedirect("dashboard.jsp?error=invalidCourse");
            return;
        }

        // 2. IDENTIFY STUDENT
        int targetStudentId;
        if ("admin".equals(role)) {
            targetStudentId = Integer.parseInt(request.getParameter("studentId"));
        } else {
            targetStudentId = studentDAO.getStudentIdByUserId(sessionUserId);
        }

        boolean success = false;
        
        // 3. EXECUTE ENROLLMENT
        if ("enroll".equals(action)) {
            success = enrollmentDAO.enrollStudent(targetStudentId, courseId);
            if (success) {
                courseDAO.incrementEnrolledCount(courseId);
            }
        } else if ("drop".equals(action)) {
            success = enrollmentDAO.dropCourse(targetStudentId, courseId);
            if (success) {
                courseDAO.decrementEnrolledCount(courseId);
            }
        }

        String redirect = "admin".equals(role) ? "adminstudent.jsp" : "addcourse.jsp";
        response.sendRedirect(redirect + "?success=" + success);
    }
}