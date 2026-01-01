<%-- 
    Document   : viewgrades
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
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/studentCSS/dashboard.css?v=2.1">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/studentCSS/viewgrades.css?v=2.1">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

    </head>
    <body>
        <%
            if (session.getAttribute("userId") == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            Student student = (Student) request.getAttribute("student");
            String fullName = (student != null) ? student.getFirstName() + " " + student.getLastName() : "Student";
            String role = (String) session.getAttribute("role");
            Double cgpaAttr = (Double) request.getAttribute("cgpa");
            double cgpa = (cgpaAttr != null) ? cgpaAttr : 0.00;

            List<Enrollment> transcript = (List<Enrollment>) request.getAttribute("transcript");

            int totalCreditsGained = 0;
            double totalGradePoints = 0.0;
        %>

        <div class="dashboard-wrapper">
            <aside class="sidebar">
                <div class="logo-section">
                    <img src="${pageContext.request.contextPath}/assets/cat.png" class="logo-img" style="width: 50px; height: 50px;">
                    <span class="logo-text">Barfact Uni</span>
                </div>
                <nav class="nav-menu">
                    <a href="${pageContext.request.contextPath}/DashboardServlet" class="nav-link"><i class="fas fa-home"></i> Dashboard</a>
                    <a href="${pageContext.request.contextPath}/student/MyCourseServlet" class="nav-link"><i class="fas fa-book"></i> My Classes</a>
                    <a href="${pageContext.request.contextPath}/student/AddCourseServlet" class="nav-link"><i class="fas fa-plus-square"></i> Add Subjects</a>
                    <a href="${pageContext.request.contextPath}/GradeServlet" class="nav-link active"><i class="fas fa-poll-h"></i> My Results</a>
                    <a href="${pageContext.request.contextPath}/EvaluationServlet" class="nav-link">
                        <i class="fas fa-star"></i> Course Evaluation
                    </a>
                    <a href="${pageContext.request.contextPath}/ProfileServlet" class="nav-link"><i class="fas fa-user"></i> Profile</a>
                </nav>
                <div class="cgpa-container">
                    <div class="cgpa-value"><%= String.format("%.2f", cgpa)%></div>
                    <p class="cgpa-label">Current CGPA</p>
                </div>
            </aside>

            <main class="main-content">
                <div class="welcome-banner">
                    <div class="banner-text">
                        <h1>Academic <span class="highlight-blue">Transcript</span></h1>
                        <p>Detailed breakdown of your academic performance.</p>
                    </div>
                    <div class="banner-icon">
                        <i class="fas fa-file-invoice"></i>
                    </div>
                </div>

                <div class="schedule-container">
                    <table class="admin-table" style="width: 100%; border-collapse: collapse;">
                        <thead>
                            <tr style="background: #f8fafc; text-align: left; border-bottom: 2px solid #edf2f7;">
                                <th style="padding: 15px;">Subject Code</th>
                                <th style="padding: 15px;">Subject Name</th>
                                <th style="padding: 15px; text-align: center;">Credit Hour</th>
                                <th style="padding: 15px; text-align: center;">Grade</th>
                                <th style="padding: 15px; text-align: center;">Point</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (transcript != null && !transcript.isEmpty()) {
                                    for (Enrollment e : transcript) {
                                        double points = 0.0;
                                        if ("A".equals(e.getGrade())) {
                                            points = 4.0;
                                        } else if ("B".equals(e.getGrade())) {
                                            points = 3.5;
                                        } else if ("C".equals(e.getGrade())) {
                                            points = 2.5;
                                        } else if ("FAIL".equals(e.getGrade())) {
                                            points = 0.0;
                                        }

                                        if (!"N/A".equals(e.getGrade()) && !"exempted".equals(e.getGrade())) {
                                            totalCreditsGained += e.getCredits();
                                            totalGradePoints += (points * e.getCredits());
                                        }
                            %>
                            <tr class="grade-row" data-grade="<%= e.getGrade()%>" style="border-bottom: 1px solid #f1f5f9;">
                                <td style="padding: 15px;"><strong><%= e.getCourseCode()%></strong></td>
                                <td style="padding: 15px;"><%= e.getCourseName()%></td>
                                <td style="padding: 15px; text-align: center;"><%= e.getCredits()%></td>
                                <td style="padding: 15px; text-align: center;"><%= e.getGrade()%></td>
                                <td style="padding: 15px; text-align: center;"><%= String.format("%.2f", points)%></td>
                            </tr>
                            <%      }
                            } else { %>
                            <tr><td colspan="5" style="padding:20px; text-align:center;">No transcript records found.</td></tr>
                            <%  }%>
                        </tbody>
                    </table>

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
                </div>
            </main>

            <aside class="right-panel">

                <%
                    // --- JAVA LOGIC FOR IMAGE CHECKING ---
                    com.sems.dao.StudentDAO sidebarDao = new com.sems.dao.StudentDAO();
                    Integer sidebarStudentId = (Integer) session.getAttribute("studentId");
                    boolean showSidebarPhoto = (sidebarStudentId != null) && sidebarDao.hasProfilePhoto(sidebarStudentId);
                %>

                <div class="profile-avatar profile-avatar-side">
                    <% if (showSidebarPhoto) { %>
                    <img src="${pageContext.request.contextPath}/ImageServlet?userId=${sessionScope.userId}" alt="Profile Photo">
                    <% } else { %>
                    <i class="fas fa-user"></i>
                    <% }%>
                </div>

                <h2 class="profile-name"><%= (student != null) ? student.getFirstName() + " " + student.getLastName() : "Student"%></h2>
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

        <script src="${pageContext.request.contextPath}/js/viewgrades.js"></script>
    </body>
</html>