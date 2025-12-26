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

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int targetStudentId = -1;

        // 1. Resolve Target Student ID
        if ("admin".equals(role)) {
            String searchInput = request.getParameter("studentId");

            // If admin just clicked the link but hasn't searched yet
            if (searchInput == null || searchInput.isEmpty()) {
                request.getRequestDispatcher("/admin/admingrade.jsp").forward(request, response);
                return;
            }

            try {
                // Try to parse as ID first
                targetStudentId = Integer.parseInt(searchInput);
            } catch (NumberFormatException e) {
                // If it's not a number, it might be a name. 
                // You can add studentDAO.getIdByName(searchInput) here later!
                response.sendRedirect(request.getContextPath() + "/GradeServlet?error=invalidId");
                return;
            }
        } else {
            // Student vision: get their own ID
            targetStudentId = studentDAO.getStudentIdByUserId(userId);
        }

        // 2. Fetch Data
        Student s = studentDAO.getStudentById(targetStudentId);
        if (s == null) {
            response.sendRedirect(request.getContextPath() + "/GradeServlet?error=notFound");
            return;
        }

        // Fresh math for CGPA and Transcript
        //checks if its admin or student
        // 1. Always update math first
        double currentCGPA = enrollmentDAO.updateAndGetStudentCGPA(targetStudentId);

// 2. Logic to decide which DAO method to use
        List<Enrollment> transcript;
        if ("admin".equals(role)) {
            // Admin needs to see EVERYTHING (including N/A) to grade them
            transcript = enrollmentDAO.getAdminTranscript(targetStudentId);
        } else {
            // Students only see completed subjects (Report Card view)
            transcript = enrollmentDAO.getFullTranscript(targetStudentId);
        }

// 3. Set Attributes as usual
        request.setAttribute("student", s);
        request.setAttribute("transcript", transcript);
        request.setAttribute("cgpa", currentCGPA);

        // 3. Set Attributes
        request.setAttribute("student", s);
        request.setAttribute("transcript", transcript);
        request.setAttribute("cgpa", currentCGPA);

        // 4. SMART REDIRECTION
        if ("admin".equals(role)) {
            // Admins go to the management console (admingrade.jsp)
            request.getRequestDispatcher("/admin/admingrade.jsp").forward(request, response);
        } else {
            // Students go to the read-only view
            request.getRequestDispatcher("/student/viewgrades.jsp").forward(request, response);
        }
    }
}
