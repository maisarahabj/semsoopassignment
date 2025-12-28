<%-- 
    Document   : Admin Academic Calendar
    Created on : Dec 28, 2025
    Author     : SEMS Team
--%>
<%@page import="com.sems.model.CalendarEvent"%>
<%@page import="com.sems.model.Semester"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Barfact University | Academic Calendar</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/adminCSS/admindash.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            .calendar-container {
                background: white;
                padding: 30px;
                border-radius: 10px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                margin: 20px 0;
            }
            
            .event-form {
                background: #f8f9fa;
                padding: 20px;
                border-radius: 8px;
                margin-bottom: 30px;
            }
            
            .form-row {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 15px;
                margin-bottom: 15px;
            }
            
            .form-group label {
                display: block;
                font-weight: 600;
                margin-bottom: 5px;
            }
            
            .form-group input,
            .form-group select,
            .form-group textarea {
                width: 100%;
                padding: 10px;
                border: 1px solid #ddd;
                border-radius: 5px;
            }
            
            .event-timeline {
                position: relative;
                padding-left: 30px;
            }
            
            .event-item {
                position: relative;
                padding: 15px;
                margin-bottom: 15px;
                background: white;
                border-left: 4px solid #667eea;
                border-radius: 5px;
                box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            }
            
            .event-item::before {
                content: '';
                position: absolute;
                left: -17px;
                top: 20px;
                width: 10px;
                height: 10px;
                border-radius: 50%;
                background: #667eea;
                border: 3px solid white;
                box-shadow: 0 0 0 2px #667eea;
            }
            
            .event-type-EXAM { border-left-color: #dc3545; }
            .event-type-EXAM::before { background: #dc3545; box-shadow: 0 0 0 2px #dc3545; }
            
            .event-type-REGISTRATION { border-left-color: #28a745; }
            .event-type-REGISTRATION::before { background: #28a745; box-shadow: 0 0 0 2px #28a745; }
            
            .event-type-HOLIDAY { border-left-color: #ffc107; }
            .event-type-HOLIDAY::before { background: #ffc107; box-shadow: 0 0 0 2px #ffc107; }
            
            .event-type-DEADLINE { border-left-color: #fd7e14; }
            .event-type-DEADLINE::before { background: #fd7e14; box-shadow: 0 0 0 2px #fd7e14; }
            
            .event-badge {
                display: inline-block;
                padding: 3px 10px;
                border-radius: 12px;
                font-size: 12px;
                font-weight: 600;
                margin-right: 10px;
            }
            
            .badge-EXAM { background: #dc3545; color: white; }
            .badge-REGISTRATION { background: #28a745; color: white; }
            .badge-HOLIDAY { background: #ffc107; color: #000; }
            .badge-DEADLINE { background: #fd7e14; color: white; }
            .badge-OTHER { background: #6c757d; color: white; }
            
            .btn-delete {
                background: #dc3545;
                color: white;
                padding: 5px 12px;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 13px;
            }
        </style>
    </head>
    <body>
        <%
            if (session.getAttribute("userId") == null || !"admin".equals(session.getAttribute("role"))) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            
            List<CalendarEvent> upcomingEvents = (List<CalendarEvent>) request.getAttribute("upcomingEvents");
            List<CalendarEvent> allEvents = (List<CalendarEvent>) request.getAttribute("allEvents");
            List<Semester> allSemesters = (List<Semester>) request.getAttribute("allSemesters");
            String successMessage = (String) request.getAttribute("successMessage");
            String errorMessage = (String) request.getAttribute("errorMessage");
        %>

        <div class="dashboard-wrapper">
            <aside class="sidebar">
                <div class="logo-section">
                    <img src="${pageContext.request.contextPath}/assets/cat.png" class="logo-img" style="width: 50px; height: 50px;">
                    <span class="logo-text">Barfact Admin</span>
                </div>

                <nav class="nav-menu">
                    <a href="${pageContext.request.contextPath}/DashboardServlet" class="nav-link">
                        <i class="fas fa-chart-line"></i> Overview
                    </a>
                    <a href="${pageContext.request.contextPath}/SemesterServlet" class="nav-link">
                        <i class="fas fa-calendar-alt"></i> Manage Semesters
                    </a>
                    <a href="${pageContext.request.contextPath}/AcademicCalendarServlet" class="nav-link active">
                        <i class="fas fa-calendar-check"></i> Academic Calendar
                    </a>
                    <a href="${pageContext.request.contextPath}/CourseServlet" class="nav-link">
                        <i class="fas fa-book-open"></i> Manage Courses
                    </a>
                    <a href="${pageContext.request.contextPath}/AdminManageStudentServlet" class="nav-link">
                        <i class="fas fa-user-graduate"></i> Manage Students
                    </a>
                    <a href="${pageContext.request.contextPath}/GradeServlet" class="nav-link">
                        <i class="fas fa-graduation-cap"></i> Grade Management
                    </a>
                </nav>
            </aside>

            <main class="main-content">
                <div class="welcome-banner">
                    <div class="banner-text">
                        <h1>Academic Calendar</h1>
                        <p>Manage important dates and events</p>
                    </div>
                    <div class="banner-icon">
                        <i class="fas fa-calendar-check"></i>
                    </div>
                </div>

                <% if (successMessage != null) { %>
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i> <%= successMessage %>
                </div>
                <% } %>

                <% if (errorMessage != null) { %>
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-circle"></i> <%= errorMessage %>
                </div>
                <% } %>

                <div class="calendar-container">
                    <h2><i class="fas fa-plus-circle"></i> Add New Event</h2>
                    <form action="${pageContext.request.contextPath}/AcademicCalendarServlet" method="post" class="event-form">
                        <div class="form-row">
                            <div class="form-group">
                                <label>Event Title *</label>
                                <input type="text" name="eventTitle" placeholder="e.g., Midterm Exams" required>
                            </div>
                            <div class="form-group">
                                <label>Event Type *</label>
                                <select name="eventType" required>
                                    <option value="EXAM">Exam</option>
                                    <option value="REGISTRATION">Registration</option>
                                    <option value="DEADLINE">Deadline</option>
                                    <option value="HOLIDAY">Holiday</option>
                                    <option value="OTHER">Other</option>
                                </select>
                            </div>
                        </div>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label>Semester *</label>
                                <select name="semesterId" required>
                                    <% if (allSemesters != null) {
                                        for (Semester sem : allSemesters) { %>
                                    <option value="<%= sem.getSemesterId() %>">
                                        <%= sem.getSemesterName() %>
                                    </option>
                                    <% } } %>
                                </select>
                            </div>
                            <div class="form-group">
                                <label>Event Date *</label>
                                <input type="date" name="eventDate" required>
                            </div>
                        </div>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label>End Date (Optional for multi-day events)</label>
                                <input type="date" name="endDate">
                            </div>
                            <div class="form-group">
                                <label>Description</label>
                                <input type="text" name="description" placeholder="Additional details">
                            </div>
                        </div>
                        
                        <button type="submit" class="btn-create">
                            <i class="fas fa-plus"></i> Add Event
                        </button>
                    </form>
                </div>

                <div class="calendar-container">
                    <h2><i class="fas fa-calendar-day"></i> All Events</h2>
                    
                    <% if (allEvents != null && !allEvents.isEmpty()) { %>
                    <div class="event-timeline">
                        <% for (CalendarEvent event : allEvents) { %>
                        <div class="event-item event-type-<%= event.getEventType() %>">
                            <div style="display: flex; justify-content: space-between; align-items: start;">
                                <div style="flex: 1;">
                                    <span class="event-badge badge-<%= event.getEventType() %>">
                                        <%= event.getEventType() %>
                                    </span>
                                    <strong><%= event.getEventTitle() %></strong>
                                    <div style="margin-top: 5px; color: #666; font-size: 14px;">
                                        <i class="fas fa-calendar"></i> 
                                        <%= event.getEventDate() %>
                                        <% if (event.isMultiDay()) { %>
                                            - <%= event.getEndDate() %>
                                        <% } %>
                                        | <i class="fas fa-graduation-cap"></i> <%= event.getSemesterName() %>
                                    </div>
                                    <% if (event.getDescription() != null && !event.getDescription().isEmpty()) { %>
                                    <div style="margin-top: 8px; font-size: 14px;">
                                        <%= event.getDescription() %>
                                    </div>
                                    <% } %>
                                </div>
                                <form action="${pageContext.request.contextPath}/AcademicCalendarServlet" method="get" style="display: inline;">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="eventId" value="<%= event.getEventId() %>">
                                    <button type="submit" class="btn-delete" 
                                            onclick="return confirm('Delete this event?')">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </form>
                            </div>
                        </div>
                        <% } %>
                    </div>
                    <% } else { %>
                    <p style="text-align: center; padding: 40px; color: #666;">
                        <i class="fas fa-calendar-times" style="font-size: 48px; display: block; margin-bottom: 15px;"></i>
                        No events found. Add your first event above.
                    </p>
                    <% } %>
                </div>

            </main>
        </div>
    </body>
</html>
