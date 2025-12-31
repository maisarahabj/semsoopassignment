<%-- 
    Admin Profile Page
    Author: maisarahabjalil
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.sems.model.Student, com.sems.model.User" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Admin Profile - SEMS</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/adminCSS/admindash.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/studentCSS/viewprofile.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>
    <body>
        <%
            if (session.getAttribute("userId") == null || !"admin".equals(session.getAttribute("role"))) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            User user = (User) request.getAttribute("user");
            Student adminProfile = (Student) request.getAttribute("adminProfile");
            String fullName = (adminProfile != null) ? adminProfile.getFirstName() + " " + adminProfile.getLastName() : "Admin";
        %>

        <%@ include file="../assets/header.html" %>

        <div class="container" style="max-width: 1200px; margin: 40px auto; padding: 20px;">
            <h1 style="margin-bottom: 30px;">Admin <span style="color: #3b82f6;">Profile</span></h1>

            <% if ("success".equals(request.getParameter("status"))) { %>
            <div class="alert success-alert" id="successAlert" style="background: #d1fae5; color: #065f46; padding: 15px; border-radius: 8px; margin-bottom: 20px;">
                <i class="fas fa-check-circle"></i> Profile updated successfully!
            </div>
            <% } %>

            <div class="profile-card-container">
                <form action="${pageContext.request.contextPath}/AdminProfileServlet" method="POST" 
                      class="profile-form" enctype="multipart/form-data">
                    
                    <!-- Profile Picture Section -->
                    <div class="form-group full-width">
                        <label>Profile Picture</label>
                        <div class="profile-picture-upload">
                            <% 
                                String profilePicture = (adminProfile != null) ? adminProfile.getProfilePicture() : null;
                                String displayPicture = (profilePicture != null && !profilePicture.isEmpty()) 
                                    ? request.getContextPath() + "/" + profilePicture 
                                    : request.getContextPath() + "/assets/cat.png";
                            %>
                            <img id="profilePreview" src="<%= displayPicture %>" 
                                 alt="Profile Picture" class="profile-picture-preview">
                            <input type="file" name="profilePicture" id="profilePicture" 
                                   accept="image/*" onchange="previewProfilePicture(this)">
                            <label for="profilePicture" class="upload-btn">
                                <i class="fas fa-camera"></i> Change Photo
                            </label>
                        </div>
                    </div>

                    <div class="form-grid">
                        <div class="form-group">
                            <label>Username</label>
                            <p class="view-text"><%= user != null ? user.getUsername() : "N/A" %></p>
                        </div>

                        <div class="form-group">
                            <label>Role</label>
                            <p class="view-text">Administrator</p>
                        </div>

                        <% if (adminProfile != null) { %>
                        <div class="form-group">
                            <label>First Name</label>
                            <p class="view-text"><%= adminProfile.getFirstName() %></p>
                        </div>

                        <div class="form-group">
                            <label>Last Name</label>
                            <p class="view-text"><%= adminProfile.getLastName() %></p>
                        </div>

                        <div class="form-group">
                            <label>Email Address</label>
                            <div class="editable-input-group">
                                <p class="display-value"><%= adminProfile.getEmail() %></p>
                                <input type="email" name="email" value="<%= adminProfile.getEmail() %>" 
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
                                    <%= (adminProfile.getPhone() != null && !adminProfile.getPhone().isEmpty()) ? adminProfile.getPhone() : "Not set" %>
                                </p>
                                <input type="text" name="phone" value="<%= (adminProfile.getPhone() != null) ? adminProfile.getPhone() : "" %>" 
                                       class="edit-field hidden">
                                <button type="button" class="inline-edit-btn" onclick="toggleFieldEdit(this)">
                                    <i class="fas fa-edit"></i>
                                </button>
                            </div>
                        </div>

                        <div class="form-group full-width">
                            <label>Address</label>
                            <div class="editable-input-group">
                                <p class="display-value">
                                    <%= (adminProfile.getAddress() != null && !adminProfile.getAddress().isEmpty()) ? adminProfile.getAddress() : "Not set" %>
                                </p>
                                <textarea name="address" rows="2" class="edit-field hidden"><%= (adminProfile.getAddress() != null) ? adminProfile.getAddress() : "" %></textarea>
                                <button type="button" class="inline-edit-btn" onclick="toggleFieldEdit(this)">
                                    <i class="fas fa-edit"></i>
                                </button>
                            </div>
                        </div>
                        <% } %>
                    </div>

                    <div id="formActions" class="form-actions hidden">
                        <hr>
                        <button type="submit" class="btn-save-profile" style="background: #3b82f6; color: white; padding: 12px 30px; border: none; border-radius: 8px; cursor: pointer; font-size: 16px;">
                            Save Changes
                        </button>
                        <button type="button" class="btn-cancel" onclick="window.location.reload()" style="background: #6b7280; color: white; padding: 12px 30px; border: none; border-radius: 8px; cursor: pointer; margin-left: 10px; font-size: 16px;">
                            Cancel
                        </button>
                    </div>
                </form>
            </div>

            <div style="margin-top: 30px;">
                <a href="${pageContext.request.contextPath}/admin/admindash.jsp" 
                   style="color: #3b82f6; text-decoration: none; font-weight: 600;">
                    <i class="fas fa-arrow-left"></i> Back to Dashboard
                </a>
            </div>
        </div>

        <%@ include file="../assets/footer.html" %>

        <script src="${pageContext.request.contextPath}/js/viewprofile.js"></script>
        <script>
            function previewProfilePicture(input) {
                if (input.files && input.files[0]) {
                    const reader = new FileReader();
                    reader.onload = function(e) {
                        document.getElementById('profilePreview').src = e.target.result;
                    };
                    reader.readAsDataURL(input.files[0]);
                    
                    const formActions = document.getElementById('formActions');
                    if (formActions) {
                        formActions.classList.remove('hidden');
                    }
                }
            }

            function toggleFieldEdit(button) {
                const container = button.closest('.editable-input-group');
                const displayValue = container.querySelector('.display-value');
                const inputField = container.querySelector('.edit-field');
                const actions = document.getElementById('formActions');

                displayValue.classList.toggle('hidden');
                inputField.classList.toggle('hidden');
                
                const icon = button.querySelector('i');
                if (inputField.classList.contains('hidden')) {
                    icon.classList.replace('fa-times', 'fa-edit');
                } else {
                    icon.classList.replace('fa-edit', 'fa-times');
                    inputField.focus();
                }

                const isAnyEditing = document.querySelectorAll('.edit-field:not(.hidden)').length > 0;
                actions.classList.toggle('hidden', !isAnyEditing);
            }

            // Auto-hide success alert
            document.addEventListener('DOMContentLoaded', function() {
                const alert = document.getElementById('successAlert');
                if(alert) {
                    setTimeout(() => {
                        alert.style.transition = 'opacity 0.5s ease';
                        alert.style.opacity = '0';
                        setTimeout(() => alert.style.display = 'none', 500);
                    }, 3000);
                }
            });
        </script>
    </body>
</html>
