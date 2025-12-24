<%-- 
    Document   : addcourse
    Created on : 24 Dec 2025
    Author     : maisarahabjalil
--%>
<%@page import="java.util.Set"%>
<%@page import="java.util.List"%>
<%@page import="com.sems.model.Course"%>
<%@page import="com.sems.model.Student"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Barfact University | Add Subjects</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/studentCSS/addcourse.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>
    <body>
        <%
            if (session.getAttribute("userId") == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            Student student = (Student) request.getAttribute("student");
            List<Course> allCourses = (List<Course>) request.getAttribute("allCourses");
            // We use a Set of IDs for quick lookup to check if enrolled
            Set<Integer> enrolledIds = (Set<Integer>) request.getAttribute("enrolledIds");
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
                    <a href="${pageContext.request.contextPath}/student/MyCourseServlet" class="nav-link">
                        <i class="fas fa-book"></i> My Classes
                    </a>
                    <a href="${pageContext.request.contextPath}/student/AddCourseServlet" class="nav-link active">
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
                        <h1>Available <span class="highlight-blue">Subjects</span></h1>
                        <p>Search and register for new courses to add to your semester schedule.</p>
                    </div>
                    <div class="banner-icon"><i class="fas fa-plus-circle"></i></div>
                </div>

                <div class="schedule-container">
                    <div class="table-header-flex">
                        <div class="search-box-container">
                            <i class="fas fa-search"></i>
                            <input type="text" id="courseSearch" placeholder="Search by name or code..." onkeyup="filterCourses()">
                        </div>

                        <div class="header-actions">
                            <button type="button" id="toggleAddBtn" class="btn-enroll-mode" onclick="toggleAddMode()">
                                <i class="fas fa-plus-circle"></i> Register                             </button>
                            <span class="badge-count"><%= (allCourses != null) ? allCourses.size() : 0%> Courses</span>
                        </div>
                    </div>

                    <table class="admin-table" id="courseTable">
                        <thead>
                            <tr>
                                <th class="add-column"></th>
                                <th>Code</th>
                                <th>Course Name</th>
                                <th>Credits</th>
                                <th>Seats</th>
                                <th>Schedule</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (allCourses != null && !allCourses.isEmpty()) {
                                    for (Course c : allCourses) {
                                        boolean isEnrolled = (enrolledIds != null && enrolledIds.contains(c.getCourseId()));
                            %>
                            <tr class="<%= isEnrolled ? "row-enrolled" : ""%>">
                                <td class="add-column">
                                    <% if (!isEnrolled) {%>
                                    <button type="button" class="btn-action-add" 
                                            onclick="confirmEnroll('<%= c.getCourseId()%>', '<%= c.getCourseCode()%>')">
                                        <i class="fas fa-plus-circle"></i>
                                    </button>
                                    <% } else { %>
                                    <i class="fas fa-check-double enrolled-icon"></i>
                                    <% }%>
                                </td>
                                <td class="course-code-tag"><strong><%= c.getCourseCode()%></strong></td>
                                <td><%= c.getCourseName()%></td>
                                <td><%= c.getCredits()%></td>
                                <td><%= c.getEnrolledCount()%> / <%= c.getCapacity()%></td>
                                <td><i class="fas fa-clock schedule-icon"></i><%= c.getCourseDay()%> (<%= c.getCourseTime().substring(0, 5)%>)</td>
                            </tr>
                            <%
                                    }
                                }
                            %>
                        </tbody>
                    </table>
                </div>
            </main>

            <aside class="right-panel">
                <div class="profile-avatar"><i class="fas fa-user"></i></div>
                <h2 class="profile-name"><%= fullName%></h2>
                <p class="profile-id">ID: #<%= (student != null) ? student.getStudentId() : "N/A"%></p>
                <div class="term-info-card">
                    <h4><i class="fas fa-calendar-alt"></i> Info</h4>
                    <p>Status: Open Enrollment</p>
                </div>
                <a href="${pageContext.request.contextPath}/auth/LogoutServlet" class="btn-logout">
                    <i class="fas fa-sign-out-alt"></i> Log Out
                </a>
            </aside>
        </div>

        <script src="${pageContext.request.contextPath}/js/addcourse.js"></script>
    </body>
</html>