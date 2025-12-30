/**
 *
 * @author maisarahabjalil
 * 
 *  doGet   -   grabs all the available courses 
 *  doPost  -   creates a new record in enrollments table
 *  
 *  moves students in and out of courses
 *  checks pre-req
 * 
 */

package com.sems.servlet;

import com.sems.dao.*;
import com.sems.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/EnrollmentServlet")
public class EnrollmentServlet extends HttpServlet {

    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final ActivityLogDAO logDAO = new ActivityLogDAO(); 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

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

        // Fetch Performer Name (The person logged in)
        Student performerProfile = studentDAO.getStudentByUserId(sessionUserId);
        String performerName = (performerProfile != null) ? performerProfile.getFirstName() : "User";

        String action = request.getParameter("action");
        String courseIdStr = request.getParameter("courseId");
        String courseCode = request.getParameter("courseCode");
        
        // finds which course we need
        int courseId = -1;
        if (courseIdStr != null) {
            courseId = Integer.parseInt(courseIdStr);
        } else if (courseCode != null) {
            courseId = courseDAO.getCourseIdByCode(courseCode);
        }

        if (courseId == -1) {
            response.sendRedirect(request.getContextPath() + "/student/MyCourseServlet?error=invalidCourse");
            return;
        }
        
        //checks if its admin or student
        int targetStudentId;
        if ("admin".equals(role) && request.getParameter("studentId") != null) {
            targetStudentId = Integer.parseInt(request.getParameter("studentId"));
        } else {
            targetStudentId = studentDAO.getStudentIdByUserId(sessionUserId);
        }

        boolean success = false;

        // --- ENROLL ACTION ---
        if ("enroll".equals(action)) {
            boolean canEnroll = enrollmentDAO.isPrerequisiteSatisfied(targetStudentId, courseId);

            if (!canEnroll) {
                // LOG: Prerequisite Failure
                logDAO.recordLog(sessionUserId, courseId, "ENROLL_FAIL", 
                    performerName + " attempted to enroll Student ID #" + targetStudentId + " but prerequisites were missing.");
                
                if ("admin".equals(role)) {
                    response.sendRedirect(request.getContextPath() + "/AdminManageStudentServlet?error=missing_prereq&studentId=" + targetStudentId);
                } else {
                    response.sendRedirect(request.getContextPath() + "/student/AddCourseServlet?error=missing_prereq");
                }
                return;
            }

            success = enrollmentDAO.adminEnrollStudentInCourse(targetStudentId, courseId);
            if (success) {
                // LOG: Successful Enrollment
                String logAction = "admin".equals(role) ? "ADMIN_ENROLL" : "STUDENT_ENROLL";
                logDAO.recordLog(sessionUserId, courseId, logAction, 
                    performerName + " enrolled Student #" + targetStudentId + " in Course #" + courseId);
            }

        // --- DROP ACTION ---
        } else if ("drop".equals(action)) {
            success = enrollmentDAO.adminDropStudentFromCourse(targetStudentId, courseId);
            if (success) {
                // LOG: Successful Drop
                String logAction = "admin".equals(role) ? "ADMIN_DROP" : "STUDENT_DROP";
                logDAO.recordLog(sessionUserId, courseId, logAction, 
                    performerName + " dropped Student #" + targetStudentId + " from Course #" + courseId);
            }
        }

        // Redirect Logic
        if ("admin".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/AdminManageStudentServlet?success=" + success);
        } else {
            if ("drop".equals(action)) {
                response.sendRedirect(request.getContextPath() + "/student/MyCourseServlet?success=" + success);
            } else {
                response.sendRedirect(request.getContextPath() + "/student/AddCourseServlet?success=" + success);
            }
        }
    }
}