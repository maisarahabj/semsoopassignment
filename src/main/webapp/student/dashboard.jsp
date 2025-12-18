<%-- 
    Document   : dashboard
    Created on : 18 Dec 2025, 12:33:38 pm
    Author     : maisarahabjalil
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page session="true" %>

<%
    String username = (String) session.getAttribute("username");
    if (username == null) {
        response.sendRedirect("../index.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Student Dashboard</title>
    <link rel="stylesheet" href="../css/style.css">
</head>
<body>

    <div class="dashboard">
        <h2>Welcome, <%= username %></h2>

        <ul class="dashboard-menu">
            <li><a href="courses.jsp">My Courses</a></li>
            <li><a href="enroll.jsp">Enroll Course</a></li>
            <li><a href="grades.jsp">View Grades</a></li>
            <li><a href="../LogoutServlet">Logout</a></li>
        </ul>
    </div>

</body>
</html>
