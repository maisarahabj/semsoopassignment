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
import com.sems.dao.ActivityLogDAO;
import com.sems.dao.StudentDAO;
import com.sems.model.Student;
import com.sems.model.Course;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/CourseServlet")
public class CourseServlet extends HttpServlet {

    private CourseDAO courseDAO = new CourseDAO();
    private final ActivityLogDAO logDAO = new ActivityLogDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    // grabs course list for every1 to just view
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : "";

        List<Course> allCourses = courseDAO.getAllCourses();
        request.setAttribute("courses", allCourses);
        request.setAttribute("allCourses", allCourses);

        if ("manage".equals(action) || "admin".equalsIgnoreCase(role)) {
            request.getRequestDispatcher("/admin/admincourse.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/student/addcourse.jsp").forward(request, response);
        }
    }

    // remove and create course - links pre-req as well
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer adminUserId = (Integer) session.getAttribute("userId");
        String action = request.getParameter("action");

        // Fetch Admin Name for a better log description
        Student adminProfile = studentDAO.getStudentByUserId(adminUserId);
        String adminName = (adminProfile != null) ? adminProfile.getFirstName() : "Admin";

        // --- ADMIN REMOVE COURSE --- 
        if ("DELETE".equals(action)) {
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            boolean isDeleted = courseDAO.deleteCourse(courseId);

            if (isDeleted) {
                // LOG: Course Deletion
                logDAO.recordLog(adminUserId, courseId, "ADMIN_DELETE_COURSE",
                        "Admin " + adminName + " permanently deleted Course ID #" + courseId);
            }

            response.sendRedirect(request.getContextPath() + "/CourseServlet?action=manage&status=" + (isDeleted ? "deleted" : "error"));
            return;
        }

        // --- ADMIN ADD COURSE ---
        if ("ADD".equals(action)) {
            String courseCode = request.getParameter("courseCode");
            String courseName = request.getParameter("courseName");
            int credits = Integer.parseInt(request.getParameter("credits"));
            int capacity = Integer.parseInt(request.getParameter("capacity"));
            String day = request.getParameter("courseDay");
            String time = request.getParameter("courseTime");

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

            boolean success = courseDAO.createCourseWithPrereq(newCourse, prerequisiteId);

            if (success) {
                // LOG: Course Creation
                logDAO.recordLog(adminUserId, null, "ADMIN_ADD_COURSE",
                        "Admin " + adminName + " created new course: " + courseCode + " - " + courseName);

                response.sendRedirect(request.getContextPath() + "/CourseServlet?action=manage&msg=added");
            } else {
                response.sendRedirect(request.getContextPath() + "/CourseServlet?action=manage&msg=error");
            }
        }
    }
}
