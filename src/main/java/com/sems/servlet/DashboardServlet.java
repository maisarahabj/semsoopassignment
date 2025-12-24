package com.sems.servlet;

import com.sems.dao.*;
import com.sems.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {

    private StudentDAO studentDAO = new StudentDAO();
    private CourseDAO courseDAO = new CourseDAO();
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        // 1. Session Check
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 2. Role-Based Logic
        if ("admin".equalsIgnoreCase(role)) {
            // --- ADMIN LOGIC: Get Today's Classes ---
            String currentDay = LocalDate.now()
                    .getDayOfWeek()
                    .getDisplayName(TextStyle.FULL, Locale.ENGLISH); // Result: "Wednesday"

            // Fetch the list using the day name string
            List<Course> todayClasses = courseDAO.getTodayCourses(currentDay);

            int activeStudents = userDAO.getActiveStudentCount();
            // Set the attribute so admindash.jsp can see it
            request.setAttribute("todayClasses", todayClasses);
            request.setAttribute("activeStudentCount", activeStudents);
            // Forward to Admin Dashboard
            request.getRequestDispatcher("/admin/admindash.jsp").forward(request, response);

        } else if ("student".equalsIgnoreCase(role)) {
            // --- STUDENT LOGIC ---
            Student student = studentDAO.getStudentByUserId(userId);

            if (student != null) {
                List<Course> enrolledCourses = courseDAO.getCoursesByStudentId(student.getStudentId());
                request.setAttribute("student", student);
                request.setAttribute("enrolledCourses", enrolledCourses);

                request.getRequestDispatcher("/student/dashboard.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/student/create_profile.jsp");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }

        // IMPORTANT: Removed the code at the bottom because forwarding is already handled above!
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
