<%-- 
    Document   : adminpending
    Created on : 23 Dec 2025, 3:48:38 am
    Author     : maisarahabjalil
    
    approving pending neww users
--%>

<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Barfact University | Registration Queue</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/adminCSS/admindash.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/adminCSS/adminpending.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <%
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
                <a href="${pageContext.request.contextPath}/DashboardServlet" class="nav-link">
                    <i class="fas fa-chart-line"></i> Overview
                </a>
                <a href="${pageContext.request.contextPath}/CourseServlet" class="nav-link">
                    <i class="fas fa-book-open"></i> Manage Courses
                </a>
                <a href="${pageContext.request.contextPath}/admin/adminstudent.jsp" class="nav-link">
                    <i class="fas fa-user-graduate"></i> Manage Students
                </a>
                <a href="${pageContext.request.contextPath}/auth/AdminPendingServlet" class="nav-link active">
                    <i class="fas fa-clock"></i> Pending Approvals
                </a>
            </nav>
        </aside>

        <main class="main-content">
            <div class="welcome-banner">
                <div class="banner-text">
                    <h1>Registration <span class="highlight-blue">Queue</span></h1>
                    <p>Process pending account requests to grant system access.</p>
                </div>
                <div class="banner-icon">
                     <i class="fas fa-user-clock"></i>
                </div>
            </div>

            <div class="schedule-container">
                <div class="table-header-flex">
                    <h3 style="margin-bottom: 20px;">Waiting for Approval</h3>
                    <span class="badge-count"><%= (request.getAttribute("pendingUsers") != null) ? ((List)request.getAttribute("pendingUsers")).size() : 0 %> Requests</span>
                </div>
                
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th>ID</th> <th>Username</th>
                            <th>Role</th>
                            <th style="text-align: center;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<Map<String, Object>> users = (List<Map<String, Object>>) request.getAttribute("pendingUsers");
                            if (users != null && !users.isEmpty()) {
                                for (Map<String, Object> u : users) {
                        %>
                        <tr>
                            <td>#<%= u.get("studentId") %></td> <td><div class="user-info-cell"><strong><%= u.get("username") %></strong></div></td>
                            <td><span class="role-badge"><%= u.get("role") %></span></td>
                            <td style="text-align: center;">
                                <form action="${pageContext.request.contextPath}/auth/AdminPendingServlet" method="POST" class="action-form">
                                    <input type="hidden" name="userId" value="<%= u.get("userId") %>">
                                    <button name="action" value="APPROVE" class="btn-approve">
                                        <i class="fas fa-check"></i> Approve
                                    </button>
                                    <button name="action" value="REJECT" class="btn-reject">
                                        <i class="fas fa-times"></i> Reject
                                    </button>
                                </form>
                            </td>
                        </tr>
                        <% 
                                }
                            } else {
                        %>
                        <tr>
                            <td colspan="4" class="empty-state">
                                <i class="fas fa-check-circle"></i>
                                <p>All clear! No pending registrations.</p>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </main>

        <aside class="right-panel">
            <div class="profile-avatar"><i class="fas fa-user-tie"></i></div>
            <h2 class="profile-name">Administrator</h2>
            <p class="profile-id">Session: <%= adminName %></p>
            <div class="term-info-card">
                <h4><i class="fas fa-shield-alt"></i> Security</h4>
                <p>Pending users are blocked from logging in until approved.</p>
            </div>
            <a href="${pageContext.request.contextPath}/auth/LogoutServlet" class="btn-logout">
                <i class="fas fa-sign-out-alt"></i> Log Out
            </a>
        </aside>
    </div>
</body>
</html>