/**
 *
 * @author maisarahabjalil
 */
package com.sems.servlet;

import com.sems.dao.StudentDAO;
import com.sems.dao.StudentDAO;
import com.sems.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/AdminManageStudentServlet")
public class AdminManageStudentServlet extends HttpServlet {

    private StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Fetch all students using your existing DAO logic
        List<Student> studentList = studentDAO.getAllStudents();
        request.setAttribute("studentList", studentList);

        request.getRequestDispatcher("/admin/adminstudent.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("ADD_MANUAL".equals(action)) {
            // Extract Account Data
            String username = request.getParameter("username");
            String pass = request.getParameter("password");
            int manualId = Integer.parseInt(request.getParameter("studentId"));

            // Extract Profile Data
            Student s = new Student();
            s.setStudentId(manualId);
            s.setFirstName(request.getParameter("firstName"));
            s.setLastName(request.getParameter("lastName"));
            s.setEmail(request.getParameter("email"));
            s.setPhone(request.getParameter("phone"));
            s.setAddress(request.getParameter("address"));

            // Convert String date to SQL Date
            try {
                java.sql.Date dob = java.sql.Date.valueOf(request.getParameter("dob"));
                s.setDob(dob);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Call the new transactional method
            boolean success = studentDAO.createStudentManually(s, username, pass);

            if (success) {
                // Success! We can add a message attribute if we want
            }
        }

        if ("DELETE".equals(action)) {
            String studentIdStr = request.getParameter("studentId");
            String userIdStr = request.getParameter("userId"); // Captured from the hidden input in JSP

            if (studentIdStr != null && !studentIdStr.isEmpty() && userIdStr != null && !userIdStr.isEmpty()) {
                try {
                    int sId = Integer.parseInt(studentIdStr);
                    int uId = Integer.parseInt(userIdStr);

                    // Call the double-delete method in your DAO
                    studentDAO.deleteStudent(sId, uId);

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        // Redirect refreshes the list so the UI stays in sync
        response.sendRedirect(request.getContextPath() + "/AdminManageStudentServlet");
    }
}
