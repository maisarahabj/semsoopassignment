/**
 * Admin Profile Servlet
 * Handles viewing and updating admin user profile information
 * @author maisarahabjalil
 */
package com.sems.servlet;

import com.sems.dao.StudentDAO;
import com.sems.dao.UserDAO;
import com.sems.dao.ActivityLogDAO;
import com.sems.model.Student;
import com.sems.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@WebServlet("/AdminProfileServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class AdminProfileServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();
    private final UserDAO userDAO = new UserDAO();
    private final ActivityLogDAO logDAO = new ActivityLogDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        // Security check - only admins can access
        if (userId == null || !"admin".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Get admin user information
        User user = userDAO.getUserById(userId);
        Student adminProfile = studentDAO.getStudentByUserId(userId);

        request.setAttribute("user", user);
        request.setAttribute("adminProfile", adminProfile);
        request.getRequestDispatcher("/admin/adminprofile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId == null || !"admin".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Handle profile picture upload
        Part filePart = request.getPart("profilePicture");
        String profilePicturePath = null;
        
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
            String uniqueFileName = "admin_profile_" + userId + "_" + System.currentTimeMillis() + fileExtension;
            
            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            String filePath = uploadPath + File.separator + uniqueFileName;
            filePart.write(filePath);
            
            profilePicturePath = "uploads/" + uniqueFileName;
            
            boolean pictureUpdated = studentDAO.updateProfilePicture(userId, profilePicturePath);
            if (pictureUpdated) {
                logDAO.recordLog(userId, userId, "UPDATE_ADMIN_PROFILE_PICTURE",
                        "Admin updated their profile picture.");
            }
        }

        // Get updated contact information
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        boolean success = studentDAO.updateStudentContactInfo(userId, email, phone, address);

        if (success) {
            logDAO.recordLog(userId, userId, "UPDATE_ADMIN_PROFILE",
                    "Admin updated their profile information.");
            response.sendRedirect(request.getContextPath() + "/AdminProfileServlet?status=success");
        } else {
            response.sendRedirect(request.getContextPath() + "/AdminProfileServlet?error=1");
        }
    }
}
