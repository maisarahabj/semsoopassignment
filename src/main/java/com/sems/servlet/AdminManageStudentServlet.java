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
