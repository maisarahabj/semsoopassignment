<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>SEMS - Login</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    </head>
    <body>
        <div class="login-container">
            <div class="logo-area">
                <div class="temp-logo">SEMS</div>
            </div>

            <form action="${pageContext.request.contextPath}/auth/LoginServlet" method="POST">
                <div class="input-group">
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" placeholder="Username" required>
                </div>
                
                <div class="input-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" placeholder="Your password" required>
                </div>
                
                <% 
                    String error = (String) request.getAttribute("errorMessage");
                    if (error != null) { 
                %>
                    <p style="color: #ff4d4f; font-size: 14px; margin-bottom: 15px; font-weight: bold; text-align: center;">
                        <%= error %>
                    </p>
                <% } %>
                
                <button type="submit" class="btn-login">Log In</button>
            </form>

            <div class="register-link">
                <p>Don't have an account? <a href="register.jsp">Register now</a></p>
            </div>
            
            <footer class="footer">
                &copy; 2025 SEMS 
            </footer>
        </div>
        <script src="${pageContext.request.contextPath}/js/main.js"></script>
    </body>
</html>