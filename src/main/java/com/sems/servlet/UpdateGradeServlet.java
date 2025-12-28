/**
 *
 * @author maisarahabjalil
 * ADMIN VIEW: updates/saves/edit grade  from results
 *
 */
package com.sems.servlet;

import com.sems.dao.EnrollmentDAO;
import com.sems.dao.ActivityLogDAO; 
import com.sems.dao.StudentDAO;     
import com.sems.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Enumeration;

@WebServlet("/UpdateGradeServlet")
public class UpdateGradeServlet extends HttpServlet {

    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final ActivityLogDAO logDAO = new ActivityLogDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer adminUserId = (Integer) session.getAttribute("userId");

        // Security Check: Ensure admin is logged in
        if (adminUserId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Fetch Admin Name for the log
        Student adminProfile = studentDAO.getStudentByUserId(adminUserId);
        String adminName = (adminProfile != null) ? adminProfile.getFirstName() : "Admin";

        String studentIdStr = request.getParameter("studentId");
        if (studentIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/GradeServlet");
            return;
        }

        int studentId = Integer.parseInt(studentIdStr);

        // Loop through all parameters sent by the form
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();

            if (paramName.startsWith("grade_")) {
                int enrollmentId = Integer.parseInt(paramName.split("_")[1]);
                String newGrade = request.getParameter(paramName);

                // 1. Update the database
                boolean success = enrollmentDAO.updateGrade(enrollmentId, newGrade);

                // 2. TRIGGER LOG: Log each grade change specifically
                if (success && !newGrade.equals("N/A")) {
                    logDAO.recordLog(
                            adminUserId,
                            studentId,
                            "GRADE_UPDATE",
                            "Admin " + adminName + " updated grade for Enrollment ID #" + enrollmentId + " to [" + newGrade + "] for Student #" + studentId
                    );
                }
            }
        }

        // Recalculate CGPA so the profile is immediately updated
        enrollmentDAO.updateAndGetStudentCGPA(studentId);

        // Redirect back
        response.sendRedirect(request.getContextPath() + "/GradeServlet?studentId=" + studentId + "&success=true");
    }
}
