<%-- 
    Document   : dashboard student
    Created on : 18 Dec 2025, 12:33:38 pm
    Author     : maisarahabjalil
--%>

<%@page import="java.util.List"%>
<%@page import="com.sems.model.Course"%>
<%@page import="com.sems.model.Student"%>
<%@page import="com.sems.model.Semester"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Barfact University | Dashboard</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/studentCSS/dashboard.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>
    <body>
        <%
            if (session.getAttribute("userId") == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            Student student = (Student) request.getAttribute("student");
            List<Course> enrolledCourses = (List<Course>) request.getAttribute("enrolledCourses");
            Semester activeSemester = (Semester) request.getAttribute("activeSemester");
            String fullName = (student != null) ? student.getFirstName() + " " + student.getLastName() : "Student";
            request.setAttribute("activePage", "dashboard");
        %>

        <div class="dashboard-wrapper">
            <%@ include file="/includes/studentSidebar.jsp" %>

            <main class="main-content">
                <div class="welcome-banner">
                    <div class="banner-text">
                        <h1>Welcome back, <span class="highlight-blue"><%= fullName%></span>!</h1>
                        <p>Your academic progress is looking great this semester.</p>
                        <% if (activeSemester != null) { %>
                        <p style="margin-top: 10px; font-size: 14px; opacity: 0.9;">
                            <i class="fas fa-calendar-alt"></i> Current Semester: 
                            <strong><%= activeSemester.getSemesterName() %></strong>
                        </p>
                        <% } %>
                    </div>
                    <div class="banner-icon">
                        <i class="fas fa-graduation-cap"></i>
                    </div>
                </div>

                <div class="schedule-container">
                    <h3>Weekly Timetable</h3>
                    <div class="timetable-grid">
                        <div class="grid-header">Time</div>
                        <div class="grid-header">Monday</div>
                        <div class="grid-header">Tuesday</div>
                        <div class="grid-header">Wednesday</div>
                        <div class="grid-header">Thursday</div>
                        <div class="grid-header">Friday</div>

                        <% for (int hour = 8; hour <= 18; hour++) {
                                int rowPosition = hour - 6;
                        %>
                        <div class="time-label" style="grid-row: <%= rowPosition%>;"><%= hour%>:00</div>
                        <% } %>

                        <%
                            if (enrolledCourses != null) {
                                for (Course c : enrolledCourses) {
                                    int col = 2;
                                    String day = c.getCourseDay();
                                    if (day.equals("Tuesday")) {
                                        col = 3;
                                    } else if (day.equals("Wednesday")) {
                                        col = 4;
                                    } else if (day.equals("Thursday")) {
                                        col = 5;
                                    } else if (day.equals("Friday")) {
                                        col = 6;
                                    }

                                    int startHour = Integer.parseInt(c.getCourseTime().substring(0, 2));
                                    int row = startHour - 6;
                        %>
                        <div class="course-entry bg-java" style="grid-column: <%= col%>; grid-row: <%= row%>;">
                            <strong><%= c.getCourseCode()%></strong>
                            <span><%= c.getCourseName()%></span>
                            <span class="course-time-meta">
                                <i class="fas fa-clock"></i> <%= c.getCourseTime().substring(0, 5)%>
                            </span>
                        </div>
                        <%
                                }
                            }
                        %>
                    </div>
                </div>
            </main>

            <aside class="right-panel">
                <div class="profile-avatar">
                    <i class="fas fa-user"></i>
                </div>
                <h2 class="profile-name"><%= fullName%></h2>
                <p class="profile-id">Student ID: #<%= (student != null) ? student.getStudentId() : "N/A"%></p>

                <div class="term-info-card">
                    <h4><i class="fas fa-calendar-alt"></i> Term Info</h4>
                    <p>Semester: Dec 2025</p>
                    <p>Status: Active</p>
                </div>

                <a href="${pageContext.request.contextPath}/auth/LogoutServlet" class="btn-logout">
                    <i class="fas fa-sign-out-alt"></i> Log Out
                </a>
            </aside>
        </div>
    </body>
</html>