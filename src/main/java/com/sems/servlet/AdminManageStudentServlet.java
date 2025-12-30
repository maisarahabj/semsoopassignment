/**
 *
 *  FOR ADMIN VIEW ONLY
 *  doGet   -   loads list of all approved students n available courses
 *  doPost  -   ADD: manually add new student and profile without using registration.jsp
 *              UPDATE: update student's personal details
 *              DELETE: deletes a student, their enrollments, profile, login account (user table)
 *
 *
 * @author maisarahabjalil
 */
package com.sems.servlet;

import com.sems.dao.ActivityLogDAO;
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
    private ActivityLogDAO logDAO = new ActivityLogDAO();

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

        HttpSession session = request.getSession();
        Integer adminUserId = (Integer) session.getAttribute("userId"); // Identify the Admin performer
        String action = request.getParameter("action");

        // --- ACTION 1: ADD MANUAL ---
        if ("ADD_MANUAL".equals(action)) {
            String username = request.getParameter("username");
            String pass = request.getParameter("password");
            int manualId = Integer.parseInt(request.getParameter("studentId"));

            Student s = new Student();
            s.setStudentId(manualId);
            s.setFirstName(request.getParameter("firstName"));
            s.setLastName(request.getParameter("lastName"));
            s.setEmail(request.getParameter("email"));
            s.setPhone(request.getParameter("phone"));
            s.setAddress(request.getParameter("address"));

            try {
                String dobStr = request.getParameter("dob");
                if (dobStr != null && !dobStr.isEmpty()) {
                    s.setDob(java.sql.Date.valueOf(dobStr));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean success = studentDAO.createStudentManually(s, username, pass);

            if (success) {
                // LOG: Admin creates student
                logDAO.recordLog(adminUserId, s.getStudentId(), "ADMIN_CREATE_STUDENT",
                        "Admin manually created student profile for: " + s.getFirstName() + " " + s.getLastName());

                response.sendRedirect(request.getContextPath() + "/AdminManageStudentServlet?status=addSuccess");
                return;
            }
        }

        // --- ACTION 2: DELETE STUDENT ---
        if ("DELETE".equals(action)) {
            String studentIdStr = request.getParameter("studentId");
            String userIdStr = request.getParameter("userId");

            if (studentIdStr != null && !studentIdStr.isEmpty() && userIdStr != null && !userIdStr.isEmpty()) {
                try {
                    int sId = Integer.parseInt(studentIdStr);
                    int uId = Integer.parseInt(userIdStr);

                    boolean deleted = studentDAO.deleteStudent(sId, uId);

                    if (deleted) {
                        // LOG: Admin deletes student
                        logDAO.recordLog(adminUserId, sId, "ADMIN_DELETE_STUDENT",
                                "Admin deleted student ID #" + sId + " and cleared associated account.");

                        response.sendRedirect(request.getContextPath() + "/AdminManageStudentServlet?status=deleteSuccess");
                        return;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        // --- ACTION 3: UPDATE STUDENT ---
        if ("UPDATE_STUDENT".equals(action)) {
            try {
                int studentId = Integer.parseInt(request.getParameter("studentId"));
                Student s = new Student();
                s.setStudentId(studentId);
                s.setFirstName(request.getParameter("firstName"));
                s.setLastName(request.getParameter("lastName"));
                s.setEmail(request.getParameter("email"));
                s.setPhone(request.getParameter("phone"));
                s.setAddress(request.getParameter("address"));
                s.setGpa(Double.parseDouble(request.getParameter("gpa")));

                String dobStr = request.getParameter("dob");
                if (dobStr != null && !dobStr.isEmpty()) {
                    s.setDob(java.sql.Date.valueOf(dobStr));
                }

                boolean success = studentDAO.updateStudent(s);

                if (success) {
                    // LOG: Admin updates profile
                    logDAO.recordLog(adminUserId, s.getStudentId(), "ADMIN_UPDATE_PROFILE",
                            "Admin updated profile details (including GPA) for student ID #" + s.getStudentId());

                    response.sendRedirect(request.getContextPath() + "/AdminManageStudentServlet?status=success");
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Default redirect if no specific logic consumes the request
        response.sendRedirect(request.getContextPath() + "/AdminManageStudentServlet");
    }
}
