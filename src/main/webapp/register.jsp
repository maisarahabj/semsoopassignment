<%-- 
    Document   : register page
    Created on : 18 Dec 2025, 2:58:51 am
    Author     : maisarahabjalil
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>SEMS | Login</title>

    <!-- CSS -->
    <link rel="stylesheet" href="css/style.css">

    <!-- JS -->
    <script defer src="js/main.js"></script>
</head>
<body>

    <div class="login-container">
        <h1>Student Enrollment Management System</h1>

        <form action="LoginServlet" method="post" class="login-form">
            <label>Username</label>
            <input type="text" name="username" required>

            <label>Password</label>
            <input type="password" name="password" required>

            <button type="submit">Login</button>

            <%-- Error message from servlet --%>
            <%
                String error = (String) request.getAttribute("error");
                if (error != null) {
            %>
                <p class="error"><%= error %></p>
            <%
                }
            %>
        </form>
    </div>

</body>
</html>
