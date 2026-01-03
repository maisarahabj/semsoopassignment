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
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/adminCSS/adminpending.css?v=2.1">
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
                    <img src="${pageContext.request.contextPath}/assets/cat.png" class="logo-img"style="width: 50px; height: 50px; ">
                    <span class="logo-text">Barfact Admin</span>
                </div>
                <nav class="nav-menu">
                    <a href="${pageContext.request.contextPath}/DashboardServlet" class="nav-link">
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
                    <a href="${pageContext.request.contextPath}/auth/AdminPendingServlet" class="nav-link active">
                        <i class="fas fa-clock"></i> Pending Approvals
                    </a>
                    <a href="${pageContext.request.contextPath}/ActivityServlet" class="nav-link">
                        <i class="fas fa-history"></i> System Logs
                    </a>
                    <a href="${pageContext.request.contextPath}/AdminReportServlet" class="nav-link">
                        <i class="fas fa-file-alt"></i> Academic Report
                    </a>
                    <a href="${pageContext.request.contextPath}/ProfileServlet" class="nav-link">
                        <i class="fas fa-user-shield"></i> My Account
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
                        <div class="view-toggle-container">
                            <button class="toggle-btn active" id="btnPending" onclick="switchView('pending')">
                                <i class="fas fa-clock"></i> Pending Requests
                                <span class="badge-count"><%= ((List) request.getAttribute("pendingUsers")).size()%></span>
                            </button>
                            <button class="toggle-btn" id="btnRejected" onclick="switchView('rejected')">
                                <i class="fas fa-user-times"></i> Rejected List
                                <span class="badge-count" style="background: #fff5f5; color: #e53e3e;">
                                    <%= ((List) request.getAttribute("rejectedUsers")).size()%>
                                </span>
                            </button>
                        </div>
                    </div>

                    <div id="pendingView">
                        <table class="admin-table">
                            <thead>
                                <tr>
                                    <th>ID</th> <th>Username</th> <th>Role</th> <th style="text-align: center;">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    List<Map<String, Object>> pUsers = (List<Map<String, Object>>) request.getAttribute("pendingUsers");
                                    if (pUsers != null && !pUsers.isEmpty()) {
                                        for (Map<String, Object> u : pUsers) {
                                %>
                                <tr>
                                    <td>#<%= u.get("studentId")%></td>
                                    <td><strong><%= u.get("username")%></strong></td>
                                    <td><span class="role-badge"><%= u.get("role")%></span></td>
                                    <td style="text-align: center;">
                                        <form action="${pageContext.request.contextPath}/auth/AdminPendingServlet" method="POST" class="action-form">
                                            <input type="hidden" name="userId" value="<%= u.get("userId")%>">
                                            <button name="action" value="APPROVE" class="btn-approve"><i class="fas fa-check"></i> Approve</button>
                                            <button type="button" class="btn-reject" onclick="openRejectModal('<%= u.get("userId")%>')"><i class="fas fa-times"></i> Reject</button>
                                        </form>
                                    </td>
                                </tr>
                                <% }
                                } else { %>
                                <tr><td colspan="4" class="empty-state"><p>No pending requests.</p></td></tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>

                    <div id="rejectedView" class="hidden-view">
                        <table class="admin-table">
                            <thead>
                                <tr>
                                    <th>ID</th> <th>Username</th> <th>Rejection Reason</th> <th style="text-align: center;">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    List<Map<String, Object>> rUsers = (List<Map<String, Object>>) request.getAttribute("rejectedUsers");
                                    if (rUsers != null && !rUsers.isEmpty()) {
                                        for (Map<String, Object> u : rUsers) {
                                %>
                                <tr>
                                    <td>#<%= u.get("studentId")%></td>
                                    <td><strong><%= u.get("username")%></strong></td>
                                    <td><span class="reason-text" title="<%= u.get("reason")%>"><%= u.get("reason")%></span></td>
                                    <td style="text-align: center;">
                                        <form action="${pageContext.request.contextPath}/auth/AdminPendingServlet" method="POST" class="action-form">
                                            <input type="hidden" name="userId" value="<%= u.get("userId")%>">
                                            <button name="action" value="APPROVE" class="btn-approve" style="background: #f0fdf4;">
                                                <i class="fas fa-undo"></i> Restore
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                                <% }
                                } else { %>
                                <tr><td colspan="4" class="empty-state"><p>No rejected accounts.</p></td></tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </main>

            <aside class="right-panel">
                <%
                    // 1. Setup DAO and get Admin IDs from session
                    com.sems.dao.StudentDAO sidebarDao = new com.sems.dao.StudentDAO();
                    Integer adminUid = (Integer) session.getAttribute("userId");

                    // 2. Fetch the actual Admin Profile to get the first/last name
                    com.sems.model.Student adminProfile = (adminUid != null) ? sidebarDao.getStudentByUserId(adminUid) : null;

                    String displayFullName = "Administrator";
                    if (adminProfile != null) {
                        displayFullName = adminProfile.getFirstName() + " " + adminProfile.getLastName();
                    }

                    // 3. Check for photo
                    boolean sideHasPhoto = (adminUid != null) && sidebarDao.hasProfilePhotoByUserId(adminUid);
                %>

                <div class="profile-avatar">
                    <% if (sideHasPhoto) {%>
                    <img src="${pageContext.request.contextPath}/ImageServlet?userId=<%= adminUid%>" alt="Admin Photo">
                    <% } else { %>
                    <i class="fas fa-user-tie"></i>
                    <% }%>
                </div>

                <%-- Display the real full name here --%>
                <h2 class="profile-name"><%= displayFullName%></h2>
                <p class="profile-id" style="margin-top: 4px;">Role: Admin</p>

                <div class="term-info-card" style="text-align: left;">
                    <h4><i class="fas fa-shield-alt"></i> Security Portal</h4>
                    <p>Pending users cannot log in.</p>
                    <p>Rejection deletes the temporary account.</p>
                    <p>Approval grants immediate access.</p>
                </div>

                <a href="${pageContext.request.contextPath}/auth/LogoutServlet" class="btn-logout">
                    <i class="fas fa-sign-out-alt"></i> Log Out
                </a>
            </aside>
            <div id="rejectModal" class="modal-overlay" style="display:none;">
                <div class="modal-box" style="width: 400px;">
                    <div class="modal-icon" style="color: #e53e3e; background: #fff5f5;">
                        <i class="fas fa-comment-slash"></i>
                    </div>
                    <h3>Rejection Reason</h3>
                    <p style="font-size: 13px; color: #666; margin-bottom: 15px;">
                        Please provide a brief reason why this application is being rejected.
                    </p>

                    <form action="${pageContext.request.contextPath}/auth/AdminPendingServlet" method="POST">
                        <input type="hidden" name="action" value="REJECT">
                        <input type="hidden" name="userId" id="rejectUserId">

                        <textarea name="reason" required class="modal-input" 
                                  placeholder="e.g. Invalid Student ID provided, Duplicate account..."
                                  style="width: 100%; height: 100px; padding: 10px; border-radius: 8px; border: 1px solid #eef2f6;"></textarea>

                        <div class="modal-actions" style="margin-top: 20px; justify-content: flex-end;">
                            <button type="button" onclick="closeRejectModal()" class="btn-cancel">Cancel</button>
                            <button type="submit" class="btn-reject" style="margin: 0;">Submit Rejection</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <script>
            function openRejectModal(userId) {
                document.getElementById('rejectUserId').value = userId;
                document.getElementById('rejectModal').style.display = 'flex';
            }

            function closeRejectModal() {
                document.getElementById('rejectModal').style.display = 'none';
            }
            function switchView(view) {
                const pendingView = document.getElementById('pendingView');
                const rejectedView = document.getElementById('rejectedView');
                const btnPending = document.getElementById('btnPending');
                const btnRejected = document.getElementById('btnRejected');

                if (view === 'pending') {
                    pendingView.classList.remove('hidden-view');
                    rejectedView.classList.add('hidden-view');
                    btnPending.classList.add('active');
                    btnRejected.classList.remove('active');
                } else {
                    pendingView.classList.add('hidden-view');
                    rejectedView.classList.remove('hidden-view');
                    btnPending.classList.remove('active');
                    btnRejected.classList.add('active');
                }
            }
        </script>
    </body>
</html>