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
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/adminCSS/admindash.css">
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
                    <div class="logo-box"></div>
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

                    <a href="${pageContext.request.contextPath}/auth/AdminPendingServlet" class="nav-link">
                        <i class="fas fa-clock"></i> Pending Approvals
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

                <div class="admin-stats-grid">
                    <div class="stat-card">
                        <i class="fas fa-users"></i>
                        <div class="stat-info">
                            <h3>Total Students</h3>
                            <p>View and edit all student records</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <i class="fas fa-layer-group"></i>
                        <div class="stat-info">
                            <h3>Active Courses</h3>
                            <p>Add, edit, or remove course offerings</p>
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
                <div class="profile-avatar">
                    <i class="fas fa-user-tie"></i>
                </div>
                <h2 class="profile-name">Administrator</h2>
                <p class="profile-id">Level: Full Access</p>

                <div class="term-info-card">
                    <h4><i class="fas fa-tools"></i> Quick Actions</h4>
                    <p>Register New Student</p>
                    <p>Generate Report</p>
                </div>

                <a href="${pageContext.request.contextPath}/auth/LogoutServlet" class="btn-logout">
                    <i class="fas fa-sign-out-alt"></i> Log Out
                </a>
            </aside>
        </div>
    </body>
</html>