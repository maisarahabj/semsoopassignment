/**
 *
 * plays w adminstudent.jsp - handles adding/dropping a student's course enrollment
 *
 *  doGet   -   shows a specific student's current schedule (Student Profile)
 *          -   shows which student is in a class (View Button)
 *
 *  doPost  -   ENROLL: allows admin to enroll a student w/ pre-req check
 *              DROP: allows admin to remove a student from a course
 *
 * @author maisarahabjalil
 */
package com.sems.servlet;

import com.sems.dao.ActivityLogDAO;
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

    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final ActivityLogDAO logDAO = new ActivityLogDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ... (Your doGet logic is already correct for fetching lists) ...
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String courseIdStr = request.getParameter("courseId");
        String studentIdStr = request.getParameter("studentId");

        if (courseIdStr != null) {
            int courseId = Integer.parseInt(courseIdStr);
            List<Student> students = studentDAO.getStudentsByCourseId(courseId);
            if (students == null || students.isEmpty()) {
                out.print("<tr><td colspan='2' style='text-align:center;'>No students enrolled yet.</td></tr>");
            } else {
                for (Student s : students) {
                    out.println("<tr><td>#" + s.getStudentId() + "</td><td>" + s.getFirstName() + " " + s.getLastName() + "</td></tr>");
                }
            }
        } else if (studentIdStr != null) {
            int studentId = Integer.parseInt(studentIdStr);
            List<Course> courses = enrollmentDAO.getEnrolledCourseDetails(studentId);
            if (courses == null || courses.isEmpty()) {
                out.print("<tr><td colspan='4' style='text-align:center;'>No courses found.</td></tr>");
                return;
            }
            for (Course c : courses) {
                String day = c.getCourseDay();
                String time = (c.getCourseTime() != null && c.getCourseTime().length() >= 5) ? c.getCourseTime().substring(0, 5) : "TBA";
                out.println("<tr><td><strong>" + c.getCourseCode() + "</strong></td><td>" + c.getCourseName() + "</td>");
                out.println("<td style='color: #64748b;'>" + (day != null ? day : "TBA") + " " + time + "</td>");
                out.println("<td style='text-align: right;'><button type='button' class='btn-drop-mini' onclick='dropCourseAction(" + c.getCourseId() + ")'><i class='fas fa-trash-alt'></i></button></td></tr>");
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer adminUserId = (Integer) session.getAttribute("userId");

        if (adminUserId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // --- NEW: Fetch the Admin's Name Dynamically ---
        Student adminProfile = studentDAO.getStudentByUserId(adminUserId);
        String adminName = (adminProfile != null) ? adminProfile.getFirstName() : "Admin";

        String action = request.getParameter("action");
        int studentId = Integer.parseInt(request.getParameter("studentId"));
        int courseId = Integer.parseInt(request.getParameter("courseId"));

        if ("ENROLL".equals(action)) {
            boolean canEnroll = enrollmentDAO.isPrerequisiteSatisfied(studentId, courseId);

            if (!canEnroll) {
                logDAO.recordLog(adminUserId, studentId, "ENROLL_FAIL",
                        "Admin " + adminName + " attempt blocked: Student #" + studentId + " missing prereqs for course #" + courseId);
                response.getWriter().print("prereq_missing");
                return;
            }

            boolean success = enrollmentDAO.adminEnrollStudentInCourse(studentId, courseId);
            if (success) {
                logDAO.recordLog(adminUserId, studentId, "ADMIN_ENROLL",
                        "Admin " + adminName + " manually enrolled Student #" + studentId + " into Course #" + courseId);
            }
            response.getWriter().print(success ? "success" : "error");

        } else if ("DROP".equals(action)) {
            boolean success = enrollmentDAO.adminDropStudentFromCourse(studentId, courseId);
            if (success) {
                logDAO.recordLog(adminUserId, studentId, "ADMIN_DROP",
                        "Admin " + adminName + " dropped Student #" + studentId + " from Course #" + courseId);
            }
            response.getWriter().print(success ? "success" : "error");
        }
    }
}
