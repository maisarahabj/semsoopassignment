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
import java.io.PrintWriter;

@WebServlet("/AdminGetStudentCoursesServlet")
public class AdminGetStudentCoursesServlet extends HttpServlet {

    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    // 1. Fetching the list for the modal
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int studentId = Integer.parseInt(request.getParameter("studentId"));
        List<Course> courses = enrollmentDAO.getEnrolledCourseDetails(studentId);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if (courses == null || courses.isEmpty()) {
            out.print("<tr><td colspan='4' style='text-align:center;'>No courses found.</td></tr>");
            return;
        }

        for (Course c : courses) {
            // EXACT LOGIC FROM YOUR ADMINCOURSE.JSP
            String day = c.getCourseDay();
            String time = "TBA";

            // Safety check to prevent StringIndexOutOfBoundsException
            if (c.getCourseTime() != null && c.getCourseTime().length() >= 5) {
                time = c.getCourseTime().substring(0, 5);
            }

            out.println("<tr>");
            out.println("  <td><strong>" + c.getCourseCode() + "</strong></td>");
            out.println("  <td>" + c.getCourseName() + "</td>");
            out.println("  <td style='color: #64748b;'>" + (day != null ? day : "TBA") + " " + time + "</td>");
            out.println("  <td style='text-align: right;'>");
            out.println("    <button type='button' class='btn-drop-mini' onclick='dropCourseAction(" + c.getCourseId() + ")'>");
            out.println("      <i class='fas fa-trash-alt'></i>");
            out.println("    </button>");
            out.println("  </td>");
            out.println("</tr>");
        }
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
