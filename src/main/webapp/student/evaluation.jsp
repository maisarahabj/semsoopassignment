<%@page import="java.util.List"%>
<%@page import="com.sems.model.Enrollment"%>
<%@page import="com.sems.model.Student"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Barfact University | Course Evaluation</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/studentCSS/dashboard.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/studentCSS/evaluation.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <script src="${pageContext.request.contextPath}/js/evaluation.js" defer></script>
    </head>
    <body>
        <%
            if (session.getAttribute("userId") == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            Student student = (Student) request.getAttribute("student");
            String fullName = (student != null) ? student.getFirstName() + " " + student.getLastName() : "Student";
            List<Enrollment> transcript = (List<Enrollment>) request.getAttribute("transcript");
            List<Integer> submittedIds = (List<Integer>) request.getAttribute("submittedCourseIds");
        %>

        <div class="dashboard-wrapper">
            <aside class="sidebar">
                <div class="logo-section">
                    <img src="${pageContext.request.contextPath}/assets/cat.png" class="logo-img" style="width: 50px; height: 50px;">
                    <span class="logo-text">Barfact Uni</span>
                </div>
                <nav class="nav-menu">
                    <a href="${pageContext.request.contextPath}/DashboardServlet" class="nav-link">
                        <i class="fas fa-home"></i> Dashboard
                    </a>
                    <a href="${pageContext.request.contextPath}/AcademicCalendarServlet" class="nav-link">
                        <i class="fas fa-calendar-check"></i> Academic Calendar
                    </a>
                    <a href="${pageContext.request.contextPath}/student/MyCourseServlet" class="nav-link">
                        <i class="fas fa-book"></i> My Classes
                    </a>
                    <a href="${pageContext.request.contextPath}/student/AddCourseServlet" class="nav-link">
                        <i class="fas fa-plus-square"></i> Add Subjects
                    </a>
                    <a href="${pageContext.request.contextPath}/GradeServlet" class="nav-link">
                        <i class="fas fa-poll-h"></i> My Results
                    </a>
                    <a href="${pageContext.request.contextPath}/student/SemesterResultsServlet" class="nav-link">
                        <i class="fas fa-calendar-alt"></i> Semester Results
                    </a>
                    <a href="${pageContext.request.contextPath}/EvaluationServlet" class="nav-link active">
                        <i class="fas fa-star"></i> Course Evaluation
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
                        <h1>Course <span class="highlight-blue">Evaluation</span></h1>
                        <p>Share your feedback to help us improve your learning experience.</p>
                    </div>
                    <div class="banner-icon">
                        <i class="fas fa-comment-dots"></i>
                    </div>
                </div>

                <% if ("success".equals(request.getParameter("status"))) { %>
                <div style="background-color: #f0fdf4; color: #166534; margin-top: 15px; padding: 15px; border-radius: 8px; margin-bottom: 5px; border: 1px solid #bbf7d0;">
                    <i class="fas fa-check-circle"></i> Feedback submitted successfully!
                </div>
                <% } %>

                <div class="schedule-container">
                    <h3>Completed Courses</h3>
                    <table class="admin-table" style="width: 100%; border-collapse: collapse;">
                        <thead>
                            <tr style="background: #f8fafc; text-align: left; border-bottom: 2px solid #e2e8f0;">
                                <th style="padding: 15px;">Subject Code</th>
                                <th style="padding: 15px;">Subject Name</th>
                                <th style="padding: 15px; text-align: center;">Grade</th>
                                <th style="padding: 15px; text-align: center;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (transcript != null && !transcript.isEmpty()) {
                                    for (Enrollment e : transcript) {
                                        boolean alreadySubmitted = false;
                                        if (submittedIds != null) {
                                            for (Integer subId : submittedIds) {
                                                if (subId != null && subId.intValue() == e.getCourseId()) {
                                                    alreadySubmitted = true;
                                                    break;
                                                }
                                            }
                                        }
                            %>
                            <tr style="border-bottom: 1px solid #f1f5f9;">
                                <td style="padding: 15px;"><strong><%= e.getCourseCode()%></strong></td>
                                <td style="padding: 15px;"><%= e.getCourseName()%></td>
                                <td style="padding: 15px; text-align: center;"><%= e.getGrade()%></td>
                                <td style="padding: 15px; text-align: center;">
                                    <% if (alreadySubmitted) { %>
                                    <span class="badge-submitted"><i class="fas fa-check"></i> Submitted</span>
                                    <% } else {%>
                                    <button class="btn-evaluate" 
                                            onclick="openEvalModal('<%= e.getCourseId()%>', '<%= e.getCourseName().replace("'", "\\'")%>')">
                                        Evaluate
                                    </button>
                                    <% } %>
                                </td>
                            </tr>
                            <% }
                            } else { %>
                            <tr><td colspan="4" style="padding:20px; text-align:center;">No completed courses available for evaluation.</td></tr>
                            <% }%>
                        </tbody>
                    </table>
                </div>
            </main>

            <aside class="right-panel">
                <div class="profile-avatar">
                    <i class="fas fa-user"></i>
                </div>
                <h2 class="profile-name"><%= fullName%></h2>
                <p class="profile-id">Student ID: #<%= (student != null) ? student.getStudentId() : "N/A"%></p>

                <div class="term-info-card">
                    <h4><i class="fas fa-info-circle"></i> Instructions</h4>
                    <p style="font-size: 0.85rem; line-height: 1.4; color: #64748b;">
                        Evaluations are <b>anonymous</b>. Admins can only see your identity with a security override.
                    </p>
                </div>

                <a href="${pageContext.request.contextPath}/auth/LogoutServlet" class="btn-logout">
                    <i class="fas fa-sign-out-alt"></i> Log Out
                </a>
            </aside>
        </div>

        <div id="evalModal" class="modal">
            <div class="modal-content">
                <h2 id="modalCourseName">Evaluate Course</h2>
                <form action="${pageContext.request.contextPath}/EvaluationServlet" method="POST">
                    <input type="hidden" name="action" value="submitEvaluation">
                    <input type="hidden" name="courseId" id="modalCourseId">
                    <input type="hidden" name="studentId" value="<%= session.getAttribute("studentId")%>">

                    <div class="form-group">
                        <label>Rating</label>
                        <select name="rating" required>
                            <option value="5">5 - Excellent</option>
                            <option value="4">4 - Good</option>
                            <option value="3">3 - Satisfactory</option>
                            <option value="2">2 - Poor</option>
                            <option value="1">1 - Very Poor</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Comments</label>
                        <textarea name="comments" rows="4" required></textarea>
                    </div>
                    <div style="display: flex; gap: 10px;">
                        <button type="submit" class="btn-evaluate" style="flex: 1;">Submit</button>
                        <button type="button" onclick="closeEvalModal()" style="flex: 1; background: #eee; border:none; cursor:pointer; border-radius:6px;">Cancel</button>
                    </div>
                </form>
            </div>
        </div>

        <script src="${pageContext.request.contextPath}/js/evaluation.js" defer></script>

    </body>
</html>