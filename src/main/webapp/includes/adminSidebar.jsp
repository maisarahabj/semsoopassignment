<%-- 
    Reusable Admin Sidebar Navigation
    Usage: Set request attribute "activePage" before including this file
    Example: request.setAttribute("activePage", "dashboard");
--%>
<%
    String activePage = (String) request.getAttribute("activePage");
    if (activePage == null) activePage = "";
%>
<aside class="sidebar">
    <div class="logo-section">
        <img src="${pageContext.request.contextPath}/assets/cat.png" class="logo-img" style="width: 50px; height: 50px;">
        <span class="logo-text">Barfact Admin</span>
    </div>

    <nav class="nav-menu">
        <a href="${pageContext.request.contextPath}/DashboardServlet" class="nav-link <%= "dashboard".equals(activePage) ? "active" : "" %>">
            <i class="fas fa-chart-line"></i> Overview
        </a>
        <a href="${pageContext.request.contextPath}/SemesterServlet" class="nav-link <%= "semester".equals(activePage) ? "active" : "" %>">
            <i class="fas fa-calendar-alt"></i> Manage Semesters
        </a>
        <a href="${pageContext.request.contextPath}/AcademicCalendarServlet" class="nav-link <%= "calendar".equals(activePage) ? "active" : "" %>">
            <i class="fas fa-calendar-check"></i> Academic Calendar
        </a>
        <a href="${pageContext.request.contextPath}/BulkCourseMigrationServlet" class="nav-link <%= "migration".equals(activePage) ? "active" : "" %>">
            <i class="fas fa-exchange-alt"></i> Course Migration
        </a>
        <a href="${pageContext.request.contextPath}/CourseServlet" class="nav-link <%= "course".equals(activePage) ? "active" : "" %>">
            <i class="fas fa-book-open"></i> Manage Courses
        </a>
        <a href="${pageContext.request.contextPath}/AdminManageStudentServlet" class="nav-link <%= "student".equals(activePage) ? "active" : "" %>">
            <i class="fas fa-user-graduate"></i> Manage Students
        </a>
        <a href="${pageContext.request.contextPath}/GradeServlet" class="nav-link <%= "grade".equals(activePage) ? "active" : "" %>">
            <i class="fas fa-graduation-cap"></i> Grade Management
        </a>
        <a href="${pageContext.request.contextPath}/auth/AdminPendingServlet" class="nav-link <%= "pending".equals(activePage) ? "active" : "" %>">
            <i class="fas fa-clock"></i> Pending Approvals
        </a>
        <a href="${pageContext.request.contextPath}/ActivityServlet" class="nav-link <%= "logs".equals(activePage) ? "active" : "" %>">
            <i class="fas fa-history"></i> System Logs
        </a>
        <a href="${pageContext.request.contextPath}/AdminReportServlet" class="nav-link <%= "report".equals(activePage) ? "active" : "" %>">
            <i class="fas fa-file-alt"></i> Academic Report
        </a>
    </nav>
</aside>
