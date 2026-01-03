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
            request.setAttribute("activePage", "profile");
        %>

        <div class="dashboard-wrapper">
            <%@ include file="/includes/studentSidebar.jsp" %>

            <main class="main-content">
                <div class="welcome-banner">
                    <div class="banner-text">
                        <h1>My <span class="highlight-blue">Profile</span> Settings</h1>
                        <p>Manage your contact information and personal details.</p>
                    </div>
                </div>

                <% if ("success".equals(request.getParameter("status"))) { %>
                <div class="alert success-alert" id="successAlert">
                    <i class="fas fa-check-circle"></i> Profile updated successfully!
                </div>
                <% }%>

                <div class="profile-card-container">
                    <form action="${pageContext.request.contextPath}/ProfileServlet" method="POST" 
                          class="profile-form" enctype="multipart/form-data">
                        
                        <!-- Profile Picture Upload Section -->
                        <div class="form-group full-width">
                            <label>Profile Picture</label>
                            <div class="profile-picture-upload">
                                <% 
                                    String profilePicture = student.getProfilePicture();
                                    String displayPicture = (profilePicture != null && !profilePicture.isEmpty()) 
                                        ? request.getContextPath() + "/" + profilePicture 
                                        : request.getContextPath() + "/assets/default-profile.png";
                                %>
                                <img id="profilePreview" src="<%= displayPicture %>" 
                                     alt="Profile Picture" class="profile-picture-preview"
                                     onerror="this.src='${pageContext.request.contextPath}/assets/cat.png'">
                                <input type="file" name="profilePicture" id="profilePicture" 
                                       accept="image/*" onchange="previewProfilePicture(this)">
                                <label for="profilePicture" class="upload-btn">
                                    <i class="fas fa-camera"></i> Change Photo
                                </label>
                            </div>
                        </div>
                        
                        <div class="form-grid">

                            <div class="form-group">
                                <label>First Name</label>
                                <p class="view-text"><%= student.getFirstName()%></p>
                            </div>

                            <div class="form-group">
                                <label>Last Name</label>
                                <p class="view-text"><%= student.getLastName()%></p>
                            </div>

                            <div class="form-group">
                                <label>Date of Birth</label>
                                <p class="view-text"><%= student.getDob()%></p>
                            </div>

                            <div class="form-group">
                                <label>Registered On</label>
                                <p class="view-text">
                                    <%= (student.getEnrollmentDate() != null) ? student.getEnrollmentDate() : "N/A"%>
                                </p>
                            </div>

                            <div class="form-group">
                                <label>Email Address</label>
                                <div class="editable-input-group">
                                    <p class="display-value"><%= student.getEmail()%></p>
                                    <input type="email" name="email" value="<%= student.getEmail()%>" 
                                           class="edit-field hidden" required>
                                    <button type="button" class="inline-edit-btn" onclick="toggleFieldEdit(this)">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                </div>
                            </div>

                            <div class="form-group">
                                <label>Phone Number</label>
                                <div class="editable-input-group">
                                    <p class="display-value">
                                        <%= (student.getPhone() != null && !student.getPhone().isEmpty()) ? student.getPhone() : "Not set"%>
                                    </p>
                                    <input type="text" name="phone" value="<%= (student.getPhone() != null) ? student.getPhone() : ""%>" 
                                           class="edit-field hidden">
                                    <button type="button" class="inline-edit-btn" onclick="toggleFieldEdit(this)">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                </div>
                            </div>

                            <div class="form-group full-width">
                                <label>Home Address</label>
                                <div class="editable-input-group">
                                    <p class="display-value">
                                        <%= (student.getAddress() != null && !student.getAddress().isEmpty()) ? student.getAddress() : "Not set"%>
                                    </p>
                                    <textarea name="address" rows="2" class="edit-field hidden"><%= (student.getAddress() != null) ? student.getAddress() : ""%></textarea>
                                    <button type="button" class="inline-edit-btn" onclick="toggleFieldEdit(this)">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                </div>
                            </div>
                        </div>

                        <div id="formActions" class="form-actions hidden">
                            <hr>
                            <button type="submit" class="btn-save-profile">Save Changes</button>
                            <button type="button" class="btn-cancel" onclick="window.location.reload()">Cancel</button>
                        </div>
                    </form>
                </div>
            </main>

            <aside class="right-panel">
                <div class="profile-avatar">
                    <% 
                        String profilePicture = student.getProfilePicture();
                        if (profilePicture != null && !profilePicture.isEmpty()) {
                    %>
                        <img src="${pageContext.request.contextPath}/<%= profilePicture %>" 
                             alt="Profile" style="width: 100%; height: 100%; object-fit: cover; border-radius: 50%;"
                             onerror="this.style.display='none'; this.nextElementSibling.style.display='block';">
                        <i class="fas fa-user" style="display: none;"></i>
                    <% } else { %>
                        <i class="fas fa-user"></i>
                    <% } %>
                </div>
                <h2 class="profile-name"><%= fullName%></h2>
                <p class="profile-id">Student ID: #<%= (student != null) ? student.getStudentId() : "N/A"%></p>

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
        <script src="${pageContext.request.contextPath}/js/viewprofile.js"></script>
    </body>
</html>