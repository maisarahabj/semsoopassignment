/**
 *
 * @author maisarahabjalil
 */
package com.sems.servlet;

import com.sems.dao.EnrollmentDAO;
import com.sems.dao.StudentDAO;
import com.sems.model.Enrollment;
import com.sems.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/GradeServlet")
public class GradeServlet extends HttpServlet {

    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private StudentDAO studentDAO = new StudentDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String role = (String) session.getAttribute("role");
        Integer userId = (Integer) session.getAttribute("userId");

        int targetStudentId;

        // ADMIN VISION: They can search for a student
        if ("admin".equals(role)) {
            String searchId = request.getParameter("studentId");
            if (searchId == null || searchId.isEmpty()) {
                // If no student selected yet, show a search page or list
                request.getRequestDispatcher("/admin/adminGradesSearch.jsp").forward(request, response);
                return;
            }
            targetStudentId = Integer.parseInt(searchId);
        } // STUDENT VISION: They only see their own
        else {
            targetStudentId = studentDAO.getStudentIdByUserId(userId);
        }

        Student s = studentDAO.getStudentById(targetStudentId);
        request.setAttribute("student", s);

        // 1. Calculate/Update CGPA so it's fresh
        double currentCGPA = enrollmentDAO.updateAndGetStudentCGPA(targetStudentId);

        // 2. Fetch all grades for the transcript table
        List<Enrollment> transcript = enrollmentDAO.getFullTranscript(targetStudentId);

        // 3. Send to JSP
        request.setAttribute("transcript", transcript);
        request.setAttribute("cgpa", currentCGPA);
        request.setAttribute("studentId", targetStudentId);

        request.getRequestDispatcher("/student/viewgrades.jsp").forward(request, response);
    }
}
