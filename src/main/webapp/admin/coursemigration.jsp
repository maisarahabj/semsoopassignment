<%-- 
    Document   : Bulk Course Migration
    Created on : Dec 28, 2025
    Author     : SEMS Team
--%>
<%@page import="com.sems.model.Semester"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Barfact University | Course Migration</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/adminCSS/admindash.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            .migration-container {
                background: white;
                padding: 30px;
                border-radius: 10px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                max-width: 800px;
                margin: 20px auto;
            }
            
            .migration-form {
                margin-top: 20px;
            }
            
            .form-group {
                margin-bottom: 20px;
            }
            
            .form-group label {
                display: block;
                font-weight: 600;
                margin-bottom: 8px;
                color: #333;
            }
            
            .form-group select {
                width: 100%;
                padding: 12px;
                border: 2px solid #ddd;
                border-radius: 5px;
                font-size: 16px;
            }
            
            .form-group select:focus {
                border-color: #667eea;
                outline: none;
            }
            
            .btn-migrate {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 12px 30px;
                border: none;
                border-radius: 5px;
                cursor: pointer;
                font-size: 16px;
                font-weight: 600;
                width: 100%;
                transition: transform 0.2s;
            }
            
            .btn-migrate:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
            }
            
            .info-box {
                background: #e3f2fd;
                border-left: 4px solid #2196F3;
                padding: 15px;
                margin-bottom: 20px;
                border-radius: 5px;
            }
            
            .warning-box {
                background: #fff3cd;
                border-left: 4px solid #ffc107;
                padding: 15px;
                margin-bottom: 20px;
                border-radius: 5px;
            }
            
            .migration-icon {
                text-align: center;
                margin: 30px 0;
                font-size: 48px;
                color: #667eea;
            }
            
            .arrow-icon {
                display: flex;
                justify-content: center;
                align-items: center;
                margin: 20px 0;
                font-size: 36px;
                color: #667eea;
            }
        </style>
    </head>
    <body>
        <%
            if (session.getAttribute("userId") == null || !"admin".equals(session.getAttribute("role"))) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            
            List<Semester> allSemesters = (List<Semester>) request.getAttribute("allSemesters");
            String successMessage = (String) request.getAttribute("successMessage");
            String errorMessage = (String) request.getAttribute("errorMessage");
        %>

        <div class="dashboard-wrapper">
            <aside class="sidebar">
                <div class="logo-section">
                    <img src="${pageContext.request.contextPath}/assets/cat.png" class="logo-img" style="width: 50px; height: 50px;">
                    <span class="logo-text">Barfact Admin</span>
                </div>

                <nav class="nav-menu">
                    <a href="${pageContext.request.contextPath}/DashboardServlet" class="nav-link">
                        <i class="fas fa-chart-line"></i> Overview
                    </a>
                    <a href="${pageContext.request.contextPath}/SemesterServlet" class="nav-link">
                        <i class="fas fa-calendar-alt"></i> Manage Semesters
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/BulkCourseMigrationServlet" class="nav-link active">
                        <i class="fas fa-exchange-alt"></i> Course Migration
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
                </nav>
                
                <div class="logout-section">
                    <a href="${pageContext.request.contextPath}/auth/LogoutServlet" class="logout-link">
                        <i class="fas fa-sign-out-alt"></i> Logout
                    </a>
                </div>
            </aside>

            <main class="main-content">
                <div class="welcome-banner">
                    <div class="banner-text">
                        <h1>Bulk Course Migration</h1>
                        <p>Copy courses from one semester to another</p>
                    </div>
                    <div class="banner-icon">
                        <i class="fas fa-exchange-alt"></i>
                    </div>
                </div>

                <% if (successMessage != null) { %>
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i> <%= successMessage %>
                </div>
                <% } %>

                <% if (errorMessage != null) { %>
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-circle"></i> <%= errorMessage %>
                </div>
                <% } %>

                <div class="migration-container">
                    <div class="migration-icon">
                        <i class="fas fa-copy"></i>
                    </div>
                    
                    <div class="info-box">
                        <strong><i class="fas fa-info-circle"></i> How it works:</strong>
                        <p style="margin: 10px 0 0 0;">
                            This tool copies all courses from the source semester to the target semester. 
                            Course codes, names, credits, capacity, days, and times will be copied. 
                            Enrollment counts will be reset to 0.
                        </p>
                    </div>
                    
                    <div class="warning-box">
                        <strong><i class="fas fa-exclamation-triangle"></i> Important:</strong>
                        <p style="margin: 10px 0 0 0;">
                            This creates new course entries in the target semester. 
                            Existing courses in the target semester will not be affected.
                        </p>
                    </div>

                    <form action="${pageContext.request.contextPath}/admin/BulkCourseMigrationServlet" 
                          method="post" class="migration-form" 
                          onsubmit="return confirm('Are you sure you want to migrate courses? This will create copies in the target semester.');">
                        
                        <div class="form-group">
                            <label for="sourceSemester">
                                <i class="fas fa-folder-open"></i> Source Semester (Copy From)
                            </label>
                            <select id="sourceSemester" name="sourceSemesterId" required>
                                <option value="">-- Select Source Semester --</option>
                                <% if (allSemesters != null) {
                                    for (Semester sem : allSemesters) { %>
                                <option value="<%= sem.getSemesterId() %>">
                                    <%= sem.getSemesterName() %> 
                                    (<%= sem.getStartDate() %> - <%= sem.getEndDate() %>)
                                    [<%= sem.getStatus() %>]
                                </option>
                                <% } } %>
                            </select>
                        </div>

                        <div class="arrow-icon">
                            <i class="fas fa-arrow-down"></i>
                        </div>

                        <div class="form-group">
                            <label for="targetSemester">
                                <i class="fas fa-folder"></i> Target Semester (Copy To)
                            </label>
                            <select id="targetSemester" name="targetSemesterId" required>
                                <option value="">-- Select Target Semester --</option>
                                <% if (allSemesters != null) {
                                    for (Semester sem : allSemesters) { %>
                                <option value="<%= sem.getSemesterId() %>">
                                    <%= sem.getSemesterName() %> 
                                    (<%= sem.getStartDate() %> - <%= sem.getEndDate() %>)
                                    [<%= sem.getStatus() %>]
                                </option>
                                <% } } %>
                            </select>
                        </div>

                        <button type="submit" class="btn-migrate">
                            <i class="fas fa-exchange-alt"></i> Migrate Courses
                        </button>
                    </form>

                    <div style="margin-top: 30px; padding: 15px; background: #f8f9fa; border-radius: 5px;">
                        <h4 style="margin: 0 0 10px 0;"><i class="fas fa-lightbulb"></i> Use Cases:</h4>
                        <ul style="margin: 0; padding-left: 20px;">
                            <li>Copy Fall semester courses to Spring semester</li>
                            <li>Replicate course catalog for next academic year</li>
                            <li>Create template semesters for recurring courses</li>
                            <li>Quick setup of new semester with existing course structure</li>
                        </ul>
                    </div>
                </div>

            </main>
        </div>
    </body>
</html>
