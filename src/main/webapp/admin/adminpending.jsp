<%-- 
    Document   : adminpending
    Created on : 23 Dec 2025, 3:48:38 am
    Author     : maisarahabjalil
    
    approving pending neww users
--%>

<%@page import="com.sems.model.User"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Barfact University | Pending Approvals</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/adminCSS/admindash.css">
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
                <a href="${pageContext.request.contextPath}/admin/admincourse.jsp" class="nav-link">
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
                    <p>Approve or reject new account requests to grant system access.</p>
                </div>
                <div class="banner-icon">
                     <i class="fas fa-user-clock"></i>
                </div>
            </div>

            <div class="schedule-container">
                <h3 style="margin-bottom: 20px;">Waiting for Approval</h3>
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th>User ID</th>
                            <th>Username</th>
                            <th>Requested Role</th>
                            <th style="text-align: center;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<User> users = (List<User>) request.getAttribute("pendingUsers");
                            if (users != null && !users.isEmpty()) {
                                for (User u : users) {
                        %>
                        <tr>
                            <td>#<%= u.getUserId() %></td>
                            <td><strong><%= u.getUsername() %></strong></td>
                            <td><span class="role-badge"><%= u.getRole() %></span></td>
                            <td style="text-align: center;">
                                <form action="${pageContext.request.contextPath}/auth/AdminPendingServlet" method="POST" style="display:inline;">
                                    <input type="hidden" name="userId" value="<%= u.getUserId() %>">
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
                            <td colspan="4" style="text-align:center; padding: 40px; color: #888;">
                                <i class="fas fa-check-circle" style="font-size: 24px; display: block; margin-bottom: 10px;"></i>
                                All clear! No pending registrations.
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </main>

        <aside class="right-panel">
            <div class="profile-avatar">
                <i class="fas fa-user-tie"></i>
            </div>
            <h2 class="profile-name">Administrator</h2>
            <p class="profile-id">Session: <%= adminName %></p>
            
            <div class="term-info-card">
                <h4><i class="fas fa-shield-alt"></i> Security Note</h4>
                <p>Users cannot log in until their status is changed to ACTIVE.</p>
            </div>

            <a href="${pageContext.request.contextPath}/auth/LogoutServlet" class="btn-logout">
                <i class="fas fa-sign-out-alt"></i> Log Out
            </a>
        </aside>
    </div>
</body>
</html>