/**
 *
 * @author maisarahabjalil
 */
package com.sems.servlet;

import com.sems.dao.StudentDAO;
import com.sems.dao.UserDAO;
import com.sems.dao.ActivityLogDAO;
import com.sems.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/ProfileServlet")
public class ProfileServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();
    private final ActivityLogDAO logDAO = new ActivityLogDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId != null) {
            Student userDetails = studentDAO.getStudentByUserId(userId);
            request.setAttribute("student", userDetails);

            if ("admin".equals(role)) {
                request.getRequestDispatcher("/admin/adminprofile.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/student/viewprofile.jsp").forward(request, response);
            }
        } else {
            response.sendRedirect("login.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 1. Get ALL parameters
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String fName = request.getParameter("firstName");
        String lName = request.getParameter("lastName");
        String dob = request.getParameter("dob");

        // 2. Fetch current student data for fallback (Important for Admin)
        Student current = studentDAO.getStudentByUserId(userId);

        // Safety check: if inputs are missing from JSP, use existing data
        if (fName == null) {
            fName = current.getFirstName();
        }
        if (lName == null) {
            lName = current.getLastName();
        }
        if (dob == null) {
            dob = (current.getDob() != null) ? current.getDob().toString() : "";
        }

        // 3. DUPLICATE CHECK (Using the new userId-aware method)
        if (studentDAO.isUserExists(username, email, userId)) {
            response.sendRedirect(request.getContextPath() + "/ProfileServlet?status=duplicate");
            return;
        }

        // 4. PASSWORD VALIDATION
        if (password != null && !password.isEmpty()) {
            if (!password.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
                response.sendRedirect(request.getContextPath() + "/ProfileServlet?status=weak_password");
                return;
            }
        }

        // 5. DATABASE UPDATES
        UserDAO userDAO = new UserDAO();
        boolean securitySuccess = userDAO.updateAccountSecurity(userId, username, password);
        boolean profileSuccess = studentDAO.updateFullProfile(userId, fName, lName, email, phone, address, dob);

        if (securitySuccess && profileSuccess) {
            session.setAttribute("username", username);
            response.sendRedirect(request.getContextPath() + "/ProfileServlet?status=success");
        } else {
            response.sendRedirect(request.getContextPath() + "/ProfileServlet?status=error");
        }
    }
}
