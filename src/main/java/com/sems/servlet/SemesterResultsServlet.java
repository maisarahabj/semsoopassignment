package com.sems.servlet;

import com.sems.dao.*;
import com.sems.model.*;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet for students to view their results by semester
 * @author SEMS Team
 */
@WebServlet("/student/SemesterResultsServlet")
public class SemesterResultsServlet extends HttpServlet {

    private SemesterDAO semesterDAO;
    private StudentDAO studentDAO;
    private EnrollmentDAO enrollmentDAO;

    @Override
    public void init() {
        semesterDAO = new SemesterDAO();
        studentDAO = new StudentDAO();
        enrollmentDAO = new EnrollmentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"student".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        Student student = studentDAO.getStudentByUserId(userId);
        
        if (student == null) {
            response.sendRedirect(request.getContextPath() + "/student/create_profile.jsp");
            return;
        }

        // Get all semesters (to show in dropdown)
        List<Semester> allSemesters = semesterDAO.getAllSemesters();
        
        // Get selected semester (if any)
        String semesterIdParam = request.getParameter("semesterId");
        Semester selectedSemester = null;
        List<Enrollment> enrollments = null;
        Double semesterGPA = null;
        
        if (semesterIdParam != null && !semesterIdParam.isEmpty()) {
            try {
                int semesterId = Integer.parseInt(semesterIdParam);
                selectedSemester = semesterDAO.getSemesterById(semesterId);
                
                // Get enrollments for this student in this semester
                enrollments = enrollmentDAO.getEnrollmentsBySemester(student.getStudentId(), semesterId);
                
                // Calculate semester GPA
                semesterGPA = enrollmentDAO.calculateSemesterGPA(student.getStudentId(), semesterId);
            } catch (NumberFormatException e) {
                // Invalid semester ID
            }
        } else {
            // Default to active semester
            selectedSemester = semesterDAO.getActiveSemester();
            if (selectedSemester != null) {
                enrollments = enrollmentDAO.getEnrollmentsBySemester(
                    student.getStudentId(), 
                    selectedSemester.getSemesterId()
                );
                semesterGPA = enrollmentDAO.calculateSemesterGPA(
                    student.getStudentId(), 
                    selectedSemester.getSemesterId()
                );
            }
        }
        
        request.setAttribute("student", student);
        request.setAttribute("allSemesters", allSemesters);
        request.setAttribute("selectedSemester", selectedSemester);
        request.setAttribute("enrollments", enrollments);
        request.setAttribute("semesterGPA", semesterGPA);
        
        request.getRequestDispatcher("/student/semesterresults.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
