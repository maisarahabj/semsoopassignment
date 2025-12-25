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

    // Handle the "Drop" clicks from My Classes (GET requests)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    // Handle Enrollment submissions (POST requests)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    //processing all enrollment-related actions - Enroll/Drop
    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Session & Authentication Check
        HttpSession session = request.getSession();
        String role = (String) session.getAttribute("role");
        Integer sessionUserId = (Integer) session.getAttribute("userId");

        if (sessionUserId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 2. Extract Request Parameters
        String action = request.getParameter("action");
        String courseIdStr = request.getParameter("courseId");
        String courseCode = request.getParameter("courseCode");

        // 3. Resolve Course Identity
        int courseId = -1;
        if (courseIdStr != null) {
            courseId = Integer.parseInt(courseIdStr);
        } else if (courseCode != null) {
            courseId = courseDAO.getCourseIdByCode(courseCode);
        }

        // Validation: If no valid course is found, exit early
        if (courseId == -1) {
            response.sendRedirect(request.getContextPath() + "/student/MyCourseServlet?error=invalidCourse");
            return;
        }

        // 4. Resolve Target Student Identity
        int targetStudentId;
        if ("admin".equals(role) && request.getParameter("studentId") != null) {
            // Admin can perform actions on behalf of any student
            targetStudentId = Integer.parseInt(request.getParameter("studentId"));
        } else {
            // Students can only perform actions on their own record
            targetStudentId = studentDAO.getStudentIdByUserId(sessionUserId);
        }

        boolean success = false;

        // 5. Action Logic Execution
        if ("enroll".equals(action)) {

            // --- SECURITY GATE: Prerequisite Check ---
            // Verifies if the student has passed required foundational courses with A, B, or C.
            boolean canEnroll = enrollmentDAO.isPrerequisiteSatisfied(targetStudentId, courseId);

            if (!canEnroll) {
                // If prerequisite check fails, redirect back with error code
                response.sendRedirect(request.getContextPath() + "/student/AddCourseServlet?error=missing_prereq");
                return;
            }

            // If check passes, execute enrollment transaction (Insert + Increment Count)
            success = enrollmentDAO.adminEnrollStudentInCourse(targetStudentId, courseId);

        } else if ("drop".equals(action)) {
            // Execute drop transaction (Delete + Decrement Count)
            success = enrollmentDAO.adminDropStudentFromCourse(targetStudentId, courseId);
        }

        // 6. Navigation Management (Role-based redirection)
        if ("admin".equals(role)) {
            // Admins stay on the student management page
            response.sendRedirect("adminstudent.jsp?success=" + success);
        } else {
            // Students go back to the relevant view depending on the action performed
            if ("drop".equals(action)) {
                response.sendRedirect(request.getContextPath() + "/student/MyCourseServlet?success=" + success);
            } else {
                response.sendRedirect(request.getContextPath() + "/student/AddCourseServlet?success=" + success);
            }
        }
    }
}
