<%-- 
    Document   : admin_profile ADMIN VIEW
    Created on : 2026
    Author     : maisarahabjalil
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sems.model.Student" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Barfact University | Admin Profile</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/adminCSS/admindash.css?v=2.1">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/studentCSS/viewprofile.css?v=2.1">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>
    <style>



        /* Reuse the photo styles from student profile */
        .profile-avatar {
            width: 120px;
            height: 120px;
            margin: 0 auto;
            border-radius: 50%;
            background-color: #f1f5f9;
            display: flex;
            align-items: center;
            justify-content: center;
            overflow: hidden;
        }
        .profile-avatar img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        .profile-avatar i {
            font-size: 3.5rem;
            color: #cbd5e1;
        }
        .upload-btn-container {
            text-align: center;
            margin-top: 15px;
            margin-bottom: 30px;
            display: flex;
            justify-content: center;
            gap: 10px;
        }
        .btn-action-photo {
            cursor: pointer;
            font-size: 0.9rem;
            width: 40px;
            height: 40px;
            border-radius: 50%;
            border: 1px solid #e2e8f0;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: all 0.2s ease;
        }
        .btn-upload {
            background-color: #f0f7ff;
            color: #007bff;
            border-color: #007bff;
        }
        #file-upload {
            display: none;
        }
        .form-inline {
            display: inline-block;
            margin: 0;
        }
    </style>
    <body>
        <%
            // 1. Security Check
            if (session.getAttribute("userId") == null || !"admin".equals(session.getAttribute("role"))) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            Student adminObj = (Student) request.getAttribute("student");
            String adminName = (adminObj != null) ? adminObj.getFirstName() + " " + adminObj.getLastName() : "Administrator";

            // 2. Photo Logic using Universal userId
            com.sems.dao.StudentDAO viewDao = new com.sems.dao.StudentDAO();
            Integer uId = (Integer) session.getAttribute("userId");
            boolean hasPhoto = (uId != null) && viewDao.hasProfilePhotoByUserId(uId);
        %>

        <div class="dashboard-wrapper">
            <aside class="sidebar">
                <div class="logo-section">
                    <img src="${pageContext.request.contextPath}/assets/cat.png" class="logo-img" style="width: 50px; height: 50px;">
                    <span class="logo-text">Barfact Admin</span>
                </div>
                <nav class="nav-menu">
                    <a href="${pageContext.request.contextPath}/DashboardServlet" class="nav-link">
                        <i class="fas fa-chart-line"></i> Overview
                    </a>
                    <a href="${pageContext.request.contextPath}/CourseServlet" class="nav-link">
                        <i class="fas fa-book-open"></i> Manage Courses
                    </a>
                    <a href="${pageContext.request.contextPath}/AdminManageStudentServlet" class="nav-link">
                        <i class="fas fa-user-graduate"></i> Manage Students
                    </a>
                    <a href="${pageContext.request.contextPath}/GradeServlet" class="nav-link">
                        <i class="fas fa-graduation-cap"></i> Grade Management
                    </a>
                    <a href="${pageContext.request.contextPath}/auth/AdminPendingServlet" class="nav-link">
                        <i class="fas fa-clock"></i> Pending Approvals
                    </a>
                    <a href="${pageContext.request.contextPath}/ActivityServlet" class="nav-link">
                        <i class="fas fa-history"></i> System Logs
                    </a>
                    <a href="${pageContext.request.contextPath}/AdminReportServlet" class="nav-link">
                        <i class="fas fa-file-alt"></i> Academic Report
                    </a>
                    <a href="${pageContext.request.contextPath}/ProfileServlet" class="nav-link active">
                        <i class="fas fa-user-shield"></i> My Account
                    </a>
                </nav>
            </aside>

            <main class="main-content">
                <div class="welcome-banner">
                    <div class="banner-text">
                        <h1>Admin <span class="highlight-blue">Settings</span></h1>
                        <p>Update your administrative contact details and profile picture.</p>
                    </div>
                </div>

                <% if ("success".equals(request.getParameter("status"))) { %>
                <div class="alert success-alert" id="successAlert"><i class="fas fa-check-circle"></i> Profile updated!</div>
                <% } else if ("img_success".equals(request.getParameter("status"))) { %>
                <div class="alert success-alert"><i class="fas fa-camera"></i> Photo updated!</div>
                <% } else if ("img_removed".equals(request.getParameter("status"))) { %>
                <div class="alert success-alert" style="color: #c2410c; background-color: #fff7ed; border-color: #fdba74;">
                    <i class="fas fa-trash-alt"></i> Photo removed.
                </div>
                <% } %>

                <div class="profile-card-container">
                    <div class="profile-avatar profile-avatar-main">
                        <% if (hasPhoto) {%>
                        <img src="${pageContext.request.contextPath}/ImageServlet?userId=<%= uId%>" alt="Admin Photo">
                        <% } else { %>
                        <i class="fas fa-user-shield"></i>
                        <% } %>
                    </div>

                    <div class="upload-btn-container">
                        <form action="${pageContext.request.contextPath}/UploadPhotoServlet" method="post" enctype="multipart/form-data" class="form-inline">
                            <label for="file-upload" class="btn-action-photo btn-upload" title="Upload Photo"><i class="fas fa-camera"></i></label>
                            <input id="file-upload" type="file" name="photo" accept="image/*" onchange="this.form.submit()">
                        </form>
                        <% if (hasPhoto) { %>
                        <form action="${pageContext.request.contextPath}/UploadPhotoServlet" method="post" class="form-inline">
                            <input type="hidden" name="action" value="delete">
                            <button type="submit" class="btn-action-photo btn-remove" title="Remove Photo" onclick="return confirm('Remove photo?');">
                                <i class="fas fa-trash-alt"></i>
                            </button>
                        </form>
                        <% }%>
                    </div>

                    <form action="${pageContext.request.contextPath}/ProfileServlet" method="POST" class="profile-form">
                        <div class="form-grid">
                            <div class="form-group">
                                <label>Username</label>
                                <div class="editable-input-group">
                                    <p class="display-value"><%= session.getAttribute("username")%></p>
                                    <input type="text" name="username" value="<%= session.getAttribute("username")%>" class="edit-field hidden" required>
                                    <button type="button" class="inline-edit-btn" onclick="toggleFieldEdit(this)"><i class="fas fa-edit"></i></button>
                                </div>
                            </div>

                            <div class="form-group">
                                <label>Password</label>
                                <div class="editable-input-group">
                                    <p class="display-value">••••••••</p>
                                    <input type="password" name="password" id="passwordInput" 
                                           placeholder="New password (8+ chars, 1 Upper, 1 Digit)" 
                                           class="edit-field hidden"
                                           pattern="(?=.*\d)(?=.*[A-Z]).{8,}"
                                           title="Must contain at least 8 characters, one number, and one uppercase letter">
                                    <button type="button" class="inline-edit-btn" onclick="toggleFieldEdit(this)">
                                        <i class="fas fa-key"></i>
                                    </button>
                                </div>
                            </div>

                            <div class="form-group">
                                <label>First Name</label>
                                <div class="editable-input-group">
                                    <p class="display-value"><%= adminObj.getFirstName()%></p>
                                    <input type="text" name="firstName" value="<%= adminObj.getFirstName()%>" class="edit-field hidden" required>
                                    <button type="button" class="inline-edit-btn" onclick="toggleFieldEdit(this)"><i class="fas fa-edit"></i></button>
                                </div>
                            </div>

                            <div class="form-group">
                                <label>Last Name</label>
                                <div class="editable-input-group">
                                    <p class="display-value"><%= adminObj.getLastName()%></p>
                                    <input type="text" name="lastName" value="<%= adminObj.getLastName()%>" class="edit-field hidden" required>
                                    <button type="button" class="inline-edit-btn" onclick="toggleFieldEdit(this)"><i class="fas fa-edit"></i></button>
                                </div>
                            </div>

                            <div class="form-group">
                                <label>Official Email</label>
                                <div class="editable-input-group">
                                    <p class="display-value"><%= adminObj.getEmail()%></p>
                                    <input type="email" name="email" value="<%= adminObj.getEmail()%>" class="edit-field hidden" required>
                                    <button type="button" class="inline-edit-btn" onclick="toggleFieldEdit(this)"><i class="fas fa-edit"></i></button>
                                </div>
                            </div>

                            <div class="form-group">
                                <label>Contact Number</label>
                                <div class="editable-input-group">
                                    <p class="display-value"><%= (adminObj.getPhone() != null && !adminObj.getPhone().isEmpty()) ? adminObj.getPhone() : "Not set"%></p>
                                    <input type="text" name="phone" value="<%= (adminObj.getPhone() != null) ? adminObj.getPhone() : ""%>" class="edit-field hidden">
                                    <button type="button" class="inline-edit-btn" onclick="toggleFieldEdit(this)"><i class="fas fa-edit"></i></button>
                                </div>
                            </div>

                            <div class="form-group full-width">
                                <label>Home Address</label>
                                <div class="editable-input-group">
                                    <p class="display-value"><%= (adminObj.getAddress() != null && !adminObj.getAddress().isEmpty()) ? adminObj.getAddress() : "Not set"%></p>
                                    <textarea name="address" rows="2" class="edit-field hidden"><%= (adminObj.getAddress() != null) ? adminObj.getAddress() : ""%></textarea>
                                    <button type="button" class="inline-edit-btn" onclick="toggleFieldEdit(this)"><i class="fas fa-edit"></i></button>
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
                <div class="profile-avatar profile-avatar-side">
                    <% if (hasPhoto) {%>
                    <img src="${pageContext.request.contextPath}/ImageServlet?userId=<%= uId%>" alt="Admin Photo">
                    <% } else { %>
                    <i class="fas fa-user-tie" style="color: #007bff;"></i>
                    <% }%>
                </div>
                <h2 class="profile-name"><%= adminName%></h2>
                <p class="profile-id" style="margin-top: 4px;">Role: Admin</p>
                <div class="term-info-card" style="border-left-color: #007bff;">
                    <h4><i class="fas fa-shield-alt"></i> Security</h4>
                    <p>Status: Verified</p>
                    <p>Session: Active</p>
                </div>
                <a href="${pageContext.request.contextPath}/auth/LogoutServlet" class="btn-logout">
                    <i class="fas fa-sign-out-alt"></i> Log Out
                </a>
            </aside>
        </div>
        <script src="${pageContext.request.contextPath}/js/viewprofile.js"></script>
    </body>
</html>
</html>