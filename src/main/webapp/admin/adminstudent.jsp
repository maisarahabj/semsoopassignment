<%-- 
    Document   : adminstudent VIEW/EDIT STUDENT LIST
    Created on : 22 Dec 2025, 1:45:50 am
    Author     : maisarahabjalil
--%>

<%@page import="com.sems.model.Student"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Barfact Admin | Manage Students</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/adminCSS/admindash.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/adminCSS/adminstudent.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>
    <body>
        <div class="dashboard-wrapper">
            <aside class="sidebar">
                <div class="logo-section"><div class="logo-box"></div><span class="logo-text">Barfact Admin</span></div>
                <nav class="nav-menu">
                    <a href="${pageContext.request.contextPath}/DashboardServlet" class="nav-link"><i class="fas fa-chart-line"></i> Overview</a>
                    <a href="${pageContext.request.contextPath}/CourseServlet?action=manage" class="nav-link"><i class="fas fa-book-open"></i> Manage Courses</a>
                    <a href="${pageContext.request.contextPath}/AdminManageStudentServlet" 
                       class="nav-link <%= request.getRequestURI().contains("adminstudent") ? "active" : ""%>">
                        <i class="fas fa-user-graduate"></i> Manage Students
                    </a>
                    <a href="${pageContext.request.contextPath}/auth/AdminPendingServlet" class="nav-link"><i class="fas fa-clock"></i> Pending Approvals</a>
                </nav>
            </aside>

            <main class="main-content">
                <div class="welcome-banner">
                    <div class="banner-text">
                        <h1>Student <span class="highlight-blue">Directory</span></h1>
                        <p>Manage academic profiles, contact information, and GPA records.</p>
                    </div>
                </div>

                <div class="schedule-container">
                    <div class="table-header-flex">
                        <h3>Enrolled Students</h3>
                        <span class="badge-count"><%= (request.getAttribute("studentList") != null) ? ((List) request.getAttribute("studentList")).size() : 0%> Students</span>
                    </div>
                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Full Name</th>
                                <th>Email</th>
                                <th>Phone</th>
                                <th>GPA</th>
                                <th style="text-align: center;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                List<Student> students = (List<Student>) request.getAttribute("studentList");
                                if (students != null && !students.isEmpty()) {
                                    for (Student s : students) {
                            %>
                            <tr>
                                <td>#<%= s.getStudentId()%></td>
                                <td><strong><%= s.getFullName()%></strong></td>
                                <td><%= s.getEmail()%></td>
                                <td><%= (s.getPhone() != null) ? s.getPhone() : "-"%></td>
                                <td><span class="role-badge"><%= String.format("%.2f", s.getGpa())%></span></td>
                                <td style="text-align: center;">
                                    <button type="button" class="btn-reject" 
                                            onclick="showDeleteModal('<%= s.getStudentId()%>', '<%= s.getFirstName()%>', '<%= s.getLastName()%>')">
                                        <i class="fas fa-user-minus"></i> Remove
                                    </button>
                                </td>
                            </tr>
                            <% }
                            }%>
                        </tbody>
                    </table>
                </div>
            </main>
        </div>

        <div id="deleteOverlay" class="modal-overlay">
            <div class="modal-box">
                <div class="modal-icon"><i class="fas fa-exclamation-triangle"></i></div>
                <h3>Remove Student?</h3>
                <p id="deleteMessage">Confirm deletion.</p>
                <div class="modal-actions">
                    <button onclick="closeDeleteModal()" class="btn-cancel">Cancel</button>
                    <form id="finalDeleteForm" action="${pageContext.request.contextPath}/ManageStudentServlet" method="POST">
                        <input type="hidden" name="action" value="DELETE">
                        <input type="hidden" name="studentId" id="modalStudentId">
                        <button type="submit" class="btn-confirm">Permanently Remove</button>
                    </form>
                </div>
            </div>
        </div>

        <script src="${pageContext.request.contextPath}/js/adminstudent.js"></script>
    </body>
</html>