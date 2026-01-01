/**
 *
 * @author maisarahabjalil
 */
package com.sems.servlet.upload;

import com.sems.dao.StudentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig; // REQUIRED!
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import jakarta.servlet.http.HttpSession;

@WebServlet("/UploadPhotoServlet")
@MultipartConfig(maxFileSize = 16177215)
public class UploadPhotoServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            studentDAO.deleteProfilePhoto(userId);
            response.sendRedirect("ProfileServlet?status=img_removed");
            return;
        }

        Part filePart = request.getPart("photo");
        if (filePart != null && filePart.getSize() > 0) {
            try (InputStream inputStream = filePart.getInputStream()) {
                boolean success = studentDAO.updateProfilePhoto(userId, inputStream);
                response.sendRedirect("ProfileServlet?status=" + (success ? "img_success" : "img_fail"));
            }
        }
    }
}
