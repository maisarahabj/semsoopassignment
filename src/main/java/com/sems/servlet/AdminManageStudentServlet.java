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
            String idStr = request.getParameter("studentId");
            if (idStr != null && !idStr.isEmpty()) {
                try {
                    int id = Integer.parseInt(idStr);
                    studentDAO.deleteStudent(id);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        // This refreshes the page so the deleted student disappears from the table
        response.sendRedirect(request.getContextPath() + "/AdminManageStudentServlet");
    }
}
