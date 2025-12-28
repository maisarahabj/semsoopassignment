package com.sems.servlet;

import com.sems.dao.SemesterDAO;
import com.sems.model.Semester;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet for managing semesters
 * @author SEMS Team
 */
@WebServlet("/SemesterServlet")
public class SemesterServlet extends HttpServlet {

    private SemesterDAO semesterDAO;

    @Override
    public void init() {
        semesterDAO = new SemesterDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Security check - only admin can access
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        
        if ("end".equals(action)) {
            endSemester(request, response);
        } else {
            listSemesters(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Security check - only admin can access
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        
        if ("create".equals(action)) {
            createSemester(request, response);
        } else if ("end".equals(action)) {
            endSemester(request, response);
        } else {
            listSemesters(request, response);
        }
    }

    private void listSemesters(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Semester> semesters = semesterDAO.getAllSemesters();
        Semester activeSemester = semesterDAO.getActiveSemester();
        
        request.setAttribute("semesters", semesters);
        request.setAttribute("activeSemester", activeSemester);
        
        request.getRequestDispatcher("/admin/adminsemester.jsp").forward(request, response);
    }

    private void createSemester(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String semesterName = request.getParameter("semesterName");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            
            // Validation
            if (semesterName == null || semesterName.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Semester name is required");
                listSemesters(request, response);
                return;
            }
            
            Date startDate = Date.valueOf(startDateStr);
            Date endDate = Date.valueOf(endDateStr);
            
            // Check if dates are valid
            if (endDate.before(startDate)) {
                request.setAttribute("errorMessage", "End date must be after start date");
                listSemesters(request, response);
                return;
            }
            
            // Check if there's already an active semester
            Semester activeSemester = semesterDAO.getActiveSemester();
            if (activeSemester != null) {
                // Auto-end the previous semester
                semesterDAO.endSemester(activeSemester.getSemesterId());
            }
            
            // Create new semester
            Semester semester = new Semester(semesterName, startDate, endDate);
            semester.setStatus("ACTIVE");
            
            if (semesterDAO.createSemester(semester)) {
                request.setAttribute("successMessage", "Semester created successfully");
            } else {
                request.setAttribute("errorMessage", "Failed to create semester");
            }
            
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Invalid date format");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error creating semester: " + e.getMessage());
        }
        
        listSemesters(request, response);
    }

    private void endSemester(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int semesterId = Integer.parseInt(request.getParameter("semesterId"));
            
            if (semesterDAO.endSemester(semesterId)) {
                request.setAttribute("successMessage", "Semester ended successfully");
            } else {
                request.setAttribute("errorMessage", "Failed to end semester");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid semester ID");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error ending semester: " + e.getMessage());
        }
        
        listSemesters(request, response);
    }
}
