<%-- 
    Document   : register
    Created on : 22 Dec 2025, 1:48:06 am
    Author     : maisarahabjalil
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>SEMS - Register</title>

        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/register.css">
    </head>
    <body>
        <div class="register-container">
            <div class="logo-area">
                
                <h2 style="margin-top: 15px; color: #333;">Create Account</h2>
            </div>

            <%
                String error = (String) request.getAttribute("errorMessage");
                if (error != null) {
            %>
            <p style="color: #ff4d4f; font-size: 14px; margin-bottom: 15px; font-weight: bold;">
                <%= error%>
            </p>
            <% }%>

            <form action="${pageContext.request.contextPath}/auth/RegisterServlet" method="POST" id="registerForm">

                <div class="input-group">
                    <label>Account Info</label>
                    <input type="text" name="username" placeholder="Username" required>
                    <input type="password" name="password" id="password" placeholder="Password" required style="margin-top:10px;">
                    <small style="color: #666; font-size: 12px; margin-top: 5px; display: block;">
                        Password must be at least 8 characters with uppercase, number, and special character
                    </small>
                    <div id="passwordError" style="color: #ff4d4f; font-size: 12px; margin-top: 5px; display: none;"></div>
                </div>

                <div class="input-group">
                    <label for="role">Role</label>
                    <select name="role" id="role">
                        <option value="student">Student</option>
                        <option value="admin">Admin</option>
                    </select>
                </div>

                <div class="input-group" id="studentIdGroup">
                    <label for="studentRegId">Student ID Number</label>
                    <input type="number" 
                           name="studentRegId" 
                           id="studentRegId" 
                           step="1" 
                           min="1"
                           placeholder="e.g. 12345"
                           oninput="this.value = this.value.replace(/[^0-9]/g, '');" 
                           >
                </div>

                <hr>

                <div class="input-group">
                    <label>Personal Details</label>
                    <div class="form-row">
                        <input type="text" name="firstName" placeholder="First Name" required>
                        <input type="text" name="lastName" placeholder="Last Name" required>
                    </div>
                    <input type="email" name="email" placeholder="Email Address" required>
                </div>

                <div class="input-group">
                    <label for="dob">Date of Birth</label>
                    <input type="date" name="dob" id="dob" required>
                </div>

                <button type="submit" class="btn-login">Register</button>
            </form>

            <div class="register-link">
                <p>Already have an account? <a href="login.jsp">Log In</a></p>
            </div>

            <footer class="footer">
                &copy; 2025 Barfact University 
            </footer>
        </div>
        <script src="${pageContext.request.contextPath}/js/main.js"></script>
        <script>
            // Password validation on form submit
            document.getElementById('registerForm').addEventListener('submit', function(e) {
                const password = document.getElementById('password').value;
                const errorDiv = document.getElementById('passwordError');
                
                const validationError = validatePassword(password);
                if (validationError) {
                    e.preventDefault();
                    errorDiv.textContent = validationError;
                    errorDiv.style.display = 'block';
                    return false;
                }
                errorDiv.style.display = 'none';
            });
        </script>
    </body>
</html>