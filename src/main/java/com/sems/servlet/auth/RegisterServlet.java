/**
 *
 * @author maisarahabjalil
 * 
 * ----- NOTE: CHANGE SQL PASSWORD TO YOUR PASS --------
 * conn = DriverManager.getConnection(url, "root", "Rockie.69");
 * find this line and replace "Rockie.69" as your SQL passw
 */

package com.sems.servlet.auth;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet("/auth/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    
    /**
     * Validates password meets requirements:
     * - At least 8 characters
     * - Contains uppercase letter
     * - Contains number
     * - Contains special character
     * @return null if valid, error message if invalid
     */
    private String validatePassword(String password) {
        if (password == null || password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter.";
        }
        if (!password.matches(".*[0-9].*")) {
            return "Password must contain at least one number.";
        }
        if (!password.matches(".*[!@#$%^&*(),.?\"':{}|<>].*")) {
            return "Password must contain at least one special character.";
        }
        return null;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Get parameters
        String user = request.getParameter("username");
        String pass = request.getParameter("password");
        String role = request.getParameter("role");
        String fName = request.getParameter("firstName");
        String lName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String dob = request.getParameter("dob");
        String studentRegId = request.getParameter("studentRegId");
        
        // Validate password constraints
        String passwordError = validatePassword(pass);
        if (passwordError != null) {
            request.setAttribute("errorMessage", passwordError);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        String url = "jdbc:mysql://localhost:3306/sems_db?useSSL=false&allowPublicKeyRetrieval=true";
        Connection conn = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, "root", "Rockie.69");
            conn.setAutoCommit(false);

            // 2. Insert into users table
            String userSql = "INSERT INTO users (username, password_hash, role, status) VALUES (?, ?, ?, 'PENDING')";
            PreparedStatement userSt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            userSt.setString(1, user);
            userSt.setString(2, pass);
            userSt.setString(3, role);
            userSt.executeUpdate();

            ResultSet rs = userSt.getGeneratedKeys();
            if (rs.next()) {
                int newUserId = rs.getInt(1);

                // 3. Insert into students table (Option B)
                String studentSql = "INSERT INTO students (student_id, user_id, first_name, last_name, email, dob) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stuSt = conn.prepareStatement(studentSql);

                // LOGIC: Manual ID for Student, Auto-Increment for Admin
                if ("student".equalsIgnoreCase(role) && studentRegId != null && !studentRegId.isEmpty()) {
                    stuSt.setInt(1, Integer.parseInt(studentRegId));
                } else {
                    stuSt.setNull(1, java.sql.Types.INTEGER);
                }

                stuSt.setInt(2, newUserId);
                stuSt.setString(3, fName);
                stuSt.setString(4, lName);
                stuSt.setString(5, email);
                stuSt.setDate(6, java.sql.Date.valueOf(dob));
                
                stuSt.executeUpdate();
            }

            conn.commit();
            response.sendRedirect(request.getContextPath() + "/login.jsp?registered=true");

        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace(); // View this in NetBeans Output
            request.setAttribute("errorMessage", "Registration failed: " + e.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }
    }
}