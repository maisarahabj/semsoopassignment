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
            // CHANGES HERE: Using a more robust session check
            String username = (String) session.getAttribute("username");
            
            if (username == null) {
                // CHANGES HERE: Using Context Path for safer redirect if session is missing
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return; 
            }
        %>

        <h1>Welcome to the Dashboard!</h1>
        <p>You are logged in as: <strong><%= username %></strong></p>
        <hr>
        <p>Your login was successful.</p>
    </body>
</html>