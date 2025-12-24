/**
 *
 * @author maisarahabjalil
 */

package com.sems.servlet;

import com.sems.dao.CourseDAO;
import com.sems.dao.EnrollmentDAO;
import com.sems.dao.StudentDAO;
import com.sems.model.Course;
import com.sems.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set; // Added missing import

@WebServlet("/student/AddCourseServlet")
public class AddCourseServlet extends HttpServlet {
    
    private CourseDAO courseDAO = new CourseDAO();
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // 1. Get Student Profile
            Student student = studentDAO.getStudentByUserId(userId);
            
            if (student != null) {
                // 2. Get ALL courses from the database
                List<Course> allCourses = courseDAO.getAllCourses();

                // 3. Get IDs of courses the student is ALREADY in
                List<Course> enrolled = enrollmentDAO.getEnrolledCourseDetails(student.getStudentId());
                Set<Integer> enrolledIds = new HashSet<>();
                for (Course c : enrolled) {
                    enrolledIds.add(c.getCourseId());
                }

                // 4. Send everything to the JSP
                request.setAttribute("student", student);
                request.setAttribute("allCourses", allCourses);
                request.setAttribute("enrolledIds", enrolledIds);

                request.getRequestDispatcher("/student/addcourse.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=noProfile");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}