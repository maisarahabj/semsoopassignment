/**
 *
 * @author maisarahabjalil
 */
package com.sems.servlet;

import com.sems.dao.SummaryDAO;
import com.sems.model.Course;
import com.sems.model.Student; // Import Student here too
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/AdminReportServlet")
public class AdminReportServlet extends HttpServlet {

    private final SummaryDAO summaryDAO = new SummaryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Security Check
        HttpSession session = request.getSession();
        String role = (String) session.getAttribute("role");

        if (session.getAttribute("userId") == null || !"admin".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        summaryDAO.recalculateAllGPAs();

        // 2. Fetch Data from DAO
        double avgGpa = summaryDAO.getCampusAvgGPA();
        int totalSeats = summaryDAO.getTotalActiveEnrollments();
        Map<String, Integer> gradeDist = summaryDAO.getGradeDistribution();
        List<Course> coursePop = summaryDAO.getCoursePopularity();
        List<Student> topStudents = summaryDAO.getTopStudents();

        // 3. Set Attributes for JSP
        request.setAttribute("avgGpa", avgGpa);
        request.setAttribute("totalSeats", totalSeats);
        request.setAttribute("gradeDist", gradeDist);
        request.setAttribute("coursePop", coursePop);
        request.setAttribute("topStudents", topStudents); // NEW

        // 4. Forward to the Report JSP (Updated path to adminreport.jsp)
        request.getRequestDispatcher("/admin/adminreport.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
