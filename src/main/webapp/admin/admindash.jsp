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

    <style>
        /* Layout: 2 Columns with fixed height cards */
        .admin-stats-grid-2x2 {
            display: grid;
            grid-template-columns: 1fr 1fr !important;
            gap: 20px;
            margin-top: 25px;
            width: 100%;
        }

        .stat-card {
            background: white;
            padding: 15px 25px; /* Reduced vertical padding for shorter height */
            border-radius: 20px;
            display: flex;
            align-items: center;
            gap: 20px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.02);
            height: 100px; /* Reduced height as requested */
            box-sizing: border-box;
        }

        /* 2. Circular GPA Progress Bar */
        .circular-progress {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
        }

        .inner-circle {
            width: 48px;
            height: 48px;
            background: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .inner-circle span {
            font-size: 13px;
            font-weight: 800;
            color: #007bff;
        }

        /* 3. Star Highlight - Centered Rating Font */
        .star-container {
            position: relative;
            display: flex;
            align-items: center;
            justify-content: center;
            width: 60px;
            height: 60px;
        }

        .gold-star {
            font-size: 55px !important;
            color: #facc15 !important;
            background: none !important;
            padding: 0 !important;
        }

        .rating-text {
            position: absolute;
            /* Perfect centering logic */
            top: 52%;
            left: 50%;
            transform: translate(-50%, -50%);
            font-size: 11px;
            font-weight: 900;
            color: #854d0e;
        }

        /* 4. Active Enrollment Styling */
        .enrollment-count {
            font-size: 36px;
            font-weight: 800;
            color: #6366f1;
            min-width: 60px;
            text-align: center;
        }

        .report-link-text {
            text-decoration: none;
            color: #6366f1;
            font-weight: 700;
            font-size: 0.85rem;
            border-bottom: 2px solid #e0e7ff;
            transition: all 0.2s ease;
        }

        .report-link-text:hover {
            color: #4338ca;
            border-bottom-color: #4338ca;
        }

        /* Text Consistency */
        .stat-info h3 {
            font-size: 0.85rem;
            margin: 0;
        }
        .stat-info p {
            margin-top: 2px;
            font-size: 0.75rem;
            color: #64748b;
        }
        .course-highlight {
            font-weight: 700;
            color: #475569;
            font-size: 0.85rem;
        }

        .main-content {
            flex: 1;
            overflow-y: auto; /* Internal scrolling only */
            padding: 30px;
            display: flex;
            flex-direction: column;
            gap: 5px;
            height: 100vh;
        }

        .schedule-container {
            margin-bottom: 60px;
            flex-shrink: 0;
        }
    </style>

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
                            <a href="${pageContext.request.contextPath}/AcademicReportServlet" class="report-link-text">
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