/**
 *
 * @author maisarahabjalil
 */
package com.sems.servlet;

import com.sems.dao.EnrollmentDAO;
import com.sems.dao.StudentDAO;
import com.sems.model.Course;
import com.sems.model.Student;
import com.sems.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/student/MyCourseServlet")
public class MyCourseServlet extends HttpServlet {

    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        StudentDAO sDao = new StudentDAO();
        EnrollmentDAO eDao = new EnrollmentDAO();

// 1. Get student profile for the sidebars
        Student student = sDao.getStudentByUserId(userId);

// 2. Get the enrolled courses for the table
        List<Course> enrolledCourses = eDao.getEnrolledCourseDetails(student.getStudentId());

// 3. Send to JSP
        request.setAttribute("student", student);
        request.setAttribute("enrolledCourses", enrolledCourses);
        request.getRequestDispatcher("/student/mycourse.jsp").forward(request, response);
    }
}
