/**
 *
 * plays w adminstudent.jsp - handles adding/dropping a student's course enrollment
 *
 * @author maisarahabjalil
 */
package com.sems.servlet;

import com.sems.dao.EnrollmentDAO;
import com.sems.model.Course;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/AdminGetStudentCoursesServlet")
public class AdminGetStudentCoursesServlet extends HttpServlet {

    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    // 1. Fetching the list for the modal
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int studentId = Integer.parseInt(request.getParameter("studentId"));
        List<Course> courses = enrollmentDAO.getEnrolledCourseDetails(studentId);

        response.setContentType("application/json");
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            // Simplified formatting to ensure it's a clean JSON string
            json.append(String.format("{\"id\":%d, \"code\":\"%s\", \"name\":\"%s\", \"day\":\"%s\", \"time\":\"%s\"}",
                    c.getCourseId(),
                    c.getCourseCode(),
                    c.getCourseName(),
                    c.getCourseDay(),
                    (c.getCourseTime() != null && c.getCourseTime().length() > 5 ? c.getCourseTime().substring(0, 5) : c.getCourseTime())
            ));
            if (i < courses.size() - 1) {
                json.append(",");
            }

        }
        json.append("]");
        response.getWriter().print(json.toString());
    }

    // 2. Handling the "Drop" or "Enroll" action
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        int studentId = Integer.parseInt(request.getParameter("studentId"));
        int courseId = Integer.parseInt(request.getParameter("courseId"));

        boolean success = false;
        if ("DROP".equals(action)) {
            success = enrollmentDAO.adminDropStudentFromCourse(studentId, courseId);
        } else if ("ENROLL".equals(action)) {
            success = enrollmentDAO.adminEnrollStudentInCourse(studentId, courseId);
        }

        response.getWriter().print(success ? "success" : "error");
    }
}
