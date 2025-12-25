<%-- 
    Document   : mycourse
    Created on : 22 Dec 2025
    Author     : maisarahabjalil

    talks to enrollment servlet&DAO
--%>
<%@page import="java.util.List"%>
<%@page import="com.sems.model.Course"%>
<%@page import="com.sems.model.Student"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Barfact University | My Classes</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/studentCSS/mycourse.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>
    <body>
        <%
            if (session.getAttribute("userId") == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            Student student = (Student) request.getAttribute("student");
            List<Course> enrolledCourses = (List<Course>) request.getAttribute("enrolledCourses");
            String fullName = (student != null) ? student.getFirstName() + " " + student.getLastName() : "Student";
        %>

        <div class="dashboard-wrapper">
            <aside class="sidebar">
                <div class="logo-section">
                    <img src="${pageContext.request.contextPath}/assets/cat.png" class="logo-img"style="width: 50px; height: 50px; ">
                    <span class="logo-text">Barfact Uni</span>
                </div>
                <nav class="nav-menu">
                    <a href="${pageContext.request.contextPath}/DashboardServlet" class="nav-link">
                        <i class="fas fa-home"></i> Dashboard
                    </a>
                    <a href="${pageContext.request.contextPath}/student/MyCourseServlet" class="nav-link active">
                        <i class="fas fa-book"></i> My Classes
                    </a>
                    <a href="${pageContext.request.contextPath}/student/AddCourseServlet" class="nav-link">
                        <i class="fas fa-plus-square"></i> Add Subjects
                    </a>
                    <a href="${pageContext.request.contextPath}/ProfileServlet" class="nav-link">
                        <i class="fas fa-user"></i> Profile
                    </a>
                </nav>
                <div class="cgpa-container">
                    <div class="cgpa-value"><%= (student != null) ? student.getGpa() : "0.00"%></div>
                    <p class="cgpa-label">Current CGPA</p>
                </div>
            </aside>

            <main class="main-content">
                <div class="welcome-banner">
                    <div class="banner-text">
                        <h1>My Enrolled <span class="highlight-blue">Classes</span></h1>
                        <p>Review your current subject list and academic schedule details.</p>
                    </div>
                    <div class="banner-icon"><i class="fas fa-book-open"></i></div>
                </div>

                <div class="schedule-container">
                    <div class="table-header-flex">
                        <h3>Course Enrollment List</h3>
                        <div class="header-actions">
                            <a href="${pageContext.request.contextPath}/student/AddCourseServlet" class="btn-add-mode">
                                <i class="fas fa-plus-circle"></i> Add Course
                            </a>
                            <button type="button" id="toggleDropBtn" class="btn-drop-mode" onclick="toggleDropMode()">
                                <i class="fas fa-minus-circle"></i> Drop Course
                            </button>
                            <span class="badge-count"><%= (enrolledCourses != null) ? enrolledCourses.size() : 0%> Subjects</span>
                        </div>
                    </div>

                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th class="drop-column"></th>
                                <th>Code</th>
                                <th>Course Name</th>
                                <th>Credits</th>
                                <th>Schedule</th>
                                <th>Time</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (enrolledCourses != null && !enrolledCourses.isEmpty()) {
                                    for (Course c : enrolledCourses) {
                            %>
                            <tr id="row-<%= c.getCourseId()%>">
                                <td class="drop-column">
                                    <button type="button" class="btn-action-delete" 
                                            onclick="confirmDrop('<%= c.getCourseId()%>', '<%= c.getCourseCode()%>'); event.stopPropagation();">
                                        <i class="fas fa-times-circle"></i>
                                    </button>
                                </td>

                                <td class="course-code-tag" onclick="showCourseModal('<%= c.getCourseCode()%>', '<%= c.getCourseName().replace("'", "\\'")%>', '<%= c.getCredits()%>', '<%= c.getCourseDay()%>', '<%= c.getCourseTime().substring(0, 5)%>', '<%= (c.getDepartment() != null) ? c.getDepartment() : "General"%>', '<%= c.getEnrolledCount()%>', '<%= c.getCapacity()%>')" style="cursor: pointer;">
                                    <strong><%= c.getCourseCode()%></strong>
                                </td>
                                <td onclick="showCourseModal('<%= c.getCourseCode()%>', '<%= c.getCourseName().replace("'", "\\'")%>', '<%= c.getCredits()%>', '<%= c.getCourseDay()%>', '<%= c.getCourseTime().substring(0, 5)%>', '<%= (c.getDepartment() != null) ? c.getDepartment() : "General"%>', '<%= c.getEnrolledCount()%>', '<%= c.getCapacity()%>')" style="cursor: pointer;">
                                    <%= c.getCourseName()%>
                                </td>
                                <td><%= c.getCredits()%></td>
                                <td><i class="fas fa-calendar-day schedule-icon"></i><%= c.getCourseDay()%></td>
                                <td><i class="fas fa-clock schedule-icon"></i><%= c.getCourseTime().substring(0, 5)%></td>
                            </tr>
                            <%
                                }
                            } else {
                            %>
                            <tr>
                                <td colspan="6" class="empty-state">
                                    <i class="fas fa-info-circle"></i>
                                    No courses found. Go to 'Add Subjects' to enroll.
                                </td>
                            </tr>
                            <% }%>
                        </tbody>
                    </table>
                </div>
            </main>

            <aside class="right-panel">
                <div class="profile-avatar"><i class="fas fa-user"></i></div>
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

        <div id="courseDetailOverlay" class="modal-overlay">
            <div class="modal-box">
                <div class="modal-icon"><i class="fas fa-book-open"></i></div>
                <h3 id="modalCourseName">Course Details</h3>
                <p id="modalCourseCode">CODE</p>

                <div class="modal-form-grid">
                    <div class="input-group">
                        <label>Department</label>
                        <p id="modalDepartment" class="view-data">-</p>
                    </div>
                    <div class="input-group">
                        <label>Credits</label>
                        <p id="modalCredits" class="view-data">-</p>
                    </div>
                    <div class="input-group">
                        <label>Day</label>
                        <p id="modalDay" class="view-data">-</p>
                    </div>
                    <div class="input-group">
                        <label>Time</label>
                        <p id="modalTime" class="view-data">-</p>
                    </div>
                    <div class="input-group full-width">
                        <label>Enrollment Status</label>
                        <p id="modalCapacity" class="view-data">-</p>
                    </div>
                </div>

                <div class="modal-actions">
                    <button type="button" onclick="closeCourseModal()" class="btn-cancel">Close</button>
                </div>
            </div>
        </div>

        <script src="${pageContext.request.contextPath}/js/mycourse.js"></script>
    </body>
</html>