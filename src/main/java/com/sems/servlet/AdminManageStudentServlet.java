/**
 *
 * @author maisarahabjalil
 */
package com.sems.servlet;

import com.sems.dao.StudentDAO;
import com.sems.dao.CourseDAO;
import com.sems.model.Student;
import com.sems.model.Course;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/AdminManageStudentServlet")
public class AdminManageStudentServlet extends HttpServlet {

    private StudentDAO studentDAO = new StudentDAO();
    private CourseDAO courseDAO = new CourseDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Fetch data for the student table
        List<Student> studentList = studentDAO.getAllStudents();

        // 2. NEW: Fetch data for the "Enroll in Course" dropdown in the modal
        List<Course> allCoursesList = courseDAO.getAllCourses();

        // 3. Attach both lists to the request
        request.setAttribute("studentList", studentList);
        request.setAttribute("allCoursesList", allCoursesList);

        // 4. Forward to the JSP page
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
            String userIdStr = request.getParameter("userId");

            if (studentIdStr != null && !studentIdStr.isEmpty() && userIdStr != null && !userIdStr.isEmpty()) {
                try {
                    int sId = Integer.parseInt(studentIdStr);
                    int uId = Integer.parseInt(userIdStr);

                    boolean deleted = studentDAO.deleteStudent(sId, uId);

                    if (deleted) {
                        // Redirect immediately on success to prevent further execution
                        response.sendRedirect(request.getContextPath() + "/AdminManageStudentServlet?status=deleteSuccess");
                        return;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        if ("UPDATE_STUDENT".equals(action)) {
            try {
                // 1. Extract parameters from the Admin Edit Form
                int studentId = Integer.parseInt(request.getParameter("studentId"));
                String firstName = request.getParameter("firstName");
                String lastName = request.getParameter("lastName");
                String email = request.getParameter("email");
                String phone = request.getParameter("phone");
                String address = request.getParameter("address");
                double gpa = Double.parseDouble(request.getParameter("gpa"));

                // 2. Create Student object and set values
                Student s = new Student();
                s.setStudentId(studentId);
                s.setFirstName(firstName);
                s.setLastName(lastName);
                s.setEmail(email);
                s.setPhone(phone);
                s.setAddress(address);
                s.setGpa(gpa);

                // Handle Date of Birth conversion
                String dobStr = request.getParameter("dob");
                if (dobStr != null && !dobStr.isEmpty()) {
                    s.setDob(java.sql.Date.valueOf(dobStr));
                }

                // 3. Call the master update method in DAO
                boolean success = studentDAO.updateStudent(s);

                // Optional: Add a status parameter to show a toast/alert on refresh
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/AdminManageStudentServlet?status=success");
                    return; // Important: return after redirect
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Redirect refreshes the list so the UI stays in sync
        response.sendRedirect(request.getContextPath() + "/AdminManageStudentServlet");
    }
}
