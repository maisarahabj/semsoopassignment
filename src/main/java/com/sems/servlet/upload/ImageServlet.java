/**
 *
 * @author maisarahabjalil
 */
package com.sems.servlet.upload;

import com.sems.dao.StudentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/ImageServlet")
public class ImageServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // We now use userId for everyone
        String userIdStr = request.getParameter("userId");

        try {
            if (userIdStr != null) {
                int userId = Integer.parseInt(userIdStr);
                byte[] imgData = studentDAO.getProfilePhoto(userId);

                if (imgData != null && imgData.length > 0) {
                    response.setContentType("image/jpeg");
                    response.getOutputStream().write(imgData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
