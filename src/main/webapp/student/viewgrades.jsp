<%-- 
    Document   : viewGrades
    Created on : 26 Dec 2025
    Author     : maisarahabjalil
--%>

<%@page import="java.util.List"%>
<%@page import="com.sems.model.Enrollment"%>
<%@page import="com.sems.model.Student"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Barfact University | My Results</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/studentCSS/dashboard.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            /* Specific styles for the results table */
            .grade-pill {
                padding: 4px 12px;
                border-radius: 12px;
                font-size: 0.9rem;
                background: #f1f5f9;
            }
            .status-badge {
                font-size: 0.85rem;
                color: #64748b;
                font-weight: 500;
            }
        </style>
    </head>
    <body>
        <%
            if (session.getAttribute("userId") == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            Student student = (Student) request.getAttribute("student");
            String fullName = (student != null) ? student.getFirstName() + " " + student.getLastName() : "Student";
            Object gpaObj = request.getAttribute("cgpa");
            String displayGpa = (gpaObj != null) ? String.format("%.2f", (Double)gpaObj) : "0.00";
        %>

        <div class="dashboard-wrapper">
            <aside class="sidebar">
                <div class="logo-section">
                    <img src="${pageContext.request.contextPath}/assets/cat.png" class="logo-img" style="width: 50px; height: 50px;">
                    <span class="logo-text">Barfact Uni</span>
                </div>

                <nav class="nav-menu">
                    <a href="${pageContext.request.contextPath}/DashboardServlet" class="nav-link">
                        <i class="fas fa-home"></i> Dashboard
                    </a>
                    <a href="${pageContext.request.contextPath}/student/MyCourseServlet" class="nav-link">
                        <i class="fas fa-book"></i> My Classes
                    </a>
                    <a href="${pageContext.request.contextPath}/student/AddCourseServlet" class="nav-link">
                        <i class="fas fa-plus-square"></i> Add Subjects
                    </a>
                    <a href="${pageContext.request.contextPath}/student/GradeServlet" class="nav-link active">
                        <i class="fas fa-poll-h"></i> My Results
                    </a>
                    <a href="${pageContext.request.contextPath}/ProfileServlet" class="nav-link">
                        <i class="fas fa-user"></i> Profile
                    </a>
                </nav>

                <div class="cgpa-container">
                    <div class="cgpa-value"><%= displayGpa %></div>
                    <p class="cgpa-label">Current CGPA</p>
                </div>
            </aside>

            <main class="main-content">
                <div class="welcome-banner">
                    <div class="banner-text">
                        <h1>Academic <span class="highlight-blue">Results</span></h1>
                        <p>View your performance summary and official transcript details.</p>
                    </div>
                    <div class="banner-icon">
                        <i class="fas fa-file-invoice"></i>
                    </div>
                </div>

                <div class="schedule-container">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                        <h3>Academic Transcript</h3>
                        <span class="badge-count" style="background: #eef2ff; color: #4f46e5; padding: 5px 15px; border-radius: 20px; font-weight: 600;">
                            Official GPA: <%= displayGpa %>
                        </span>
                    </div>

                    <table class="admin-table" style="width: 100%; border-collapse: collapse;">
                        <thead>
                            <tr style="background: #f8fafc; text-align: left; border-bottom: 2px solid #edf2f7;">
                                <th style="padding: 15px; color: #475569;">Course Code</th>
                                <th style="padding: 15px; color: #475569;">Course Name</th>
                                <th style="padding: 15px; color: #475569;">Status</th>
                                <th style="padding: 15px; color: #475569;">Grade</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% 
                                List<com.sems.model.Enrollment> transcript = (List<com.sems.model.Enrollment>) request.getAttribute("transcript");
                                if (transcript != null && !transcript.isEmpty()) {
                                    for (com.sems.model.Enrollment e : transcript) { 
                            %>
                            <tr style="border-bottom: 1px solid #f1f5f9;">
                                <td style="padding: 15px;"><strong><%= e.getCourseCode() %></strong></td>
                                <td style="padding: 15px;"><%= e.getCourseName() %></td>
                                <td style="padding: 15px;">
                                    <span class="status-badge" style="text-transform: capitalize;">
                                        <%= e.getStatus() %>
                                    </span>
                                </td>
                                <td style="padding: 15px;">
                                    <% 
                                        String gradeColor = "#10b981"; // Default Green
                                        if ("FAIL".equals(e.getGrade())) gradeColor = "#ef4444"; // Red
                                        if ("exempted".equals(e.getGrade())) gradeColor = "#6366f1"; // Indigo
                                    %>
                                    <span class="grade-pill" style="font-weight: bold; color: <%= gradeColor %>;">
                                        <%= e.getGrade() %>
                                    </span>
                                </td>
                            </tr>
                            <% 
                                    }
                                } else { 
                            %>
                            <tr>
                                <td colspan="4" style="text-align: center; padding: 40px; color: #94a3b8;">
                                    <i class="fas fa-folder-open" style="font-size: 2rem; display: block; margin-bottom: 10px;"></i>
                                    No graded results available yet.
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
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
                    <p>Status: <%= (student != null) ? student.getStatus() : "Active" %></p>
                </div>

                <a href="${pageContext.request.contextPath}/auth/LogoutServlet" class="btn-logout">
                    <i class="fas fa-sign-out-alt"></i> Log Out
                </a>
            </aside>
        </div>
    </body>
</html>