<%-- 
    Document   : dashboard
    Created on : 18 Dec 2025, 12:33:30 pm
    Author     : maisarahabjalil
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Student Dashboard - SEMS</title>
    </head>
    <body>
        <%
            // 1. Check if the user is actually logged in by looking at the session
            String username = (String) session.getAttribute("username");
            
            // 2. If no session exists, kick them back to the login page
            if (username == null) {
                response.sendRedirect("../login.jsp");
            }
        %>

        <h1>Welcome to the Dashboard!</h1>
        <p>You are logged in as: <strong><%= username %></strong></p>
        
        <hr>
        
        <h3>Quick Actions:</h3>
        <ul>
            <li><a href="#">View My Profile</a></li>
            <li><a href="#">Enroll in Courses</a></li>
            <li><a href="${pageContext.request.contextPath}/LogoutServlet">Logout</a></li>
        </ul>
        
    </body>
</html>