<%-- 
    Document   : dashboard
    Created on : 18 Dec 2025, 12:33:38 pm
    Author     : maisarahabjalil
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dashboard | Barfact University</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/dashboard.css">
</head>
<body class="dashboard-body">

    <%-- Session Check --%>
    <%
        if (session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
    %>

    <header class="dashboard-header">
        <div class="university-name">Barfact University</div>
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn-logout">Log Out</a>
    </header>

    <div class="dashboard-grid">
        <div class="col-left">
            <div class="card">
                <div class="card-header">Student Profile</div>
                <p>Welcome back, <strong><%= session.getAttribute("username") %></strong></p>
                <p style="color: #666; font-size: 14px;">Student ID: 2025-001</p>
            </div>

            <div class="card">
                <div class="card-header">Current GPA</div>
                <h1 style="font-size: 48px; margin: 10px 0; color: #007bff;">3.85</h1>
                <p style="color: #888;">Credits Earned: 30 / 120</p>
            </div>
        </div>

        <div class="col-right">
            <div class="card" style="border-left: 6px solid #007bff;">
                <div class="card-header">📢 Announcement</div>
                <p><strong>Final Week is coming!</strong></p>
                <p>Remember to register for your examinations. The system closes next Friday.</p>
            </div>

            <div class="card">
                <div class="card-header">Registered Courses</div>
                <table class="course-table">
                    <thead>
                        <tr>
                            <th>Code</th>
                            <th>Title</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>CS101</td>
                            <td>Intro to Programming</td>
                            <td><span class="status-label open">Open</span></td>
                        </tr>
                        <tr>
                            <td>DB202</td>
                            <td>Database Systems</td>
                            <td><span class="status-label full">Full</span></td>
                        </tr>
                    </tbody>
                </table>
                <a href="courseCatalog.jsp" class="btn-action">+ Add More Subjects</a>
            </div>
        </div>
    </div>

</body>
</html>