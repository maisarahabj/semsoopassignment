<%-- 
    Document   : adminstudent
    Created on : 22 Dec 2025, 1:45:25 am
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
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/adminCSS/adminstudent.css?v=1.2">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>
    <body>
        <div class="dashboard-wrapper">
            <aside class="sidebar">
                <div class="logo-section"><div class="logo-box"></div><span class="logo-text">Barfact Admin</span></div>
                <nav class="nav-menu">
                    <a href="${pageContext.request.contextPath}/DashboardServlet" class="nav-link"><i class="fas fa-chart-line"></i> Overview</a>
                    <a href="${pageContext.request.contextPath}/CourseServlet?action=manage" class="nav-link"><i class="fas fa-book-open"></i> Manage Courses</a>
                    <a href="${pageContext.request.contextPath}/AdminManageStudentServlet" class="nav-link active"><i class="fas fa-user-graduate"></i> Manage Students</a>
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
                        <div style="display: flex; align-items: center; gap: 15px;">
                            <h3>Enrolled Students</h3>
                            <span class="badge-count">
                                <%= (request.getAttribute("studentList") != null) ? ((List) request.getAttribute("studentList")).size() : 0%> Students
                            </span>
                        </div>

                        <button type="button" class="btn-add-manual" onclick="openAddModal()">
                            <i class="fas fa-plus"></i> Add Student Manually
                        </button>
                    </div>

                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th>ID</th> <th>Full Name</th> <th>Email</th> <th>Phone</th> <th>GPA</th>
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
                                <td><%= (s.getPhone() != null && !s.getPhone().isEmpty()) ? s.getPhone() : "-"%></td>
                                <td><span class="role-badge"><%= String.format("%.2f", s.getGpa())%></span></td>
                                <td style="text-align: center;">
                                    <div class="action-form">
                                        <button type="button" class="btn-approve" 
                                                onclick="showProfileModal('<%= s.getStudentId()%>', '<%= s.getFirstName()%>', '<%= s.getLastName()%>', '<%= s.getEmail()%>', '<%= s.getPhone()%>', '<%= s.getAddress()%>', '<%= s.getDob()%>', '<%= s.getGpa()%>')">
                                            <i class="fas fa-eye"></i> View
                                        </button>

                                        <button type="button" class="btn-reject" 
                                                onclick="showDeleteModal('<%= s.getStudentId()%>', '<%= s.getUserId()%>', '<%= s.getFirstName()%>', '<%= s.getLastName()%>')">
                                            <i class="fas fa-user-minus"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                            <% }
                                }%>
                        </tbody>
                    </table>
                </div>
            </main>
        </div>

        <div id="addStudentOverlay" class="modal-overlay">
            <div class="modal-box" style="width: 550px;">
                <div class="modal-icon" style="color: #38a169;"><i class="fas fa-user-plus"></i></div>
                <h3>Add New Student Record</h3>
                <p style="color: #666; margin-bottom: 20px;">Enter the student's account and profile details manually.</p>

                <form action="${pageContext.request.contextPath}/AdminManageStudentServlet" method="POST">
                    <input type="hidden" name="action" value="ADD_MANUAL">

                    <div class="modal-form-grid">
                        <div class="input-group full-width">
                            <label>Student ID (Manual Assignment)</label>
                            <input type="text" name="studentId" required class="modal-input" placeholder="e.g., 2023001">
                        </div>

                        <div class="input-group">
                            <label>Account Username</label>
                            <input type="text" name="username" required class="modal-input" placeholder="Username for login">
                        </div>
                        <div class="input-group">
                            <label>Account Password</label>
                            <input type="password" name="password" required class="modal-input" placeholder="Initial password">
                        </div>

                        <div class="input-group">
                            <label>First Name</label>
                            <input type="text" name="firstName" required class="modal-input">
                        </div>
                        <div class="input-group">
                            <label>Last Name</label>
                            <input type="text" name="lastName" required class="modal-input">
                        </div>

                        <div class="input-group full-width">
                            <label>Email Address</label>
                            <input type="email" name="email" required class="modal-input">
                        </div>

                        <div class="input-group">
                            <label>Date of Birth</label>
                            <input type="date" name="dob" required class="modal-input">
                        </div>
                        <div class="input-group">
                            <label>Phone Number</label>
                            <input type="text" name="phone" class="modal-input" placeholder="Optional">
                        </div>

                        <div class="input-group full-width">
                            <label>Home Address</label>
                            <textarea name="address" rows="2" class="modal-input" style="height: auto;"></textarea>
                        </div>
                    </div>

                    <div class="modal-actions" style="margin-top: 30px; justify-content: flex-end;">
                        <button type="button" onclick="closeAddModal()" class="btn-cancel">Discard</button>
                        <button type="submit" class="btn-save-student">Create Record</button>
                    </div>
                </form>
            </div>
        </div>

        <div id="deleteOverlay" class="modal-overlay">
            <div class="modal-box">
                <div class="modal-icon"><i class="fas fa-exclamation-triangle"></i></div>
                <h3>Remove Student?</h3>
                <p id="removeMessage">Are you sure you want to remove this student?</p>
                <div class="modal-actions">
                    <button type="button" onclick="closeDeleteModal()" class="btn-cancel">Cancel</button>

                    <form action="${pageContext.request.contextPath}/AdminManageStudentServlet" method="POST">
                        <input type="hidden" name="action" value="DELETE">
                        <input type="hidden" name="studentId" id="modalStudentId">
                        <input type="hidden" name="userId" id="modalUserId"> 

                        <button type="submit" class="btn-confirm">Permanently Remove</button>
                    </form>
                </div>
            </div>
        </div>

        <div id="profileOverlay" class="modal-overlay">
            <div class="modal-box" style="width: 600px;">
                <div class="modal-icon" style="color: #007bff;"><i class="fas fa-id-card"></i></div>
                <h3 id="viewFullName">Student Profile</h3>
                <p id="viewStudentId" style="color: #007bff; font-weight: bold; margin-bottom: 5px;">#ID</p>
                <p id="viewRegisteredOn" style="color: #38a169; font-size: 0.85rem; font-weight: 600; margin-top: 2px; margin-bottom: 25px;">Registered on: N/A</p>

                <div class="modal-form-grid">
                    <div class="input-group">
                        <label>Email</label>
                        <p id="viewEmail" class="view-data">-</p>
                    </div>
                    <div class="input-group">
                        <label>Phone</label>
                        <p id="viewPhone" class="view-data">-</p>
                    </div>
                    <div class="input-group">
                        <label>Date of Birth</label>
                        <p id="viewDob" class="view-data">-</p>
                    </div>
                    <div class="input-group">
                        <label>Current GPA</label>
                        <p id="viewGpa" class="view-data">-</p>
                    </div>
                    <div class="input-group full-width">
                        <label>Home Address</label>
                        <p id="viewAddress" class="view-data">-</p>
                    </div>
                    <div class="input-group full-width" style="margin-top: 20px;">
                        <label style="color: #007bff; border-bottom: 2px solid #f0f4f8; padding-bottom: 5px; margin-bottom: 10px; display: flex; justify-content: space-between; align-items: center;">
                            <span><i class="fas fa-book-reader"></i> Enrolled Courses</span>

                            <div class="inline-enroll-form">
                                <select id="enrollCourseSelect" class="modal-input-mini">
                                    <option value="">-- Enroll in Course --</option>
                                    <%
                                        List<com.sems.model.Course> allCourses = (List<com.sems.model.Course>) request.getAttribute("allCoursesList");
                                        if (allCourses != null) {
                                            for (com.sems.model.Course c : allCourses) {
                                    %>
                                    <option value="<%= c.getCourseId()%>"><%= c.getCourseCode()%> - <%= c.getCourseName()%></option>
                                    <%
                                            }
                                        }
                                    %>
                                </select>
                                <button type="button" class="btn-enroll-mini" onclick="enrollStudentAction()">Add</button>
                            </div>
                        </label>

                        <div class="course-list-container">
                            <table class="modal-course-table">
                                <thead>
                                    <tr>
                                        <th>Code</th>
                                        <th>Course Name</th>
                                        <th>Schedule</th>
                                        <th style="text-align: left;">Action</th>
                                    </tr>
                                </thead>
                                <tbody id="viewCourseList">
                                    <tr><td colspan="4" style="text-align:center;">Loading courses...</td></tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                <!<!-- Confirm deletion -->
                <div id="unenrollConfirmOverlay" class="modal-overlay" style="display:none; z-index: 2000;">
                    <div class="modal-box" style="width: 400px; text-align: center;">
                        <div class="modal-icon" style="color: #e11d48; background: #fff1f2;"><i class="fas fa-book-minus"></i></div>
                        <h3>Unenroll Student?</h3>
                        <p style="color: #64748b; margin-top: 10px;">Are you sure you want to remove this student from the course?</p>

                        <div class="modal-actions" style="margin-top: 25px; justify-content: center; gap: 15px;">
                            <button type="button" onclick="closeUnenrollModal()" class="btn-cancel">Cancel</button>
                            <button type="button" id="confirmUnenrollBtn" class="btn-confirm" style="background: #e11d48;">Remove Course</button>
                        </div>
                    </div>
                </div>  
                <!--confirm add-->
                <div class="toast-container">
                    <div id="successToast" class="toast">
                        <i class="fas fa-check-circle"></i>
                        <span id="toastMessage">Action Successful!</span>
                    </div>
                </div>

                <div class="modal-actions" style="margin-top: 30px; justify-content: flex-end;">
                    <button type="button" onclick="closeProfileModal()" class="btn-cancel">Close</button>
                </div>
            </div>
        </div>

        <script>
            // Define the context path for the JS file to use
            window.contextPath = '${pageContext.request.contextPath}';
        </script>
        <script src="${pageContext.request.contextPath}/js/adminstudent.js"></script>
</html>