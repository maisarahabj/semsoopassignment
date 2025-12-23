<%-- 
    Document   : dashboard student
    Created on : 18 Dec 2025, 12:33:38 pm
    Author     : maisarahabjalil
--%>

<%@page import="java.util.List"%>
<%@page import="com.sems.model.Course"%>
<%@page import="com.sems.model.Student"%>
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
        String fullName = (student != null) ? student.getFirstName() + " " + student.getLastName() : "Student";
    %>

    <div class="dashboard-wrapper">
        <aside class="sidebar">
            <div class="logo-section">
                <div class="logo-box"></div>
                <span class="logo-text">Barfact Uni</span>
            </div>

            <nav class="nav-menu">
                <a href="${pageContext.request.contextPath}/DashboardServlet" class="nav-link active">
                    <i class="fas fa-home"></i> Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/student/mycourse.jsp" class="nav-link">
                    <i class="fas fa-book"></i> My Classes
                </a>
                <a href="${pageContext.request.contextPath}/student/addcourse.jsp" class="nav-link">
                    <i class="fas fa-plus-square"></i> Add Subjects
                </a>
                <a href="${pageContext.request.contextPath}/student/viewprofile.jsp" class="nav-link">
                    <i class="fas fa-user"></i> Profile
                </a>
            </nav>

            <div class="cgpa-container">
                <div class="cgpa-value"><%= (student != null) ? student.getGpa() : "0.00" %></div>
                <p class="cgpa-label">Current CGPA</p>
            </div>
        </aside>

        <main class="main-content">
            <div class="welcome-banner">
                <div class="banner-text">
                    <h1>Welcome back, <span class="highlight-blue"><%= fullName %></span>!</h1>
                    <p>Your academic progress is looking great this semester.</p>
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

                    <% for(int hour=8; hour<=18; hour++) { 
                        int rowPosition = hour - 6; 
                    %>
                        <div class="time-label" style="grid-row: <%= rowPosition %>;"><%= hour %>:00</div>
                    <% } %>

                    <% 
                        if (enrolledCourses != null) {
                            for (Course c : enrolledCourses) {
                                int col = 2; 
                                String day = c.getCourseDay();
                                if(day.equals("Tuesday")) col = 3;
                                else if(day.equals("Wednesday")) col = 4;
                                else if(day.equals("Thursday")) col = 5;
                                else if(day.equals("Friday")) col = 6;

                                int startHour = Integer.parseInt(c.getCourseTime().substring(0,2));
                                int row = startHour - 6; 
                    %>
                        <div class="course-entry bg-java" style="grid-column: <%= col %>; grid-row: <%= row %>;">
                            <strong><%= c.getCourseCode() %></strong>
                            <span><%= c.getCourseName() %></span>
                            <span class="course-time-meta">
                                <i class="fas fa-clock"></i> <%= c.getCourseTime().substring(0,5) %>
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
            <h2 class="profile-name"><%= fullName %></h2>
            <p class="profile-id">Student ID: #<%= (student != null) ? student.getStudentId() : "N/A" %></p>
            
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