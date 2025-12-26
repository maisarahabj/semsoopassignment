/**
 *
 * @author maisarahabjalil
 * ADMIN VIEW: updates/saves/edit grade  from results
 *
 */
package com.sems.servlet;

import com.sems.dao.EnrollmentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

@WebServlet("/UpdateGradeServlet")
public class UpdateGradeServlet extends HttpServlet {

    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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

            // We only care about the select dropdowns which are named "grade_ID"
            if (paramName.startsWith("grade_")) {
                int enrollmentId = Integer.parseInt(paramName.split("_")[1]);
                String newGrade = request.getParameter(paramName);

                // Update the database for each subject
                enrollmentDAO.updateGrade(enrollmentId, newGrade);
            }
        }

        // Recalculate CGPA so the profile is immediately updated
        enrollmentDAO.updateAndGetStudentCGPA(studentId);

        // Redirect back to the view with a success message
        response.sendRedirect(request.getContextPath() + "/GradeServlet?studentId=" + studentId + "&success=true");
    }
}
