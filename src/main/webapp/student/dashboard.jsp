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
            <div class="logo-section" style="display:flex; align-items:center; gap:10px; margin-bottom:40px;">
                <div style="width: 35px; height: 35px; background: #007bff; border-radius: 8px;"></div>
                <span style="font-weight:bold; font-size:18px;">Barfact Uni</span>
            </div>

            <nav class="nav-menu" style="display:flex; flex-direction:column; gap:15px;">
                <a href="DashboardServlet" class="nav-link active" style="text-decoration:none; color:#007bff;"><i class="fas fa-home"></i> Dashboard</a>
                <a href="mycourse.jsp" class="nav-link" style="text-decoration:none; color:#666;"><i class="fas fa-book"></i> My Classes</a>
                <a href="addcourse.jsp" class="nav-link" style="text-decoration:none; color:#666;"><i class="fas fa-plus-square"></i> Add Subjects</a>
                <a href="viewprofile.jsp" class="nav-link" style="text-decoration:none; color:#666;"><i class="fas fa-user"></i> Profile</a>
            </nav>

            <div class="cgpa-container" style="margin-top:auto; background:#f0f7ff; padding:20px; border-radius:15px; text-align:center;">
                <div style="font-size: 24px; font-weight: bold; color: #007bff;"><%= (student != null) ? student.getGpa() : "0.00" %></div>
                <p style="font-size: 12px; color: #888;">Current CGPA</p>
            </div>
        </aside>

        <main class="main-content">
            <div class="welcome-banner">
                <div>
                    <h1>Welcome back, <span style="color: #007bff;"><%= fullName %></span>!</h1>
                    <p style="color: #666; margin-top: 10px;">Your academic progress is looking great this semester.</p>
                </div>
                <div style="width: 150px; height: 100px; background: #e7f3ff; border-radius: 20px; display:flex; align-items:center; justify-content:center;">
                     <i class="fas fa-graduation-cap" style="font-size:40px; color:#007bff;"></i>
                </div>
            </div>

            <div class="schedule-container">
                <h3 style="margin-bottom: 20px;">Weekly Timetable</h3>
                <div class="timetable-grid">
                    <div class="grid-header">Time</div>
                    <div class="grid-header">Monday</div>
                    <div class="grid-header">Tuesday</div>
                    <div class="grid-header">Wednesday</div>
                    <div class="grid-header">Thursday</div>
                    <div class="grid-header">Friday</div>

                    <% for(int hour=8; hour<=18; hour++) { 
                        int rowPosition = hour - 6; // Mapping 8am to Row 2, 9am to Row 3, etc.
                    %>
                        <div class="time-label" style="grid-row: <%= rowPosition %>;"><%= hour %>:00</div>
                    <% } %>

                    <% 
                        if (enrolledCourses != null) {
                            for (Course c : enrolledCourses) {
                                // 1. Calculate Column based on Day
                                int col = 2; // Default Monday
                                String day = c.getCourseDay();
                                if(day.equals("Tuesday")) col = 3;
                                else if(day.equals("Wednesday")) col = 4;
                                else if(day.equals("Thursday")) col = 5;
                                else if(day.equals("Friday")) col = 6;

                                // 2. Calculate Row based on Time (e.g., "09:00:00")
                                int startHour = Integer.parseInt(c.getCourseTime().substring(0,2));
                                int row = startHour - 6; 
                    %>
                        <div class="course-entry bg-java" style="grid-column: <%= col %>; grid-row: <%= row %>;">
                            <strong><%= c.getCourseCode() %></strong>
                            <span><%= c.getCourseName() %></span>
                            <span style="margin-top:5px; font-size:10px; opacity:0.7;"><i class="fas fa-clock"></i> <%= c.getCourseTime().substring(0,5) %></span>
                        </div>
                    <% 
                            }
                        } 
                    %>
                </div>
            </div>
        </main>

        <aside class="right-panel">
            <div style="width:80px; height:80px; background:#eee; border-radius:50%; margin: 0 auto 15px; display:flex; align-items:center; justify-content:center;">
                <i class="fas fa-user" style="font-size:30px; color:#ccc;"></i>
            </div>
            <h2 style="font-size: 18px;"><%= fullName %></h2>
            <p style="color: #888; font-size: 14px;">Student ID: #<%= (student != null) ? student.getStudentId() : "N/A" %></p>
            
            <div style="background: #f8faff; padding: 20px; border-radius: 20px; margin-top: 30px; text-align:left;">
                <h4 style="margin-bottom: 10px;"><i class="fas fa-calendar-alt"></i> Term Info</h4>
                <p style="font-size: 12px; color: #666;">Semester: Dec 2025</p>
                <p style="font-size: 12px; color: #666;">Status: Active</p>
            </div>

            <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn-logout">
                <i class="fas fa-sign-out-alt"></i> Log Out
            </a>
        </aside>
    </div>
</body>
</html>