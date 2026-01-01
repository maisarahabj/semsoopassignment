<%-- 
    Document   : adminreport
    Created on : 29 Dec 2025
    Author     : maisarahabjalil
--%>

<%@page import="java.util.List, com.sems.model.Course, com.sems.model.Student, java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Barfact Uni | Academic Report</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/adminCSS/admindash.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

        <style>
            /* 1. THE CUTOFF FIX */
            .dashboard-wrapper {
                display: flex;
                min-height: 100vh; /* Changed from height to min-height */
                width: 100%;
                box-sizing: border-box;
                background-color: #f8fafc; /* Restores background */
            }

            .main-content {
                flex: 1;
                /* Removes forced height to allow natural scrolling */
                padding: 30px;
                display: flex;
                flex-direction: column;
                gap: 25px;
                /* Buffer at the bottom so the last table isn't cut off */
                padding-bottom: 100px;
            }

            /* 2. THREE-COLUMN ROW LAYOUT */
            .report-row-3col {
                display: grid;
                grid-template-columns: 1.2fr 0.9fr 0.9fr;
                gap: 20px;
            }

            .stat-card-vertical {
                background: white;
                padding: 20px;
                border-radius: 20px;
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                text-align: center;
                box-shadow: 0 10px 30px rgba(0,0,0,0.02);
            }

            .stat-card-vertical h2 {
                font-size: 2.5rem;
                margin: 5px 0;
                color: #1e293b;
                font-weight: 800;
            }

            .grade-row {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 10px 0;
                border-bottom: 1px solid #f1f5f9;
            }

            .pill-grade {
                background: #eff6ff;
                color: #2563eb;
                padding: 4px 10px;
                border-radius: 6px;
                font-weight: 700;
                font-size: 11px;
            }

            /* Ensure Sidebar stays fixed while content scrolls */
            .sidebar {
                height: 100vh;
                position: sticky;
                top: 0;
            }

            @media print {
                .sidebar, .print-hide, .logout-container {
                    display: none !important;
                }
                .main-content {
                    padding: 0;
                    width: 100%;
                }
                .dashboard-wrapper {
                    display: block;
                }
            }

            #deans-list-container {
                /* Prevents the container from shrinking when data is added */
                flex-shrink: 0;
                display: block;
                width: 100%;

                /* Forces the container to expand based on the rows inside */
                height: auto !important;
                min-height: fit-content;

                /* Adds a margin at the bottom of the container itself for safety */
                margin-bottom: 40px;

                /* Visual polish to match your other cards */
                background: white;
                border-radius: 16px;
                border: 1px solid #eef2f6;
            }

            /* Ensure the table inside doesn't overflow horizontally */
            #deans-list-container .admin-table {
                margin-bottom: 0;
                width: 100%;
            }

            /* Final safeguard for the main content area */
            .main-content {
                /* This provides the necessary "scrolling room" identified in the inspector */
                padding-bottom: 150px !important;
                overflow-y: auto;
            }

        </style>
    </head>
    <body>
        <div class="dashboard-wrapper">
            <aside class="sidebar">
                <div class="logo-section">
                    <img src="${pageContext.request.contextPath}/assets/cat.png" style="width: 50px; height: 50px;">
                    <span class="logo-text">Barfact Admin</span>
                </div>
                <nav class="nav-menu">
                    <a href="${pageContext.request.contextPath}/DashboardServlet" class="nav-link"><i class="fas fa-chart-line"></i> Overview</a>
                    <a href="${pageContext.request.contextPath}/CourseServlet?action=manage" class="nav-link"><i class="fas fa-book-open"></i> Manage Courses</a>
                    <a href="${pageContext.request.contextPath}/AdminManageStudentServlet" class="nav-link"><i class="fas fa-user-graduate"></i> Manage Students</a>
                    <a href="${pageContext.request.contextPath}/GradeServlet" class="nav-link"><i class="fas fa-graduation-cap"></i> Grade Management</a>
                    <a href="${pageContext.request.contextPath}/auth/AdminPendingServlet" class="nav-link"><i class="fas fa-clock"></i> Pending Approvals</a>
                    <a href="${pageContext.request.contextPath}/ActivityServlet" class="nav-link"><i class="fas fa-history"></i> System Logs</a>
                    <a href="${pageContext.request.contextPath}/AdminReportServlet" class="nav-link active"><i class="fas fa-file-alt"></i> Academic Report</a>
                    <a href="${pageContext.request.contextPath}/ProfileServlet" class="nav-link">
                        <i class="fas fa-user-shield"></i> My Account
                    </a>
                </nav>

            </aside>

            <main class="main-content">
                <div class="welcome-banner" style="display: flex; justify-content: space-between; align-items: center;">
                    <div class="banner-text">
                        <h1>Academic <span class="highlight-blue">Status Report</span></h1>
                        <p>Internal analysis generated on <fmt:formatDate value="<%= new java.util.Date()%>" pattern="dd MMM yyyy, HH:mm" /></p>
                    </div>
                    <div class="print-hide">
                        <button onclick="window.print()" class="btn-print" style="cursor:pointer; padding:8px 16px; border-radius:8px; border:none; font-weight:bold;">
                            <i class="fas fa-print"></i> Print PDF
                        </button>
                    </div>
                </div>

                <div class="schedule-container" style="margin-top: 0;">
                    <div class="table-header-flex">
                        <h3><i class="fas fa-table" style="color: #6366f1;"></i> Course Density & Capacity</h3>
                    </div>
                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th>Course Name</th>
                                <th>Enrolled</th>
                                <th>Capacity</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="course" items="${coursePop}">
                                <tr>
                                    <td><strong>${course.courseName}</strong></td>
                                    <td>${course.enrolledCount}</td>
                                    <td>${course.capacity}</td>
                                    <td>
                                        <span class="role-badge" style="${course.enrolledCount >= course.capacity ? 'background:#fff5f5; color:#e53e3e;' : ''}">
                                            ${course.enrolledCount >= course.capacity ? 'AT CAPACITY' : 'AVAILABLE'}
                                        </span>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <div class="report-row-3col">
                    <div class="schedule-container" style="margin: 0;">
                        <div class="table-header-flex"><h3>Grade Breakdown</h3></div>
                        <c:forEach var="entry" items="${gradeDist}">
                            <div class="grade-row">
                                <span class="timestamp-text" style="font-weight: 500;">Tier: ${entry.key}</span>
                                <span class="pill-grade">${entry.value} Students</span>
                            </div>
                        </c:forEach>
                    </div>

                    <div class="stat-card-vertical">
                        <i class="fas fa-graduation-cap" style="color: #007bff; font-size: 1.5rem;"></i>
                        <h2>${avgGpa}</h2>
                        <span class="timestamp-text">Campus Avg GPA</span>
                    </div>

                    <div class="stat-card-vertical">
                        <i class="fas fa-file-signature" style="color: #6366f1; font-size: 1.5rem;"></i>
                        <h2>${totalSeats}</h2>
                        <span class="timestamp-text" style="font-weight: 700; color: #4338ca;">Total Enrollments</span>
                        <p style="font-size: 0.7rem; color: #94a3b8; margin-top: 5px;">Total active seats across all courses</p>
                    </div>
                </div>

                <div class="schedule-container" id="deans-list-container">
                    <div class="table-header-flex">
                        <h3><i class="fas fa-medal" style="color: #facc15;"></i> Dean's List (Top Performers)</h3>
                    </div>
                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th>Student Name</th>
                                <th>Academic Standing</th>
                                <th>Cumulative GPA</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="stu" items="${topStudents}">
                                <tr>
                                    <td><strong>${stu.firstName}</strong></td>
                                    <td><span class="role-badge" style="background: #ecfdf5; color: #059669;">HONORS</span></td>
                                    <td><span class="pill-grade">${stu.gpa}</span></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </main>
        </div>
    </body>
</html>