
/**
 *
 * @author maisarahabjalil
 */
package com.sems.servlet;

import com.sems.dao.StudentDAO;
import com.sems.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;

@WebServlet("/ProfileServlet")
public class ProfileServlet extends HttpServlet {
    private StudentDAO studentDAO = new StudentDAO();

    // 1. VIEW PROFILE (GET)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId != null) {
            Student student = studentDAO.getStudentByUserId(userId);
            request.setAttribute("student", student);
            request.getRequestDispatcher("/viewprofile.jsp").forward(request, response);
        } else {
            response.sendRedirect("login.jsp");
        }
    }

    // 2. EDIT PROFILE (POST)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int studentId = Integer.parseInt(request.getParameter("studentId"));
        String fName = request.getParameter("firstName");
        String lName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        Date dob = Date.valueOf(request.getParameter("dob"));

        // Create student object with updated data
        Student updatedStudent = new Student();
        updatedStudent.setStudentId(studentId);
        updatedStudent.setFirstName(fName);
        updatedStudent.setLastName(lName);
        updatedStudent.setEmail(email);
        updatedStudent.setPhone(phone);
        updatedStudent.setAddress(address);
        updatedStudent.setDob(dob);

        boolean success = studentDAO.updateStudent(updatedStudent);

        if (success) {
            response.sendRedirect("ProfileServlet?status=success");
        } else {
            response.sendRedirect("editprofile.jsp?error=update_failed");
        }
    }
}