/**
 *
 * @author maisarahabjalil
 */
package com.sems.servlet;

import com.sems.dao.EnrollmentDAO;
import com.sems.dao.CourseDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet; // Fixes the 'cannot find symbol' error
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/EnrollmentServlet")
public class EnrollmentServlet extends HttpServlet {

    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private CourseDAO courseDAO = new CourseDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer studentId = (Integer) session.getAttribute("studentId");
        
        // Use a try-catch for parsing to prevent crashes if courseId is missing
        try {
            int courseId = Integer.parseInt(request.getParameter("courseId"));

            if (studentId == null) {
                response.sendRedirect("login.jsp?error=session_expired");
                return;
            }

            // STEP 1: Attempt enrollment
            boolean enrollmentSuccess = enrollmentDAO.enrollStudent(studentId, courseId);

            if (enrollmentSuccess) {
                // STEP 2: Only increment if enrollment was successful
                courseDAO.incrementEnrolledCount(courseId);
                response.sendRedirect("dashboard?success=enrolled");
            } else {
                response.sendRedirect("dashboard?error=already_enrolled");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("dashboard?error=invalid_course");
        }
    }
}