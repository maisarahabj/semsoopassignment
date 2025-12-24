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

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String role = (String) session.getAttribute("role");
        Integer sessionUserId = (Integer) session.getAttribute("userId");

        if (sessionUserId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String courseIdStr = request.getParameter("courseId");
        String courseCode = request.getParameter("courseCode");

        int courseId = -1;

        // Determine courseId either from ID or Code
        if (courseIdStr != null) {
            courseId = Integer.parseInt(courseIdStr);
        } else if (courseCode != null) {
            courseId = courseDAO.getCourseIdByCode(courseCode);
        }

        if (courseId == -1) {
            response.sendRedirect(request.getContextPath() + "/student/MyCourseServlet?error=invalidCourse");
            return;
        }

        // Identify Student
        int targetStudentId;
        if ("admin".equals(role) && request.getParameter("studentId") != null) {
            targetStudentId = Integer.parseInt(request.getParameter("studentId"));
        } else {
            // For students, get their specific Student ID from their User ID
            targetStudentId = studentDAO.getStudentIdByUserId(sessionUserId);
        }

        boolean success = false;

        if ("enroll".equals(action)) {
            // Using your existing adminEnroll method because it handles the transaction (Enroll + Increment)
            success = enrollmentDAO.adminEnrollStudentInCourse(targetStudentId, courseId);
        } else if ("drop".equals(action)) {
            // Using your existing adminDrop method because it handles the transaction (Drop + Decrement)
            success = enrollmentDAO.adminDropStudentFromCourse(targetStudentId, courseId);
        }

        // Smart Redirect based on role and action
        if ("admin".equals(role)) {
            response.sendRedirect("adminstudent.jsp?success=" + success);
        } else {
            if ("drop".equals(action)) {
                // Go back to classes list if they dropped a course
                response.sendRedirect(request.getContextPath() + "/student/MyCourseServlet?success=" + success);
            } else {
                // Go back to add subjects if they were trying to enroll
                response.sendRedirect(request.getContextPath() + "/student/AddCourseServlet?success=" + success);
            }
        }
    }
}
