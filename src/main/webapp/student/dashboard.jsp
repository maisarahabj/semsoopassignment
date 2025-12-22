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
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/studentCSS/dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <%
        if (session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // EDITED: Retrieving data passed from DashboardServlet
        List<Course> displayCourses = (List<Course>) request.getAttribute("courses");
        String sectionTitle = (String) request.getAttribute("sectionTitle");
        
        // EDITED: Getting student object for personalized name display
        Student student = (Student) session.getAttribute("student");
        String fullName = (student != null) ? student.getFirstName() + " " + student.getLastName() : "Student";
    %>

    <div class="dashboard-wrapper">
        <aside class="sidebar">
            <div class="logo-section">
                <div style="width: 35px; height: 35px; background: #007bff; border-radius: 8px;"></div>
                <span>Barfact University</span>
            </div>

            <nav class="nav-menu">
                <%-- EDITED: Link now points to the Servlet mapping --%>
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
                    <%-- EDITED: Displaying dynamic name from student object --%>
                    <h1>Welcome back, <span style="color: #007bff;"><%= fullName %></span>!</h1>
                    <p style="color: #666; margin-top: 10px;">New Java classes available. Explore advanced concepts now.</p>
                    <button class="btn-enroll">Enroll Now</button>
                </div>
                <div style="width: 180px; height: 140px; background: #f0f7ff; border-radius: 20px;"></div>
            </div>

            <div class="classes-section">
                <h3 style="margin-bottom: 15px;"><%= sectionTitle %></h3>
                <div class="class-grid">
                    <%-- EDITED: Logic now strictly displays Today's courses or an empty state --%>
                    <% 
                        if (displayCourses != null && !displayCourses.isEmpty()) {
                            for (Course course : displayCourses) { 
                    %>
                        <div class="class-card bg-java">
                            <h4><%= course.getCourseName() %></h4>
                            <p style="font-size: 13px; opacity: 0.8;"><%= course.getCourseCode() %></p>
                            <div style="margin-top: 40px;">
                                <i class="fas fa-clock"></i> <%= course.getCourseTime().substring(0, 5) %>
                            </div>
                        </div>
                    <% 
                            } 
                        } else { 
                    %>
                        <div class="empty-state" style="text-align: center; width: 100%; padding: 40px;">
                            <i class="fas fa-calendar-day" style="font-size: 48px; color: #ccc; margin-bottom: 15px;"></i>
                            <h3 style="color: #555;">No classes scheduled for today.</h3>
                            <p style="color: #888;">Enjoy your day off!</p>
                        </div>
                    <% } %>
                    <%-- END EDITED SECTION --%>
                </div>
            </div>
        </main>

        <aside class="right-panel">
            <div class="profile-img-placeholder"></div>
            <h2 style="font-size: 18px;"><%= session.getAttribute("username") %></h2>
            <p style="color: #888; font-size: 14px;">Student</p>
            
            <div style="background: #f8faff; padding: 20px; border-radius: 20px; margin-top: 30px;">
                <h4 style="margin-bottom: 10px;">Calendar</h4>
                <p style="font-size: 12px; color: #666;">December 2025</p>
            </div>

            <a href="${pageContext.request.contextPath}/auth/LogoutServlet" class="btn-logout">Log Out</a>
        </aside>
    </div>
</body>
</html>