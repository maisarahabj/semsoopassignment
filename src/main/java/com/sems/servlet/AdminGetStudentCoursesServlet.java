/**
 *
 * plays w adminstudent.jsp - handles adding/dropping a student's course enrollment
 *
 * @author maisarahabjalil
 */
package com.sems.servlet;

import com.sems.dao.EnrollmentDAO;
import com.sems.dao.StudentDAO;
import com.sems.model.Course;
import com.sems.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.io.PrintWriter;

@WebServlet("/AdminGetStudentCoursesServlet")
public class AdminGetStudentCoursesServlet extends HttpServlet {

    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private StudentDAO studentDAO = new StudentDAO();

    // 1. Fetching the list for the modal
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String courseIdStr = request.getParameter("courseId");
        String studentIdStr = request.getParameter("studentId");

        // --- "View List" ---
        if (courseIdStr != null) {
            int courseId = Integer.parseInt(courseIdStr);
            List<Student> students = studentDAO.getStudentsByCourseId(courseId);

            if (students == null || students.isEmpty()) {
                out.print("<tr><td colspan='2' style='text-align:center;'>No students enrolled yet.</td></tr>");
            } else {
                for (Student s : students) {
                    out.println("<tr>");
                    out.println("  <td>#" + s.getStudentId() + "</td>");
                    out.println("  <td>" + s.getFirstName() + " " + s.getLastName() + "</td>");
                    out.println("</tr>");
                }
            }
        } // --- "View Courses" ---
        else if (studentIdStr != null) {
            int studentId = Integer.parseInt(studentIdStr);
            List<Course> courses = enrollmentDAO.getEnrolledCourseDetails(studentId);

            if (courses == null || courses.isEmpty()) {
                out.print("<tr><td colspan='4' style='text-align:center;'>No courses found.</td></tr>");
                return;
            }

            for (Course c : courses) {
                String day = c.getCourseDay();
                String time = "TBA";
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
    }

    // 2. Handling the "Drop" or "Enroll" action
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        int studentId = Integer.parseInt(request.getParameter("studentId"));
        int courseId = Integer.parseInt(request.getParameter("courseId"));

        if ("ENROLL".equals(action)) {
            // Run the prerequisite check before allowing enrollment
            boolean canEnroll = enrollmentDAO.isPrerequisiteSatisfied(studentId, courseId);

            if (!canEnroll) {
                // Send back a specific message so the JS knows it's a prereq issue
                response.getWriter().print("prereq_missing");
                return;
            }

            boolean success = enrollmentDAO.adminEnrollStudentInCourse(studentId, courseId);
            response.getWriter().print(success ? "success" : "error");

        } else if ("DROP".equals(action)) {
            boolean success = enrollmentDAO.adminDropStudentFromCourse(studentId, courseId);
            response.getWriter().print(success ? "success" : "error");
        }
    }
}
