<%-- 
    Document   : Student Academic Calendar
    Created on : Dec 28, 2025
    Author     : SEMS Team
--%>
<%@page import="com.sems.model.CalendarEvent"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Barfact University | Academic Calendar</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/studentCSS/dashboard.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            .calendar-container {
                background: white;
                padding: 30px;
                border-radius: 10px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                margin: 20px 0;
            }
            
            .upcoming-section {
                margin-bottom: 40px;
            }
            
            .event-card {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 20px;
                border-radius: 10px;
                margin-bottom: 15px;
                box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            }
            
            .event-card h3 {
                margin: 0 0 10px 0;
                font-size: 20px;
            }
            
            .event-card-EXAM {
                background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
            }
            
            .event-card-REGISTRATION {
                background: linear-gradient(135deg, #28a745 0%, #218838 100%);
            }
            
            .event-card-HOLIDAY {
                background: linear-gradient(135deg, #ffc107 0%, #e0a800 100%);
                color: #000;
            }
            
            .event-card-DEADLINE {
                background: linear-gradient(135deg, #fd7e14 0%, #e8590c 100%);
            }
            
            .event-meta {
                display: flex;
                gap: 20px;
                margin-top: 10px;
                font-size: 14px;
                opacity: 0.9;
            }
            
            .event-timeline {
                position: relative;
                padding-left: 30px;
            }
            
            .event-item {
                position: relative;
                padding: 15px;
                margin-bottom: 15px;
                background: #f8f9fa;
                border-left: 4px solid #667eea;
                border-radius: 5px;
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
        </style>
    </head>
    <body>
        <%
            if (session.getAttribute("userId") == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            
            List<CalendarEvent> upcomingEvents = (List<CalendarEvent>) request.getAttribute("upcomingEvents");
            List<CalendarEvent> allEvents = (List<CalendarEvent>) request.getAttribute("allEvents");
            request.setAttribute("activePage", "calendar");
        %>

        <div class="dashboard-wrapper">
            <%@ include file="/includes/studentSidebar.jsp" %>

            <main class="main-content">
                <div class="welcome-banner">
                    <div class="banner-text">
                        <h1>Academic Calendar</h1>
                        <p>Important dates and upcoming events</p>
                    </div>
                    <div class="banner-icon">
                        <i class="fas fa-calendar-check"></i>
                    </div>
                </div>

                <% if (upcomingEvents != null && !upcomingEvents.isEmpty()) { %>
                <div class="calendar-container upcoming-section">
                    <h2><i class="fas fa-calendar-day"></i> Upcoming Events</h2>
                    <% for (CalendarEvent event : upcomingEvents) { %>
                    <div class="event-card event-card-<%= event.getEventType() %>">
                        <h3><%= event.getEventTitle() %></h3>
                        <div class="event-meta">
                            <span><i class="fas fa-calendar"></i> 
                                <%= event.getEventDate() %>
                                <% if (event.isMultiDay()) { %>
                                    - <%= event.getEndDate() %>
                                <% } %>
                            </span>
                            <span><i class="fas fa-tag"></i> <%= event.getEventType() %></span>
                            <span><i class="fas fa-graduation-cap"></i> <%= event.getSemesterName() %></span>
                        </div>
                        <% if (event.getDescription() != null && !event.getDescription().isEmpty()) { %>
                        <p style="margin: 10px 0 0 0; opacity: 0.9;">
                            <%= event.getDescription() %>
                        </p>
                        <% } %>
                    </div>
                    <% } %>
                </div>
                <% } %>

                <div class="calendar-container">
                    <h2><i class="fas fa-list"></i> All Events</h2>
                    
                    <% if (allEvents != null && !allEvents.isEmpty()) { %>
                    <div class="event-timeline">
                        <% for (CalendarEvent event : allEvents) { %>
                        <div class="event-item event-type-<%= event.getEventType() %>">
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
                        <% } %>
                    </div>
                    <% } else { %>
                    <p style="text-align: center; padding: 40px; color: #666;">
                        <i class="fas fa-calendar-times" style="font-size: 48px; display: block; margin-bottom: 15px;"></i>
                        No events scheduled yet.
                    </p>
                    <% } %>
                </div>

            </main>
        </div>
    </body>
</html>
