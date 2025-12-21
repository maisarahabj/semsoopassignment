<%-- 
    Document   : dashboard student
    Created on : 18 Dec 2025, 12:33:38 pm
    Author     : maisarahabjalil
--%>

<%-- imports for the models and list --%>
<%@page import="java.util.List"%>
<%@page import="com.sems.model.Course"%>
<%@page import="com.sems.model.Student"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Barfact University | Dashboard</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <%
        if (session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        List<Course> allCourses = (List<Course>) request.getAttribute("allCourses");
        Student student = (Student) session.getAttribute("student");
        String fullName = (student != null) ? student.getFirstName() + " " + student.getLastName() : "Student";

        // EDITED: Data helpers for the weekly grid
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        String[] hours = {"08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"};
    %>

    <div class="dashboard-wrapper">
        <aside class="sidebar">
            <div class="logo-section">
                <div style="width: 35px; height: 35px; background: #007bff; border-radius: 8px;"></div>
                <span>Barfact University</span>
            </div>
            <nav class="nav-menu">
                <a href="${pageContext.request.contextPath}/dashboard" class="nav-link active"><i class="fas fa-home"></i> Dashboard</a>
                <a href="#" class="nav-link"><i class="fas fa-chalkboard"></i> Classroom</a>
                <a href="#" class="nav-link"><i class="fas fa-play-circle"></i> Live Lessons</a>
                <a href="#" class="nav-link"><i class="fas fa-plus-square"></i> Add Subjects</a>
                <a href="#" class="nav-item"><i class="fas fa-book"></i> View Subjects</a>
            </nav>
            <div class="cgpa-container">
                <div style="font-size: 24px; font-weight: bold; color: #007bff;">3.85</div>
                <p style="font-size: 12px; color: #888;">Current CGPA</p>
            </div>
        </aside>

        <main class="main-content">
            <div class="welcome-banner">
                <div>
                    <h1>Welcome back, <span style="color: #007bff;"><%= fullName %></span>!</h1>
                    <p style="color: #666; margin-top: 10px;">Here is your weekly academic schedule.</p>
                </div>
                <div style="width: 180px; height: 140px; background: #f0f7ff; border-radius: 20px;"></div>
            </div>

            <%-- EDITED: Replaced class-grid with a Weekly Timetable --%>
            <div class="schedule-container">
                <div class="timetable-grid">
                    <div class="grid-header">Time</div>
                    <% for(String day : days) { %>
                        <div class="grid-header"><%= day.substring(0,3) %></div>
                    <% } %>

                    <% for(String hour : hours) { %>
                        <div class="time-col"><%= hour %></div>
                        <% for(String day : days) { 
                            Course found = null;
                            if(allCourses != null) {
                                for(Course c : allCourses) {
                                    // Match by day and start hour
                                    if(c.getCourseDay().equalsIgnoreCase(day) && c.getCourseTime().startsWith(hour.substring(0,2))) {
                                        found = c;
                                        break;
                                    }
                                }
                            }
                        %>
                            <div class="slot">
                                <% if(found != null) { %>
                                    <div class="course-entry">
                                        <strong><%= found.getCourseName() %></strong>
                                        <%= found.getCourseCode() %>
                                    </div>
                                <% } %>
                            </div>
                        <% } %>
                    <% } %>
                </div>
            </div>
        </main>

        <aside class="right-panel">
            <div class="profile-img-placeholder"></div>
            <h2 style="font-size: 18px;"><%= session.getAttribute("username") %></h2>
            <p style="color: #888; font-size: 14px;">Student</p>
            <div style="background: #f8faff; padding: 20px; border-radius: 20px; margin-top: 30px;">
                <h4>Calendar</h4>
                <p style="font-size: 12px; color: #666;">December 2025</p>
            </div>
            <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn-logout">Log Out</a>
        </aside>
    </div>
</body>
</html>