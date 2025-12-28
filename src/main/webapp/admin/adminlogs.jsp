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
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/adminCSS/adminlogs.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

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
                    <a href="${pageContext.request.contextPath}/auth/AdminPendingServlet" class="nav-link">
                        <i class="fas fa-clock"></i> Pending Approvals
                    </a>
                    <a href="${pageContext.request.contextPath}/ActivityServlet" class="nav-link active">
                        <i class="fas fa-history"></i> System Logs
                    </a>
                    <a href="${pageContext.request.contextPath}/AdminReportServlet" class="nav-link">
                        <i class="fas fa-file-alt"></i> Academic Report
                    </a>
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