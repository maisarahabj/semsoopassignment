<%-- 
    Document   : Semester Results View
    Created on : Dec 28, 2025
    Author     : SEMS Team
--%>
<%@page import="com.sems.model.Semester"%>
<%@page import="com.sems.model.Enrollment"%>
<%@page import="com.sems.model.Student"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Barfact University | Semester Results</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/studentCSS/viewgrades.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            .semester-selector {
                background: white;
                padding: 20px;
                border-radius: 8px;
                margin-bottom: 20px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            }
            
            .semester-selector select {
                padding: 10px 15px;
                border: 2px solid #667eea;
                border-radius: 5px;
                font-size: 16px;
                min-width: 250px;
                cursor: pointer;
            }
            
            .semester-info {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 20px;
                border-radius: 8px;
                margin-bottom: 20px;
            }
            
            .semester-status {
                display: inline-block;
                padding: 5px 15px;
                background: rgba(255,255,255,0.2);
                border-radius: 20px;
                font-size: 14px;
                margin-left: 10px;
            }
            
            .grades-table {
                width: 100%;
                border-collapse: collapse;
                background: white;
                border-radius: 8px;
                overflow: hidden;
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            }
            
            .grades-table th,
            .grades-table td {
                padding: 15px;
                text-align: left;
                border-bottom: 1px solid #eee;
            }
            
            .grades-table th {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                font-weight: 600;
            }
            
            .grades-table tr:hover {
                background: #f8f9fa;
            }
            
            .grade-badge {
                padding: 5px 12px;
                border-radius: 5px;
                font-weight: 600;
                display: inline-block;
            }
            
            .grade-A { background: #d4edda; color: #155724; }
            .grade-B { background: #d1ecf1; color: #0c5460; }
            .grade-C { background: #fff3cd; color: #856404; }
            .grade-FAIL { background: #f8d7da; color: #721c24; }
            .grade-NA { background: #e2e3e5; color: #383d41; }
            
            .no-results {
                text-align: center;
                padding: 40px;
                color: #666;
            }
            
            .can-evaluate {
                background: #17a2b8;
                color: white;
                padding: 8px 15px;
                border-radius: 5px;
                text-decoration: none;
                font-size: 14px;
                display: inline-block;
                transition: background 0.3s;
            }
            
            .can-evaluate:hover {
                background: #138496;
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
            List<Semester> allSemesters = (List<Semester>) request.getAttribute("allSemesters");
            Semester selectedSemester = (Semester) request.getAttribute("selectedSemester");
            List<Enrollment> enrollments = (List<Enrollment>) request.getAttribute("enrollments");
            Double semesterGPA = (Double) request.getAttribute("semesterGPA");
            request.setAttribute("activePage", "semesterresults");
        %>

        <div class="dashboard-wrapper">
            <%@ include file="/includes/studentSidebar.jsp" %>

            <main class="main-content">
                <div class="welcome-banner">
                    <div class="banner-text">
                        <h1>Semester Results</h1>
                        <p>View your grades and performance by semester</p>
                    </div>
                    <div class="banner-icon">
                        <i class="fas fa-calendar-check"></i>
                    </div>
                </div>

                <div class="semester-selector">
                    <label for="semesterSelect" style="font-weight: 600; margin-right: 15px;">
                        <i class="fas fa-calendar-alt"></i> Select Semester:
                    </label>
                    <select id="semesterSelect" onchange="window.location.href='${pageContext.request.contextPath}/student/SemesterResultsServlet?semesterId=' + this.value">
                        <option value="">-- Choose a Semester --</option>
                        <% if (allSemesters != null) {
                            for (Semester sem : allSemesters) { 
                                String selected = (selectedSemester != null && sem.getSemesterId() == selectedSemester.getSemesterId()) ? "selected" : "";
                        %>
                        <option value="<%= sem.getSemesterId() %>" <%= selected %>>
                            <%= sem.getSemesterName() %> 
                            (<%= sem.getStatus() %>)
                        </option>
                        <% } } %>
                    </select>
                </div>

                <% if (selectedSemester != null) { %>
                <div class="semester-info">
                    <h2>
                        <i class="fas fa-calendar"></i> <%= selectedSemester.getSemesterName() %>
                        <span class="semester-status">
                            <%= selectedSemester.isActive() ? "ACTIVE" : "ENDED" %>
                        </span>
                    </h2>
                    <p style="margin: 10px 0 0 0; opacity: 0.9;">
                        <%= selectedSemester.getStartDate() %> - <%= selectedSemester.getEndDate() %>
                    </p>
                    <% if (semesterGPA != null && semesterGPA > 0) { %>
                    <div style="margin-top: 15px; font-size: 18px;">
                        <i class="fas fa-chart-line"></i> 
                        <strong>Semester GPA: <%= String.format("%.2f", semesterGPA) %></strong>
                    </div>
                    <% } %>
                </div>

                <% if (enrollments != null && !enrollments.isEmpty()) { %>
                <table class="grades-table">
                    <thead>
                        <tr>
                            <th>Course Code</th>
                            <th>Course Name</th>
                            <th>Credits</th>
                            <th>Grade</th>
                            <th>Status</th>
                            <% if (!selectedSemester.isActive()) { %>
                            <th>Actions</th>
                            <% } %>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Enrollment enroll : enrollments) { 
                            String gradeClass = "grade-NA";
                            String gradeDisplay = enroll.getGrade();
                            if (gradeDisplay != null && !gradeDisplay.equals("N/A")) {
                                gradeClass = "grade-" + gradeDisplay.replace("+", "").replace("-", "");
                            }
                        %>
                        <tr>
                            <td><strong><%= enroll.getCourseCode() %></strong></td>
                            <td><%= enroll.getCourseName() %></td>
                            <td><%= enroll.getCredits() %></td>
                            <td>
                                <span class="grade-badge <%= gradeClass %>">
                                    <%= gradeDisplay != null ? gradeDisplay : "N/A" %>
                                </span>
                            </td>
                            <td><%= enroll.getStatus() %></td>
                            <% if (!selectedSemester.isActive() && gradeDisplay != null && !gradeDisplay.equals("N/A")) { %>
                            <td>
                                <a href="${pageContext.request.contextPath}/EvaluationServlet?courseId=<%= enroll.getCourseId() %>" 
                                   class="can-evaluate">
                                    <i class="fas fa-star"></i> Evaluate
                                </a>
                            </td>
                            <% } else if (!selectedSemester.isActive()) { %>
                            <td>
                                <span style="color: #999; font-size: 13px;">Not available</span>
                            </td>
                            <% } %>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                <% } else { %>
                <div class="no-results">
                    <i class="fas fa-inbox" style="font-size: 64px; color: #ccc; margin-bottom: 15px;"></i>
                    <h3>No courses found for this semester</h3>
                    <p>You haven't enrolled in any courses for <%= selectedSemester.getSemesterName() %>.</p>
                </div>
                <% } %>

                <% } else { %>
                <div class="no-results">
                    <i class="fas fa-calendar-times" style="font-size: 64px; color: #ccc; margin-bottom: 15px;"></i>
                    <h3>Please select a semester</h3>
                    <p>Choose a semester from the dropdown above to view your results.</p>
                </div>
                <% } %>

            </main>
        </div>
    </body>
</html>
