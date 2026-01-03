<%-- 
    Document   : Admin dashboard
    Created on : 18 Dec 2025, 12:33:30 pm
    Author     : maisarahabjalil
--%>
<%@page import="com.sems.model.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Barfact University | Admin Dashboard</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/adminCSS/admindash.css?v=2.1">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>

    <body>
        <%
            // Security Check: Ensure only Admins can see this
            if (session.getAttribute("userId") == null || !"admin".equals(session.getAttribute("role"))) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            String adminName = (String) session.getAttribute("username");
        %>

        <div class="dashboard-wrapper">
            <aside class="sidebar">
                <div class="logo-section">
                    <img src="${pageContext.request.contextPath}/assets/cat.png" class="logo-img"style="width: 50px; height: 50px; ">
                    <span class="logo-text">Barfact Admin</span>
                </div>

                <nav class="nav-menu">
                    <a href="${pageContext.request.contextPath}/DashboardServlet" class="nav-link active">
                        <i class="fas fa-chart-line"></i> Overview
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
                    <a href="${pageContext.request.contextPath}/auth/AdminPendingServlet" class="nav-link">
                        <i class="fas fa-clock"></i> Pending Approvals
                    </a>
                    <a href="${pageContext.request.contextPath}/ActivityServlet" class="nav-link">
                        <i class="fas fa-history"></i> System Logs
                    </a>
                    <a href="${pageContext.request.contextPath}/AdminReportServlet" class="nav-link">
                        <i class="fas fa-file-alt"></i> Academic Report
                    </a>
                    <a href="${pageContext.request.contextPath}/ProfileServlet" class="nav-link">
                        <i class="fas fa-user-shield"></i> My Account
                    </a>
                </nav>

            </aside>

            <main class="main-content">
                <div class="welcome-banner">
                    <div class="banner-text">
                        <h1>System Portal, <span class="highlight-blue">Admin <%= adminName%></span></h1>
                        <p>Manage university courses, student enrollments, and system settings.</p>
                    </div>
                    <div class="banner-icon">
                        <i class="fas fa-user-shield"></i>
                    </div>
                </div>

                <div class="admin-stats-grid-2x2">
                    <div class="stat-card">
                        <i class="fas fa-users"></i>
                        <div class="stat-info">
                            <h3 id="total-stu">
                                <%= (request.getAttribute("activeStudentCount") != null) ? request.getAttribute("activeStudentCount") : "3"%> 
                                Total Students
                            </h3>
                            <a href="${pageContext.request.contextPath}/AdminManageStudentServlet" class="btn-stat-action">
                                View All Students 
                            </a>
                        </div>
                    </div>

                    <div class="stat-card">
                        <%
                            double avgGpa = (request.getAttribute("campusGPA") != null) ? (Double) request.getAttribute("campusGPA") : 3.42;
                            double gpaPercentage = (avgGpa / 4.0) * 100;
                        %>
                        <div class="circular-progress" style="background: conic-gradient(#007bff <%= gpaPercentage%>%, #eef2f6 0deg);">
                            <div class="inner-circle">
                                <span><%= String.format("%.2f", avgGpa)%></span>
                            </div>
                        </div>
                        <div class="stat-info">
                            <h3>Campus GPA</h3>
                            <p>Average Academic Performance</p>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div class="star-container">
                            <i class="fas fa-star gold-star"></i>
                            <span class="rating-text"><%= (request.getAttribute("topCourseRating") != null) ? request.getAttribute("topCourseRating") : "4.9"%></span>
                        </div>
                        <div class="stat-info">
                            <h3>Top Rated Course</h3>
                            <p class="course-highlight"><%= (request.getAttribute("topCourseName") != null) ? request.getAttribute("topCourseName") : "Java OOP"%></p>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div class="enrollment-count">
                            <%= (request.getAttribute("totalActiveEnrollments") != null) ? request.getAttribute("totalActiveEnrollments") : "16"%>
                        </div>
                        <div class="stat-info">
                            <h3>Active Enrollments</h3>
                            <a href="${pageContext.request.contextPath}/AdminReportServlet" class="report-link-text">
                                Generate Academic Report
                            </a>
                        </div>
                    </div>
                </div>

                <%-- DASHBOARD SHOWING CLASSES THAT DAY --%>

                <div class="schedule-container">
                    <div class="table-header-flex">
                        <div style="display: flex; align-items: center; gap: 15px;">
                            <h3>Today's Academic Schedule</h3>
                            <span class="badge-count">
                                <%= java.time.format.DateTimeFormatter.ofPattern("EEEE, dd MMM").format(java.time.LocalDate.now())%>
                            </span>
                        </div>
                        <a href="${pageContext.request.contextPath}/CourseServlet" class="view-all-link">Manage All <i class="fas fa-arrow-right"></i></a>
                    </div>

                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th>Time</th>
                                <th>Course Name</th>
                                <th>Code</th>
                                <th>Enrolled</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%@page import="com.sems.model.Course, java.util.List"%>
                            <%
                                List<Course> today = (List<Course>) request.getAttribute("todayClasses");
                                if (today != null && !today.isEmpty()) {
                                    for (Course c : today) {
                            %>
                            <tr>
                                <%-- Matches getCourseTime() in your Course.java --%>
                                <td><strong class="time-text"><%= c.getCourseTime()%></strong></td>

                                <td><%= c.getCourseName()%></td>

                                <td><span class="code-badge"><%= c.getCourseCode()%></span></td>

                                <td>
                                    <div class="capacity-bar-text">
                                        <%-- Matches getEnrolledCount() in your Course.java --%>
                                        <%= c.getEnrolledCount()%> / <%= c.getCapacity()%>
                                    </div>
                                </td>

                                <td>
                                    <%-- Logic using the correct model variable --%>
                                    <% if (c.getEnrolledCount() >= c.getCapacity()) { %>
                                    <span class="status-badge inactive">FULL</span>
                                    <% } else { %>
                                    <span class="status-badge active">AVAILABLE</span>
                                    <% } %>
                                </td>
                            </tr>
                            <%
                                }
                            } else {
                            %>
                            <tr>
                                <td colspan="5" class="empty-state">
                                    <i class="fas fa-calendar-day"></i>
                                    <p>No classes scheduled for today.</p>
                                </td>
                            </tr>
                            <% }%>
                        </tbody>
                    </table>
                </div>
            </main>

            <aside class="right-panel">
                <%
                    // 1. Setup DAO and get IDs from session
                    com.sems.dao.StudentDAO sidebarDao = new com.sems.dao.StudentDAO();
                    Integer sideUid = (Integer) session.getAttribute("userId");

                    // 2. Check for photo using our new DAO helper
                    boolean sideHasPhoto = (sideUid != null) && sidebarDao.hasProfilePhotoByUserId(sideUid);
                    com.sems.model.Student adminProfile = (sideUid != null) ? sidebarDao.getStudentByUserId(sideUid) : null;
                    String fullName = "Administrator";
                    if (adminProfile != null) {
                        fullName = adminProfile.getFirstName() + " " + adminProfile.getLastName();
                    }
                %>

                <%-- We keep the exact class 'profile-avatar' to use your existing CSS --%>
                <div class="profile-avatar">
                    <% if (sideHasPhoto) {%>
                    <%-- Pointing to ImageServlet using userId --%>
                    <img src="${pageContext.request.contextPath}/ImageServlet?userId=<%= sideUid%>" alt="Admin Photo">
                    <% } else { %>
                    <i class="fas fa-user-tie"></i>
                    <% }%>
                </div>
                <h2 class="profile-name"><%= fullName%></h2>
                <p class="profile-id"  style="margin-top: 4px;">Level: Full Access</p>

                <%
                    // Retrieve the values set by the Servlet
                    Integer pendingCountObj = (Integer) request.getAttribute("pendingCount");
                    int pendingCount = (pendingCountObj != null) ? pendingCountObj : 0;

                    Double campusAvgObj = (Double) request.getAttribute("campusAvg");
                    double campusAvg = (campusAvgObj != null) ? campusAvgObj : 0.0;
                %>
                <div class="term-info-card">
                    <h4><i class="fas fa-info-circle"></i> System Notice</h4>
                    <p style="font-size: 13px; color: #475569; line-height: 1.5;">
                        Welcome back! There are currently <strong><%= pendingCount%></strong> pending student applications requiring your review. 
                    </p>
                </div>

                <a href="${pageContext.request.contextPath}/auth/LogoutServlet" class="btn-logout">
                    <i class="fas fa-sign-out-alt"></i> Log Out
                </a>
            </aside>
        </div>
    </body>
</html>