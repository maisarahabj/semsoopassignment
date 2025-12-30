<%-- 
    Document   : admingrade
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
        <title>Barfact Admin | Grade Management</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/adminCSS/admindash.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/adminCSS/admingrade.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/studentCSS/viewgrades.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/studentCSS/dashboard.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>
    <style>
        .sidebar {
            height: 100vh;
        }
    </style>
    <body>
        <%
            // 1. Security & Session Check
            if (session.getAttribute("userId") == null || !"admin".equals(session.getAttribute("role"))) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            // 2. Data Retrieval
            Student student = (Student) request.getAttribute("student");
            List<Enrollment> transcript = (List<Enrollment>) request.getAttribute("transcript");
            Double cgpaAttr = (Double) request.getAttribute("cgpa");
            double cgpa = (cgpaAttr != null) ? cgpaAttr : 0.00;
            String adminName = (String) session.getAttribute("username");

            // 3. Initialize Variables for calculation
            int totalCreditsGained = 0;
            double totalGradePoints = 0.0;
        %>

        <div class="dashboard-wrapper">
            <aside class="sidebar">
                <div class="logo-section">
                    <img src="${pageContext.request.contextPath}/assets/cat.png" class="logo-img" style="width: 50px; height: 50px;">
                    <span class="logo-text">Barfact Admin</span>
                </div>
                <nav class="nav-menu">
                    <a href="${pageContext.request.contextPath}/DashboardServlet" class="nav-link"><i class="fas fa-chart-line"></i> Overview</a>
                    <a href="${pageContext.request.contextPath}/SemesterServlet" class="nav-link"><i class="fas fa-calendar-alt"></i> Manage Semesters</a>
                    <a href="${pageContext.request.contextPath}/CourseServlet" class="nav-link"><i class="fas fa-book-open"></i> Manage Courses</a>
                    <a href="${pageContext.request.contextPath}/AdminManageStudentServlet" class="nav-link"><i class="fas fa-user-graduate"></i> Manage Students</a>
                    <a href="${pageContext.request.contextPath}/GradeServlet" class="nav-link active"><i class="fas fa-graduation-cap"></i> Grade Management</a>
                    <a href="${pageContext.request.contextPath}/auth/AdminPendingServlet" class="nav-link"><i class="fas fa-clock"></i> Pending Approvals</a>
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
                        <h1>Grade <span class="highlight-blue">Console</span></h1>
                        <p>Search and update academic results for university students.</p>
                    </div>
                    <div class="banner-icon"><i class="fas fa-user-shield"></i></div>
                </div>

                <div class="schedule-container">
                    <div class="table-header-flex">
                        <form action="${pageContext.request.contextPath}/GradeServlet" method="GET" style="display:flex; align-items:center; gap:10px;">
                            <div class="search-box-container">
                                <i class="fas fa-search"></i>
                                <input type="text" name="studentId" id="gradeSearch" placeholder="Search by Student ID..." required>
                            </div>
                            <button type="submit" class="btn-edit-toggle">Load Profile</button>
                        </form>

                        <% if (student != null) { %>
                        <button type="button" class="btn-edit-toggle" onclick="toggleEditMode()">
                            <i class="fas fa-edit"></i> Edit Grades
                        </button>
                        <% } %>
                    </div>

                    <% if (student != null) {%>
                    <form action="${pageContext.request.contextPath}/UpdateGradeServlet" method="POST" id="gradingForm">
                        <input type="hidden" name="studentId" value="<%= student.getStudentId()%>">

                        <table class="admin-table" id="adminGradeTable">
                            <thead>
                                <tr>
                                    <th>Code</th>
                                    <th>Course Name</th>
                                    <th style="text-align: center;">Credits</th>
                                    <th style="text-align: center;">Grade</th>
                                    <th style="text-align: center;">Point</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    if (transcript != null) {
                                        for (Enrollment e : transcript) {
                                            double points = 0.0;
                                            boolean isGraded = true;

                                            if ("A".equals(e.getGrade())) {
                                                points = 4.0;
                                            } else if ("B".equals(e.getGrade())) {
                                                points = 3.5;
                                            } else if ("C".equals(e.getGrade())) {
                                                points = 2.5;
                                            } else if ("FAIL".equals(e.getGrade())) {
                                                points = 0.0;
                                            } else {
                                                isGraded = false;
                                            }

                                            if (isGraded) {
                                                totalCreditsGained += e.getCredits();
                                                totalGradePoints += (points * e.getCredits());
                                            }
                                %>
                                <tr>
                                    <td><strong><%= e.getCourseCode()%></strong></td>
                                    <td><%= e.getCourseName()%></td>
                                    <td style="text-align: center;"><%= e.getCredits()%></td>
                                    <td style="text-align: center;">
                                        <span class="grade-text"><%= e.getGrade()%></span>
                                        <select name="grade_<%= e.getEnrollmentId()%>" class="grade-select">
                                            <option value="N/A" <%= "N/A".equals(e.getGrade()) ? "selected" : ""%>>N/A</option>
                                            <option value="A" <%= "A".equals(e.getGrade()) ? "selected" : ""%>>A</option>
                                            <option value="B" <%= "B".equals(e.getGrade()) ? "selected" : ""%>>B</option>
                                            <option value="C" <%= "C".equals(e.getGrade()) ? "selected" : ""%>>C</option>
                                            <option value="FAIL" <%= "FAIL".equals(e.getGrade()) ? "selected" : ""%>>FAIL</option>
                                        </select>
                                    </td>
                                    <td style="text-align: center;"><%= String.format("%.2f", points)%></td>
                                </tr>
                                <%
                                        }
                                    }
                                %>
                            </tbody>
                        </table>

                        <div style="display: flex; justify-content: flex-end; margin-top: 20px;">
                            <button type="submit" class="btn-save-changes">Save All Changes</button>
                        </div>
                    </form>

                    <div class="summary-container">
                        <h3>Overall Summary</h3>
                        <table class="summary-table">
                            <tr>
                                <td>Cumulative Credits Gained</td>
                                <td style="text-align: center;"><%= totalCreditsGained%></td>
                            </tr>
                            <tr>
                                <td>Cumulative Grade Point</td>
                                <td style="text-align: center;"><%= String.format("%.2f", totalGradePoints)%></td>
                            </tr>
                            <tr>
                                <td>Cumulative Grade Point Average</td>
                                <td class="center-cell">
                                    <span class="badge-cgpa"><%= String.format("%.2f", cgpa)%></span>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <% } %>
                </div>
            </main>

            <aside class="right-panel">
                <% if (student != null) {%>
                <div class="profile-avatar"><i class="fas fa-user-graduate"></i></div>
                <h2 class="profile-name"><%= student.getFirstName()%> <%= student.getLastName()%></h2>
                <p class="profile-id">ID: #<%= student.getStudentId()%></p>
                <div class="term-info-card">
                    <h4><i class="fas fa-chart-line"></i> Summary</h4>
                    <p>CGPA: <strong><%= String.format("%.2f", cgpa)%></strong></p>
                    <p>Status: <%= student.getStatus()%></p>
                </div>
                <% } else {%>
                <div class="profile-avatar"><i class="fas fa-user-tie"></i></div>
                <h2 class="profile-name">Admin <%= adminName%></h2>
                <p class="profile-id">System Manager</p>
                <% }%>
                <a href="${pageContext.request.contextPath}/auth/LogoutServlet" class="btn-logout">
                    <i class="fas fa-sign-out-alt"></i> Log Out
                </a>
            </aside>
        </div>
        <script src="${pageContext.request.contextPath}/js/admingrade.js"></script>
    </body>
</html>