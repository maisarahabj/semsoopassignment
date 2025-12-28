package com.sems.servlet;

import com.sems.dao.*;
import com.sems.model.*;
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
 * Servlet for academic calendar management
 * @author SEMS Team
 */
@WebServlet("/AcademicCalendarServlet")
public class AcademicCalendarServlet extends HttpServlet {

    private CalendarEventDAO eventDAO;
    private SemesterDAO semesterDAO;

    @Override
    public void init() {
        eventDAO = new CalendarEventDAO();
        semesterDAO = new SemesterDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String role = (String) session.getAttribute("role");
        String action = request.getParameter("action");
        
        if ("delete".equals(action) && "admin".equals(role)) {
            deleteEvent(request, response);
            return;
        }
        
        // Get events and semesters
        List<CalendarEvent> upcomingEvents = eventDAO.getUpcomingEvents();
        List<CalendarEvent> allEvents = eventDAO.getAllEvents();
        List<Semester> allSemesters = semesterDAO.getAllSemesters();
        
        request.setAttribute("upcomingEvents", upcomingEvents);
        request.setAttribute("allEvents", allEvents);
        request.setAttribute("allSemesters", allSemesters);
        
        // Route based on role
        if ("admin".equals(role)) {
            request.getRequestDispatcher("/admin/admincalendar.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/student/calendar.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        createEvent(request, response);
    }

    private void createEvent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int semesterId = Integer.parseInt(request.getParameter("semesterId"));
            String eventTitle = request.getParameter("eventTitle");
            String eventType = request.getParameter("eventType");
            String eventDateStr = request.getParameter("eventDate");
            String endDateStr = request.getParameter("endDate");
            String description = request.getParameter("description");
            
            Date eventDate = Date.valueOf(eventDateStr);
            Date endDate = (endDateStr != null && !endDateStr.isEmpty()) 
                ? Date.valueOf(endDateStr) 
                : null;
            
            CalendarEvent event = new CalendarEvent(semesterId, eventTitle, eventType, eventDate);
            event.setEndDate(endDate);
            event.setDescription(description);
            
            if (eventDAO.createEvent(event)) {
                request.setAttribute("successMessage", "Event created successfully");
            } else {
                request.setAttribute("errorMessage", "Failed to create event");
            }
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error creating event: " + e.getMessage());
        }
        
        doGet(request, response);
    }

    private void deleteEvent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int eventId = Integer.parseInt(request.getParameter("eventId"));
            
            if (eventDAO.deleteEvent(eventId)) {
                request.setAttribute("successMessage", "Event deleted successfully");
            } else {
                request.setAttribute("errorMessage", "Failed to delete event");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid event ID");
        }
        
        doGet(request, response);
    }
}
