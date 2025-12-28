<%-- 
    Document   : adminlogs
    Created on : 29 Dec 2025, 1:00:51 am
    Author     : maisarahabjalil
--%>

<%@page import="java.util.List"%>
<%@page import="com.sems.model.ActivityLog"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Barfact Uni | System Logs</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/adminCSS/admindash.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/adminCSS/admincourse.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

        <style>
            /* 1. Wrap Details and Row Expansion */
            .admin-table td:last-child {
                white-space: normal !important;
                word-wrap: break-word;
                line-height: 1.4;
                padding: 12px 15px;
            }

            .admin-table td {
                height: auto;
                vertical-align: middle;
            }

            /* 2. Column Widths */
            .admin-table th:nth-child(1), .admin-table td:nth-child(1) {
                width: 14%;
                text-align: center;
            }
            .admin-table th:nth-child(2), .admin-table td:nth-child(2) {
                width: 14%;
            }
            .admin-table th:nth-child(3), .admin-table td:nth-child(3) {
                width: 7%;
            }
            .admin-table th:nth-child(4), .admin-table td:nth-child(4) {
                width: 11%;
            }
            .admin-table th:nth-child(5), .admin-table td:nth-child(5) {
                width: 18%;
            }
            .admin-table th:nth-child(6), .admin-table td:nth-child(6) {
                width: 36%;
            }

            /* 3. Action Pills & Badges */
            .role-badge {
                background: #f0f7ff;
                color: #007bff;
                padding: 4px 10px;
                border-radius: 6px;
                font-size: 11px;
                text-transform: uppercase;
                font-weight: bold;
            }
            .action-pill {
                padding: 4px 10px;
                border-radius: 6px;
                font-weight: bold;
                font-size: 11px;
                font-family: monospace;
                display: inline-block;
            }
            .pill-delete {
                background: #fff5f5;
                color: #e53e3e;
                border: 1px solid #fed7d7;
            }
            .pill-enroll {
                background: #e6fffa;
                color: #38a169;
                border: 1px solid #b2f5ea;
            }
            .pill-grade  {
                background: #eff6ff;
                color: #2563eb;
                border: 1px solid #dbeafe;
            }
            .pill-info   {
                background: #f8fafc;
                color: #64748b;
                border: 1px solid #e2e8f0;
            }

            .timestamp-text {
                color: #64748b;
                font-size: 0.82rem;
                line-height: 1.4;
            }

            .policy-banner {
                background: rgba(255, 255, 255, 0.15);
                padding: 10px 20px;
                border-radius: 10px;
                border-left: 4px solid #facc15;
                font-size: 0.9rem;
                margin-left: 20px;
            }

            /* 4. SIDEBAR & LOGOUT BUTTON (Matching Student Dashboard CSS) */
            .nav-menu {
                display: flex;
                flex-direction: column;
                height: calc(100% - 100px); /* Adjust based on logo section height */
            }

            .logout-container {
                display: flex;
                justify-items: center;
                margin-top: auto;
                padding: 10px 0;
            }

            .btn-logout {
                display: block;
                width: 90%;
                padding: 12px;
                border-radius: 10px;
                background: linear-gradient(to right, #1890ff, #eb2f96);
                color: white !important;
                font-size: 14px;
                font-weight: bold;
                cursor: pointer;
                text-align: center;
                text-decoration: none;
                transition: opacity 0.3s;
                border: none;
            }

            .btn-logout:hover {
                opacity: 0.9;
                color: white !important;
            }
        </style>
    </head>
    <body>

        <%
            if (session.getAttribute("userId") == null || !"admin".equals(session.getAttribute("role"))) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
        %>

        <div class="dashboard-wrapper">
            <aside class="sidebar">
                <div class="logo-section">
                    <img src="${pageContext.request.contextPath}/assets/cat.png" class="logo-img" style="width: 50px; height: 50px;">
                    <span class="logo-text">Barfact Admin</span>
                </div>
                <nav class="nav-menu">
                    <a href="${pageContext.request.contextPath}/DashboardServlet" class="nav-link"><i class="fas fa-chart-line"></i> Overview</a>
                    <a href="${pageContext.request.contextPath}/CourseServlet?action=manage" class="nav-link"><i class="fas fa-book-open"></i> Manage Courses</a>
                    <a href="${pageContext.request.contextPath}/AdminManageStudentServlet" class="nav-link"><i class="fas fa-user-graduate"></i> Manage Students</a>
                    <a href="${pageContext.request.contextPath}/GradeServlet" class="nav-link"><i class="fas fa-graduation-cap"></i> Grade Management</a>
                    <a href="${pageContext.request.contextPath}/auth/AdminPendingServlet" class="nav-link"><i class="fas fa-clock"></i> Pending Approvals</a>
                    <a href="${pageContext.request.contextPath}/ActivityServlet" class="nav-link active"><i class="fas fa-history"></i> System Logs</a>

                    <div class="logout-container">
                        <a href="${pageContext.request.contextPath}/auth/LogoutServlet" class="btn-logout">
                            <i class="fas fa-sign-out-alt"></i> Log Out
                        </a>
                    </div>
                </nav>
            </aside>

            <main class="main-content">
                <div class="welcome-banner" style="display: flex; justify-content: space-between; align-items: center;">
                    <div class="banner-text">
                        <h1>System <span class="highlight-blue">Audit Logs</span></h1>
                        <p>Detailed tracking of all system transactions and administrative actions.</p>
                    </div>
                    <div class="policy-banner">
                        <strong><i class="fas fa-shield-alt"></i> Log Policy</strong><br>
                        Records are immutable and stored for security audit purposes.
                    </div>
                </div>

                <div class="schedule-container">
                    <div class="table-header-flex">
                        <h3>All Activities</h3>
                        <div class="search-box-container">
                            <i class="fas fa-search"></i>
                            <input type="text" id="logSearch" placeholder="Search logs..." onkeyup="filterLogs()">
                        </div>
                    </div>

                    <table class="admin-table" id="logTable">
                        <thead>
                            <tr>
                                <th>Timestamp</th>
                                <th>Performer</th>
                                <th>ID</th>
                                <th>Role</th>
                                <th>Action</th>
                                <th>Details</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="log" items="${logs}">
                                <tr>
                                    <td class="timestamp-text">
                                        <fmt:formatDate value="${log.timestamp}" pattern="dd MMM" var="datePart" />
                                        <fmt:formatDate value="${log.timestamp}" pattern="HH:mm:ss" var="timePart" />
                                        <strong>${datePart}</strong><br>${timePart}
                                    </td>
                                    <td><strong>${log.performerName}</strong></td>
                                    <td>#${log.userId}</td>
                                    <td>
                                        <span class="role-badge">${log.performerRole}</span>
                                    </td>
                                    <td>
                                        <c:set var="pill" value="pill-info" />
                                        <c:if test="${log.actionType.contains('DELETE') || log.actionType.contains('REJECT')}"><c:set var="pill" value="pill-delete" /></c:if>
                                        <c:if test="${log.actionType.contains('ENROLL') || log.actionType.contains('APPROVE')}"><c:set var="pill" value="pill-enroll" /></c:if>
                                        <c:if test="${log.actionType.contains('GRADE')}"><c:set var="pill" value="pill-grade" /></c:if>
                                        <span class="action-pill ${pill}">${log.actionType}</span>
                                    </td>
                                    <td>${log.description}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </main>
        </div>

        <script>
            function filterLogs() {
                let input = document.getElementById("logSearch").value.toUpperCase();
                let table = document.getElementById("logTable");
                let tr = table.getElementsByTagName("tr");
                for (let i = 1; i < tr.length; i++) {
                    let text = tr[i].innerText.toUpperCase();
                    tr[i].style.display = text.indexOf(input) > -1 ? "" : "none";
                }
            }
        </script>
    </body>
</html>