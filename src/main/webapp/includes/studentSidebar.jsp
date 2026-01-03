<%-- 
    Reusable Student Sidebar Navigation
    Usage: Set request attribute "activePage" before including this file
    Example: request.setAttribute("activePage", "dashboard");
--%>
<%@page import="com.sems.model.Student"%>
<%
    String activePage = (String) request.getAttribute("activePage");
    if (activePage == null) activePage = "";
    Student student = (Student) request.getAttribute("student");
%>
<aside class="sidebar">
    <div class="logo-section">
        <img src="${pageContext.request.contextPath}/assets/cat.png" class="logo-img" style="width: 50px; height: 50px;">
        <span class="logo-text">Barfact Uni</span>
    </div>

    <nav class="nav-menu">
        <a href="${pageContext.request.contextPath}/DashboardServlet" class="nav-link <%= "dashboard".equals(activePage) ? "active" : "" %>">
            <i class="fas fa-home"></i> Dashboard
        </a>
        <a href="${pageContext.request.contextPath}/AcademicCalendarServlet" class="nav-link <%= "calendar".equals(activePage) ? "active" : "" %>">
            <i class="fas fa-calendar-check"></i> Academic Calendar
        </a>
        <a href="${pageContext.request.contextPath}/student/MyCourseServlet" class="nav-link <%= "mycourse".equals(activePage) ? "active" : "" %>">
            <i class="fas fa-book"></i> My Classes
        </a>
        <a href="${pageContext.request.contextPath}/student/AddCourseServlet" class="nav-link <%= "addcourse".equals(activePage) ? "active" : "" %>">
            <i class="fas fa-plus-square"></i> Add Subjects
        </a>
        <a href="${pageContext.request.contextPath}/GradeServlet" class="nav-link <%= "grade".equals(activePage) ? "active" : "" %>">
            <i class="fas fa-poll-h"></i> My Results
        </a>
        <a href="${pageContext.request.contextPath}/student/SemesterResultsServlet" class="nav-link <%= "semesterresults".equals(activePage) ? "active" : "" %>">
            <i class="fas fa-calendar-alt"></i> Semester Results
        </a>
        <a href="${pageContext.request.contextPath}/EvaluationServlet" class="nav-link <%= "evaluation".equals(activePage) ? "active" : "" %>">
            <i class="fas fa-star"></i> Course Evaluation
        </a>
        <a href="${pageContext.request.contextPath}/ProfileServlet" class="nav-link <%= "profile".equals(activePage) ? "active" : "" %>">
            <i class="fas fa-user"></i> Profile
        </a>
    </nav>

    <div class="cgpa-container">
        <div class="cgpa-value"><%= (student != null) ? student.getGpa() : "0.00"%></div>
        <p class="cgpa-label">Current CGPA</p>
    </div>
</aside>
