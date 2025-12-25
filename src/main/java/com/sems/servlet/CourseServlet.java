/**
 * @author maisarahabjalil
 * handles the display and management of the course data
 * addcourse jsp doGet to get list of courses
 * admincourse jsp doPost to add new course
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
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : "";

        // --- FETCH DATA FOR BOTH VIEWS ---
        List<Course> allCourses = courseDAO.getAllCourses();
        // Providing both names to ensure both Student and Admin JSPs work
        request.setAttribute("courses", allCourses);
        request.setAttribute("allCourses", allCourses);

        // --- SMART ROUTING ---
        // If action is 'manage' or user is admin, show admin page
        if ("manage".equals(action) || "admin".equalsIgnoreCase(role)) {
            request.getRequestDispatcher("/admin/admincourse.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/student/addcourse.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        // --- ADMIN REMOVE COURSE ROW --- 
        if ("DELETE".equals(action)) {
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            boolean isDeleted = courseDAO.deleteCourse(courseId);

            // Redirect with context path to refresh the list safely
            response.sendRedirect(request.getContextPath() + "/CourseServlet?action=manage&status=" + (isDeleted ? "deleted" : "error"));
            return;
        }

        // --- ADMIN FEATURE: Adding a brand new course ---
        // --- ADMIN FEATURE: Adding a brand new course ---
        if ("ADD".equals(action)) {
            String courseCode = request.getParameter("courseCode");
            String courseName = request.getParameter("courseName");
            int credits = Integer.parseInt(request.getParameter("credits"));
            int capacity = Integer.parseInt(request.getParameter("capacity"));
            String day = request.getParameter("courseDay");
            String time = request.getParameter("courseTime");

            // 1. Grab the NEW prerequisite ID from your form
            String prereqParam = request.getParameter("prerequisiteId");
            int prerequisiteId = (prereqParam != null && !prereqParam.isEmpty()) ? Integer.parseInt(prereqParam) : 0;

            Course newCourse = new Course();
            newCourse.setCourseCode(courseCode);
            newCourse.setCourseName(courseName);
            newCourse.setCredits(credits);
            newCourse.setCapacity(capacity);
            newCourse.setCourseDay(day);

            if (time != null && time.length() == 5) {
                newCourse.setCourseTime(time + ":00");
            } else {
                newCourse.setCourseTime(time);
            }

            // 2. Pass the prerequisiteId to a modified DAO method
            boolean success = courseDAO.createCourseWithPrereq(newCourse, prerequisiteId);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/CourseServlet?action=manage&msg=added");
            } else {
                response.sendRedirect(request.getContextPath() + "/CourseServlet?action=manage&msg=error");
            }
        }
    }
}
