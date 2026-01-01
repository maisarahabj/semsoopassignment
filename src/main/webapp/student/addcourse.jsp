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
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/studentCSS/addcourse.css?v=2.0">
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
                    <a href="${pageContext.request.contextPath}/GradeServlet" class="nav-link">
                        <i class="fas fa-poll-h"></i> My Results
                    </a>
                    <a href="${pageContext.request.contextPath}/EvaluationServlet" class="nav-link"><i class="fas fa-star"></i> Course Evaluation</a>
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

                <!--missing pre=-req message-->
                <% if ("missing_prereq".equals(request.getParameter("error"))) { %>
                <div class="alert-banner error">
                    <i class="fas fa-exclamation-triangle"></i>
                    <span><strong>Enrolment Blocked:</strong> You have not met the prerequisite requirements for this course (Minimum Grade C required).</span>
                    <button type="button" class="close-alert" onclick="this.parentElement.style.display = 'none';">&times;</button>
                </div>
                <% }%>


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
                                <td class="course-code-tag"><strong><%= c.getCourseCode()%></td>

                                <td>
                                    <div class="course-name-wrapper">
                                        <span class="course-title"><%= c.getCourseName()%></span>
                                        <% if (c.isHasPrereq()) {%>
                                        <div class="badge-container">
                                            <span class="badge-prereq">
                                                <i class="fas fa-lock"></i> Prerequisite
                                            </span>
                                            <div class="prereq-tooltip">
                                                Requires: <strong><%= c.getPrerequisiteName()%></strong>
                                            </div>
                                        </div>
                                        <% }%>
                                    </div>
                                </td>

                                <td><%= c.getCredits()%></td>
                                <td><%= c.getEnrolledCount()%> / <%= c.getCapacity()%></td>
                                <td>
                                    <i class="fas fa-clock schedule-icon"></i>
                                    <%= c.getCourseDay()%> 
                                    (<%= (c.getCourseTime() != null && c.getCourseTime().length() >= 5) ? c.getCourseTime().substring(0, 5) : "TBA"%>)
                                </td>
                            </tr>
                            <%
                                }
                            } else {
                            %>
                            <tr><td colspan="6" style="text-align:center;">No courses found.</td></tr>
                            <% }%>
                        </tbody>
                    </table>
                </div>
            </main>

            <aside class="right-panel">

                <%
                    // --- JAVA LOGIC FOR IMAGE CHECKING ---
                    com.sems.dao.StudentDAO sidebarDao = new com.sems.dao.StudentDAO();
                    Integer sidebarStudentId = (Integer) session.getAttribute("studentId");
                    boolean showSidebarPhoto = (sidebarStudentId != null) && sidebarDao.hasProfilePhoto(sidebarStudentId);
                %>

                <div class="profile-avatar profile-avatar-side">
                    <% if (showSidebarPhoto) { %>
                    <img src="${pageContext.request.contextPath}/ImageServlet?userId=${sessionScope.userId}" alt="Profile Photo">
                    <% } else { %>
                    <i class="fas fa-user"></i>
                    <% }%>
                </div>

                <h2 class="profile-name"><%= (student != null) ? student.getFirstName() + " " + student.getLastName() : "Student"%></h2>
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

        <!--overlay pop up for confirming enrollment-->
        <div id="enrollModal" class="custom-modal">
            <div class="modal-content">
                <div class="modal-icon"><i class="fas fa-question-circle"></i></div>
                <h2>Course Registration</h2>
                <p id="enrollModalText">Are you sure you want to enroll in this course?</p>
                <div class="modal-actions">
                    <button class="btn-cancel" onclick="closeEnrollModal()">Cancel</button>
                    <button id="confirmEnrollBtn" class="btn-confirm">Confirm Enrollment</button>
                </div>
            </div>
        </div>

        <script src="${pageContext.request.contextPath}/js/addcourse.js"></script>

    </body>
</html>