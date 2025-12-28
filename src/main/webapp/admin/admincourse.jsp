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

    <style>
        .add-course-form {
            display: flex;
            gap: 8px;
            margin-top: 15px;
            flex-wrap: wrap;
            align-items: center;
            background: #f8fafc;
            padding: 15px;
            border-radius: 12px;
        }

        .add-course-form input, .add-course-form select {
            padding: 10px;
            border-radius: 8px;
            border: 1px solid #cbd5e1;
            font-size: 0.85rem;
            outline: none;
            transition: border-color 0.2s;
        }

        .add-course-form input:focus, .add-course-form select:focus {
            border-color: #3b82f6;
        }

        /* Fixed Widths for specific small data */
        .input-small {
            width: 80px;
        }
        .input-tiny {
            width: 60px;
        }
        .input-day {
            width: 110px;
        }
        .input-time {
            width: 120px;
        }

        /* Flexible Widths for Name and Prerequisite */
        .input-flex-large {
            flex: 2;
            min-width: 150px;
        }
        .input-flex-medium {
            flex: 1.5;
            min-width: 140px;
            background-color: #ffffff;
            color: #475569;
            font-style: italic; /* Visual cue that it's different/optional */
        }

        .btn-add-submit {
            background: #3b82f6;
            color: white;
            padding: 10px 20px;
            border-radius: 10px;
            border: none;
            font-weight: 600;
            cursor: pointer;
            transition: background 0.2s;
        }

        .btn-add-submit:hover {
            background: #2563eb;
        }
    </style>

    <body>

        <%
            // This connects the data from the Servlet to your JSP variables
            List<Course> courses = (List<Course>) request.getAttribute("allCourses");
        %>

        <div class="dashboard-wrapper">
            <aside class="sidebar">
                <div class="logo-section"><img src="${pageContext.request.contextPath}/assets/cat.png" class="logo-img"style="width: 50px; height: 50px; "><span class="logo-text">Barfact Admin</span></div>
                <nav class="nav-menu">
                    <a href="${pageContext.request.contextPath}/DashboardServlet" class="nav-link"><i class="fas fa-chart-line"></i> Overview</a>
                    <a href="${pageContext.request.contextPath}/CourseServlet?action=manage" class="nav-link active"><i class="fas fa-book-open"></i> Manage Courses</a>
                    <a href="${pageContext.request.contextPath}/AdminManageStudentServlet" class="nav-link"><i class="fas fa-user-graduate"></i> Manage Students</a>
                    <a href="${pageContext.request.contextPath}/GradeServlet" class="nav-link">
                        <i class="fas fa-graduation-cap"></i> Grade Management
                    </a>
                    <a href="${pageContext.request.contextPath}/auth/AdminPendingServlet" class="nav-link"><i class="fas fa-clock"></i> Pending Approvals</a>
                    <a href="${pageContext.request.contextPath}/ActivityServlet" class="nav-link">
                        <i class="fas fa-history"></i> System Logs
                    </a>
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
                    <form action="${pageContext.request.contextPath}/CourseServlet" method="POST" class="add-course-form">
                        <input type="hidden" name="action" value="ADD">

                        <input type="text" name="courseCode" placeholder="Code" required class="input-small">

                        <input type="text" name="courseName" placeholder="Course Name" required class="input-flex-large">

                        <select name="prerequisiteId" class="input-flex-medium">
                            <option value="">No Prerequisite (Optional)</option>
                            <%
                                if (courses != null) {
                                    for (Course p : courses) {
                            %>
                            <option value="<%= p.getCourseId()%>">
                                Req: <%= p.getCourseCode()%> - <%= p.getCourseName()%></option>
                                <%
                                        }
                                    }
                                %>
                        </select>

                        <input type="number" name="credits" placeholder="Crd" min="1" max="5" required class="input-tiny">

                        <input type="number" name="capacity" placeholder="Cap" min="1" required class="input-tiny">

                        <select name="courseDay" required class="input-day">
                            <option value="Monday">Monday</option>
                            <option value="Tuesday">Tuesday</option>
                            <option value="Wednesday">Wednesday</option>
                            <option value="Thursday">Thursday</option>
                            <option value="Friday">Friday</option>
                        </select>

                        <input type="time" name="courseTime" required class="input-time">

                        <button type="submit" class="btn-add-submit">Add Course</button>
                    </form>
                </div>

                <div class="schedule-container" >
                    <h3>Current Catalog</h3>
                    <div class="search-box-container">
                        <i class="fas fa-search"></i>
                        <input type="text" id="courseSearch" placeholder="Search by name or code..." onkeyup="filterCourses()">
                    </div>
                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th>Code</th> 
                                <th>Name</th> 
                                <th>Credits</th>
                                <th>Cap</th>
                                <th>Day</th> 
                                <th>Time</th>
                                <th style="text-align: center;">Evaluations</th> <th style="text-align: center;">Enrolled List</th>
                                <th style="text-align: center;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (courses != null) {
                                    for (Course c : courses) {
                            %>
                            <tr>
                                <td><%= c.getCourseCode()%></td>

                                <td>
                                    <div class="course-name-wrapper" style="display: flex; flex-direction: column; align-items: flex-start;">
                                        <span class="course-title" style="font-weight: 400;"><%= c.getCourseName()%></span>

                                        <% if (c.isHasPrereq()) {%>
                                        <div class="badge-container" style="margin-top: 4px;">
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

                                <td><%= c.getEnrolledCount()%>/<%= c.getCapacity()%></td> 

                                <td><%= c.getCourseDay()%></td>

                                <td><%= (c.getCourseTime() != null && c.getCourseTime().length() >= 5) ? c.getCourseTime().substring(0, 5) : "TBA"%></td>

                                <td style="text-align: center;">
                                    <button type="button" class="btn-eval" 
                                            onclick="loadCourseEvaluations('<%= c.getCourseId()%>', '<%= c.getCourseCode().replace("'", "\\'")%>')">
                                        <i class="fas fa-star"></i> Eval
                                    </button>
                                </td>

                                <td style="text-align: center;">
                                    <button type="button" class="btn-approve" 
                                            onclick="loadEnrolledStudents('<%= c.getCourseId()%>', '<%= c.getCourseCode().replace("'", "\\'")%>')">
                                        <i class="fas fa-users"></i> View List
                                    </button>
                                </td>

                                <td style="text-align: center;">
                                    <button type="button" class="btn-reject" 
                                            onclick="showDeleteModal('<%= c.getCourseId()%>', '<%= c.getCourseCode()%>', '<%= c.getCourseName()%>')">
                                        <i class="fas fa-trash"></i> Delete
                                    </button>
                                </td>
                            </tr>
                            <%
                                    }
                                }
                            %>
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

        <!--EVAL OVERLAY-->
        <div id="evaluationsOverlay" class="modal-overlay">
            <div class="modal-box" style="width: 600px;">
                <div class="modal-icon" style="color: #facc15;"><i class="fas fa-chart-bar"></i></div>
                <h3>Course Feedback & Stats</h3>
                <p id="evalCourseTitle" style="font-weight: bold; color: #475569; margin-bottom: 10px;">Course Code</p>

                <div id="evalStatsSummary" style="display: flex; justify-content: space-around; background: #fffbeb; padding: 15px; border-radius: 10px; margin-bottom: 20px; border: 1px solid #fef3c7;">
                    <div>
                        <span style="display:block; font-size: 0.75rem; color: #b45309; text-transform: uppercase; font-weight: bold;">Average Rating</span>
                        <span id="avgRatingText" style="font-size: 1.5rem; font-weight: bold; color: #d97706;">0.0</span>
                    </div>
                    <div style="border-left: 1px solid #fde68a;"></div>
                    <div>
                        <span style="display:block; font-size: 0.75rem; color: #b45309; text-transform: uppercase; font-weight: bold;">Total Reviews</span>
                        <span id="totalReviewsText" style="font-size: 1.5rem; font-weight: bold; color: #d97706;">0</span>
                    </div>
                </div>

                <div style="max-height: 300px; overflow-y: auto; border: 1px solid #f1f5f9; border-radius: 8px; padding: 10px;">
                    <div id="evalListContainer">
                    </div>
                </div>

                <div class="modal-actions" style="margin-top: 20px; justify-content: flex-end;">
                    <button type="button" onclick="closeEvalModal()" class="btn-cancel">Close</button>
                </div>
            </div>
        </div>

        <div id="enrolledStudentsOverlay" class="modal-overlay">
            <div class="modal-box" style="width: 500px;">
                <div class="modal-icon" style="color: #6366f1;"><i class="fas fa-user-graduate"></i></div>
                <h3>Enrolled Students</h3>
                <p id="enrolledCourseTitle" style="font-weight: bold; color: #6366f1; margin-bottom: 20px;">Course Code</p>

                <div style="max-height: 300px; overflow-y: auto; border: 1px solid #f1f5f9; border-radius: 8px;">
                    <table class="admin-table" style="margin-top: 0;">
                        <thead style="position: sticky; top: 0; background: #f8fafc; z-index: 1;">
                            <tr>
                                <th style="text-align: center;">ID</th>
                                <th style="text-align: center;">Student Name</th>
                            </tr>
                        </thead>
                        <tbody id="studentListBody">
                        </tbody>
                    </table>
                </div>

                <div class="modal-actions" style="margin-top: 20px; justify-content: flex-end;">
                    <button type="button" onclick="closeEnrolledModal()" class="btn-cancel">Close</button>
                </div>
            </div>
        </div>

        <script src="${pageContext.request.contextPath}/js/admincourse.js"></script>                
    </body>
</html>