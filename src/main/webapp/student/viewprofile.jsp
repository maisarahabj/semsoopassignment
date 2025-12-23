<%-- 
    Document   : viewprofile STUDENT VIEW
    Created on : 22 Dec 2025, 1:49:03 am
    Author     : maisarahabjalil
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sems.model.Student" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Barfact University | My Profile</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/studentCSS/dashboard.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/studentCSS/viewprofile.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <%
        // Security check
        if (session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Student student = (Student) request.getAttribute("student");
        String fullName = (student != null) ? student.getFirstName() + " " + student.getLastName() : "Student";
    %>

    <div class="dashboard-wrapper">
        <aside class="sidebar">
            <div class="logo-section">
                <div class="logo-box"></div>
                <span class="logo-text">Barfact Uni</span>
            </div>

            <nav class="nav-menu">
                <a href="${pageContext.request.contextPath}/DashboardServlet" class="nav-link">
                    <i class="fas fa-home"></i> Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/student/mycourse.jsp" class="nav-link">
                    <i class="fas fa-book"></i> My Classes
                </a>
                <a href="${pageContext.request.contextPath}/student/addcourse.jsp" class="nav-link">
                    <i class="fas fa-plus-square"></i> Add Subjects
                </a>
                <a href="${pageContext.request.contextPath}/ProfileServlet" class="nav-link active">
                    <i class="fas fa-user"></i> Profile
                </a>
            </nav>

            <div class="cgpa-container">
                <div class="cgpa-value"><%= (student != null) ? student.getGpa() : "0.00" %></div>
                <p class="cgpa-label">Current CGPA</p>
            </div>
        </aside>

        <main class="main-content">
            <div class="welcome-banner">
                <div class="banner-text">
                    <h1>My <span class="highlight-blue">Profile</span> Settings</h1>
                    <p>Manage your contact information and personal details.</p>
                </div>
            </div>

            <div class="profile-card-container">
                <form action="${pageContext.request.contextPath}/ProfileServlet" method="POST" class="profile-form">
                    <div class="form-grid">
                        <div class="form-group">
                            <label>First Name</label>
                            <input type="text" value="<%= student.getFirstName() %>" disabled class="disabled-input">
                        </div>
                        <div class="form-group">
                            <label>Last Name</label>
                            <input type="text" value="<%= student.getLastName() %>" disabled class="disabled-input">
                        </div>
                        <div class="form-group">
                            <label>Email Address</label>
                            <input type="email" name="email" value="<%= student.getEmail() %>" required>
                        </div>
                        <div class="form-group">
                            <label>Phone Number</label>
                            <input type="text" name="phone" value="<%= (student.getPhone() != null) ? student.getPhone() : "" %>">
                        </div>
                        <div class="form-group full-width">
                            <label>Home Address</label>
                            <textarea name="address" rows="3"><%= (student.getAddress() != null) ? student.getAddress() : "" %></textarea>
                        </div>
                    </div>
                    
                    <div class="form-actions">
                        <button type="submit" class="btn-save-profile">
                            <i class="fas fa-save"></i> Save Changes
                        </button>
                    </div>
                </form>
            </div>
        </main>

        <aside class="right-panel">
            <div class="profile-avatar">
                <i class="fas fa-user"></i>
            </div>
            <h2 class="profile-name"><%= fullName %></h2>
            <p class="profile-id">Student ID: #<%= (student != null) ? student.getStudentId() : "N/A" %></p>
            
            <div class="term-info-card">
                <h4><i class="fas fa-calendar-alt"></i> Term Info</h4>
                <p>Semester: Dec 2025</p>
                <p>Status: Active</p>
            </div>

            <a href="${pageContext.request.contextPath}/auth/LogoutServlet" class="btn-logout">
                <i class="fas fa-sign-out-alt"></i> Log Out
            </a>
        </aside>
    </div>
</body>
</html>