package com.sems.servlet;

import com.sems.dao.*;
import com.sems.model.*;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet for bulk course migration between semesters
 * @author SEMS Team
 */
@WebServlet("/admin/BulkCourseMigrationServlet")
public class BulkCourseMigrationServlet extends HttpServlet {

    private CourseDAO courseDAO;
    private SemesterDAO semesterDAO;

    @Override
    public void init() {
        courseDAO = new CourseDAO();
        semesterDAO = new SemesterDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Get all semesters for dropdown
        List<Semester> allSemesters = semesterDAO.getAllSemesters();
        request.setAttribute("allSemesters", allSemesters);
        
        request.getRequestDispatcher("/admin/coursemigration.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            int sourceSemesterId = Integer.parseInt(request.getParameter("sourceSemesterId"));
            int targetSemesterId = Integer.parseInt(request.getParameter("targetSemesterId"));
            
            if (sourceSemesterId == targetSemesterId) {
                request.setAttribute("errorMessage", "Source and target semesters must be different");
                doGet(request, response);
                return;
            }

            // Perform bulk migration
            int migratedCount = courseDAO.migrateCourses(sourceSemesterId, targetSemesterId);
            
            if (migratedCount > 0) {
                request.setAttribute("successMessage", 
                    "Successfully migrated " + migratedCount + " courses");
            } else {
                request.setAttribute("errorMessage", "No courses were migrated");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid semester selection");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error during migration: " + e.getMessage());
        }
        
        doGet(request, response);
    }
}
