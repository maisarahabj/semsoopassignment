<%-- 
    Document   : admincourse VIEW EDIT COURSES
    Created on : 22 Dec 2025, 1:45:25 am
    Author     : maisarahabjalil
--%>

<%@page import="com.sems.model.Course"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Barfact Uni | Manage Courses</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/adminCSS/admindash.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/adminCSS/admincourse.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>
    <body>
        <div class="dashboard-wrapper">
            <aside class="sidebar">
                <div class="logo-section"><div class="logo-box"></div><span class="logo-text">Barfact Admin</span></div>
                <nav class="nav-menu">
                    <a href="${pageContext.request.contextPath}/DashboardServlet" class="nav-link"><i class="fas fa-chart-line"></i> Overview</a>
                    <a href="${pageContext.request.contextPath}/CourseServlet?action=manage" class="nav-link active"><i class="fas fa-book-open"></i> Manage Courses</a>
                    <a href="${pageContext.request.contextPath}/admin/adminstudent.jsp" class="nav-link"><i class="fas fa-user-graduate"></i> Manage Students</a>
                    <a href="${pageContext.request.contextPath}/auth/AdminPendingServlet" class="nav-link"><i class="fas fa-clock"></i> Pending Approvals</a>
                </nav>
            </aside>

            <main class="main-content">
                <div class="welcome-banner">
                    <div class="banner-text">
                        <h1>Course <span class="highlight-blue">Management</span></h1>
                        <p>Add new subjects or remove existing ones from the system.</p>
                    </div>
                </div>

                <div class="schedule-container" style="margin-bottom: 20px;">
                    <h3>Add New Course</h3>
                    <form action="${pageContext.request.contextPath}/CourseServlet" method="POST" style="display: flex; gap: 10px; margin-top: 15px; flex-wrap: wrap; align-items: center;">
                        <input type="hidden" name="action" value="ADD">
                        <input type="text" name="courseCode" placeholder="Code" required style="padding: 8px; border-radius: 5px; border: 1px solid #ddd; width: 100px;">
                        <input type="text" name="courseName" placeholder="Course Name" required style="padding: 8px; border-radius: 5px; border: 1px solid #ddd; flex: 1;">
                        <input type="number" name="credits" placeholder="Credits" min="1" max="5" required style="padding: 8px; border-radius: 5px; border: 1px solid #ddd; width: 80px;">
                        <input type="number" name="capacity" placeholder="Cap" min="1" required style="padding: 8px; border-radius: 5px; border: 1px solid #ddd; width: 80px;">
                        <select name="courseDay" required style="padding: 8px; border-radius: 5px; border: 1px solid #ddd;">
                            <option value="Monday">Monday</option>
                            <option value="Tuesday">Tuesday</option>
                            <option value="Wednesday">Wednesday</option>
                            <option value="Thursday">Thursday</option>
                            <option value="Friday">Friday</option>
                        </select>
                        <input type="time" name="courseTime" required style="padding: 8px; border-radius: 5px; border: 1px solid #ddd;">
                        <button type="submit" class="btn-approve" style="background: #007bff; color: white; padding: 8px 15px;">Add Course</button>
                    </form>
                </div>

                <div class="schedule-container" >
                    <h3>Current Catalog</h3>
                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th>Code</th> 
                                <th>Name</th> 
                                <th>Credits</th> <th>Cap</th>     <th>Day</th> 
                                <th>Time</th>
                                <th style="text-align: center;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                List<Course> courses = (List<Course>) request.getAttribute("allCourses");
                                if (courses != null) {
                                    for (Course c : courses) {
                            %>
                            <tr>
                                <td><strong><%= c.getCourseCode()%></strong></td>
                                <td><%= c.getCourseName()%></td>
                                <td><%= c.getCredits()%></td> <td><%= c.getEnrolledCount()%>/<%= c.getCapacity()%></td> <td><%= c.getCourseDay()%></td>
                                <td><%= c.getCourseTime().substring(0, 5)%></td>
                                <td style="text-align: center;">
                                    <button type="button" class="btn-reject" 
                                            onclick="showDeleteModal('<%= c.getCourseId()%>', '<%= c.getCourseCode()%>', '<%= c.getCourseName()%>')">
                                        <i class="fas fa-trash"></i> Delete
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
                <h3>Confirm Deletion</h3>
                <p id="deleteMessage">Are you sure?</p>
                <div class="modal-actions">
                    <button onclick="closeDeleteModal()" class="btn-cancel">No, Keep it</button>
                    <form id="finalDeleteForm" action="${pageContext.request.contextPath}/CourseServlet" method="POST">
                        <input type="hidden" name="action" value="DELETE">
                        <input type="hidden" name="courseId" id="modalCourseId">
                        <button type="submit" class="btn-confirm">Yes, Delete Permanently</button>
                    </form>
                </div>
            </div>
        </div>

        <script src="${pageContext.request.contextPath}/js/admincourse.js"></script>                
    </body>
</html>