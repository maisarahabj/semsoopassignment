<%-- 
    Document   : Admin Semester Management
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
        <title>Barfact University | Semester Management</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/adminCSS/admindash.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            .semester-container {
                background: white;
                padding: 30px;
                border-radius: 10px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                margin: 20px 0;
            }
            
            .semester-form {
                background: #f8f9fa;
                padding: 25px;
                border-radius: 8px;
                margin-bottom: 30px;
            }
            
            .form-row {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                gap: 15px;
                margin-bottom: 15px;
            }
            
            .form-group {
                display: flex;
                flex-direction: column;
            }
            
            .form-group label {
                font-weight: 600;
                margin-bottom: 8px;
                color: #333;
            }
            
            .form-group input {
                padding: 10px;
                border: 1px solid #ddd;
                border-radius: 5px;
                font-size: 14px;
            }
            
            .btn-create {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 12px 30px;
                border: none;
                border-radius: 5px;
                cursor: pointer;
                font-size: 16px;
                font-weight: 600;
                transition: transform 0.2s;
            }
            
            .btn-create:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
            }
            
            .semester-table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 20px;
            }
            
            .semester-table th,
            .semester-table td {
                padding: 15px;
                text-align: left;
                border-bottom: 1px solid #eee;
            }
            
            .semester-table th {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                font-weight: 600;
            }
            
            .semester-table tr:hover {
                background: #f8f9fa;
            }
            
            .status-badge {
                padding: 5px 15px;
                border-radius: 20px;
                font-size: 12px;
                font-weight: 600;
                display: inline-block;
            }
            
            .status-active {
                background: #d4edda;
                color: #155724;
            }
            
            .status-ended {
                background: #f8d7da;
                color: #721c24;
            }
            
            .btn-end {
                background: #dc3545;
                color: white;
                padding: 8px 20px;
                border: none;
                border-radius: 5px;
                cursor: pointer;
                font-size: 14px;
                transition: background 0.3s;
            }
            
            .btn-end:hover {
                background: #c82333;
            }
            
            .btn-end:disabled {
                background: #6c757d;
                cursor: not-allowed;
            }
            
            .alert {
                padding: 15px;
                margin-bottom: 20px;
                border-radius: 5px;
                font-weight: 500;
            }
            
            .alert-success {
                background: #d4edda;
                color: #155724;
                border: 1px solid #c3e6cb;
            }
            
            .alert-error {
                background: #f8d7da;
                color: #721c24;
                border: 1px solid #f5c6cb;
            }
            
            .current-semester-banner {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 20px;
                border-radius: 8px;
                margin-bottom: 20px;
                text-align: center;
            }
            
            .current-semester-banner h2 {
                margin: 0;
                font-size: 24px;
            }
            
            .current-semester-banner p {
                margin: 5px 0 0 0;
                opacity: 0.9;
            }
        </style>
    </head>
    <body>
        <%
            // Security Check: Ensure only Admins can see this
            if (session.getAttribute("userId") == null || !"admin".equals(session.getAttribute("role"))) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            String adminName = (String) session.getAttribute("username");
            
            List<Semester> semesters = (List<Semester>) request.getAttribute("semesters");
            Semester activeSemester = (Semester) request.getAttribute("activeSemester");
            String successMessage = (String) request.getAttribute("successMessage");
            String errorMessage = (String) request.getAttribute("errorMessage");
            request.setAttribute("activePage", "semester");
        %>

        <div class="dashboard-wrapper">
            <%@ include file="/includes/adminSidebar.jsp" %>

            <main class="main-content">
                <div class="welcome-banner">
                    <div class="banner-text">
                        <h1>Semester Management</h1>
                        <p>Create new semesters and manage academic periods</p>
                    </div>
                    <div class="banner-icon">
                        <i class="fas fa-calendar-alt"></i>
                    </div>
                </div>

                <% if (activeSemester != null) { %>
                <div class="current-semester-banner">
                    <h2><i class="fas fa-calendar-check"></i> Current Active Semester</h2>
                    <p><%= activeSemester.getSemesterName() %> 
                    (<%= activeSemester.getStartDate() %> - <%= activeSemester.getEndDate() %>)</p>
                </div>
                <% } else { %>
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-triangle"></i> No active semester! Please create a new semester.
                </div>
                <% } %>

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

                <div class="semester-container">
                    <h2><i class="fas fa-plus-circle"></i> Create New Semester</h2>
                    <form action="${pageContext.request.contextPath}/SemesterServlet" method="post" class="semester-form">
                        <input type="hidden" name="action" value="create">
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="semesterName">Semester Name *</label>
                                <input type="text" id="semesterName" name="semesterName" 
                                       placeholder="e.g., Spring 2025" required>
                            </div>
                            
                            <div class="form-group">
                                <label for="startDate">Start Date *</label>
                                <input type="date" id="startDate" name="startDate" required>
                            </div>
                            
                            <div class="form-group">
                                <label for="endDate">End Date *</label>
                                <input type="date" id="endDate" name="endDate" required>
                            </div>
                        </div>
                        
                        <button type="submit" class="btn-create">
                            <i class="fas fa-plus"></i> Create Semester
                        </button>
                        
                        <p style="margin-top: 10px; font-size: 14px; color: #666;">
                            <i class="fas fa-info-circle"></i> 
                            Note: Creating a new semester will automatically end the current active semester.
                        </p>
                    </form>
                </div>

                <div class="semester-container">
                    <h2><i class="fas fa-list"></i> All Semesters</h2>
                    
                    <% if (semesters != null && !semesters.isEmpty()) { %>
                    <table class="semester-table">
                        <thead>
                            <tr>
                                <th>Semester Name</th>
                                <th>Start Date</th>
                                <th>End Date</th>
                                <th>Status</th>
                                <th>Created Date</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Semester semester : semesters) { %>
                            <tr>
                                <td><strong><%= semester.getSemesterName() %></strong></td>
                                <td><%= semester.getStartDate() %></td>
                                <td><%= semester.getEndDate() %></td>
                                <td>
                                    <span class="status-badge <%= semester.isActive() ? "status-active" : "status-ended" %>">
                                        <%= semester.getStatus() %>
                                    </span>
                                </td>
                                <td><%= semester.getCreatedDate() != null ? semester.getCreatedDate() : "N/A" %></td>
                                <td>
                                    <% if (semester.isActive()) { %>
                                    <form action="${pageContext.request.contextPath}/SemesterServlet" 
                                          method="post" style="display: inline;">
                                        <input type="hidden" name="action" value="end">
                                        <input type="hidden" name="semesterId" value="<%= semester.getSemesterId() %>">
                                        <button type="submit" class="btn-end" 
                                                onclick="return confirm('Are you sure you want to end this semester? This action will finalize all enrollments and grades.')">
                                            <i class="fas fa-stop-circle"></i> End Semester
                                        </button>
                                    </form>
                                    <% } else { %>
                                    <button class="btn-end" disabled>
                                        <i class="fas fa-check"></i> Ended
                                    </button>
                                    <% } %>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                    <% } else { %>
                    <p style="text-align: center; padding: 40px; color: #666;">
                        <i class="fas fa-calendar-times" style="font-size: 48px; display: block; margin-bottom: 15px;"></i>
                        No semesters found. Create your first semester to get started.
                    </p>
                    <% } %>
                </div>

            </main>
        </div>
    </body>
</html>
