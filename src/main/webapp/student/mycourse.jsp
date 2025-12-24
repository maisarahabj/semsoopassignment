<%-- 
    Document   : mycourse
    Created on : 22 Dec 2025, 1:48:25 am
    Author     : maisarahabjalil
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
                    <div class="logo-box"></div>
                    <span class="logo-text">Barfact Uni</span>
                </div>
                <nav class="nav-menu">
                    <a href="${pageContext.request.contextPath}/DashboardServlet" class="nav-link">
                        <i class="fas fa-home"></i> Dashboard
                    </a>
                    <a href="${pageContext.request.contextPath}/student/MyCourseServlet" class="nav-link active">
                        <i class="fas fa-book"></i> My Classes
                    </a>
                    <a href="${pageContext.request.contextPath}/student/addcourse.jsp" class="nav-link">
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
                        <span class="badge-count"><%= (enrolledCourses != null) ? enrolledCourses.size() : 0%> Subjects</span>
                    </div>

                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th>Code</th>
                                <th>Course Name</th>
                                <th>Credits</th>
                                <th>Schedule</th>
                                <th>Time</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (enrolledCourses != null && !enrolledCourses.isEmpty()) {
                                for (Course c : enrolledCourses) {%>
                            <tr>
                                <td class="course-code-tag"><%= c.getCourseCode()%></td>
                                <td><%= c.getCourseName()%></td>
                                <td><%= c.getCredits()%></td>
                                <td><i class="fas fa-calendar-day schedule-icon"></i><%= c.getCourseDay()%></td>
                                <td><i class="fas fa-clock schedule-icon"></i><%= c.getCourseTime().substring(0, 5)%></td>
                            </tr>
                            <% }
                        } else { %>
                            <tr>
                                <td colspan="5" class="empty-state">
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
    </body>
</html>