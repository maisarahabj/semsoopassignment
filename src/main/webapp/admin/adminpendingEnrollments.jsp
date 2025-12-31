<%-- 
    Admin Pending Enrollments - Approval Page
    Author: maisarahabjalil
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.Map" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Pending Enrollment Approvals - SEMS</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/adminCSS/adminpending.css">
    </head>
    <body>
        <%@ include file="../assets/header.html" %>
        
        <div class="container">
            <h1>Pending Enrollment Requests</h1>
            
            <%
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> pendingEnrollments = 
                    (List<Map<String, Object>>) request.getAttribute("pendingEnrollments");
                
                if (pendingEnrollments == null || pendingEnrollments.isEmpty()) {
            %>
                <p>No pending enrollment requests at this time.</p>
            <%
                } else {
            %>
            
            <table class="pending-table">
                <thead>
                    <tr>
                        <th>Enrollment ID</th>
                        <th>Student ID</th>
                        <th>Student Name</th>
                        <th>Course Code</th>
                        <th>Course Name</th>
                        <th>Request Date</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (Map<String, Object> enrollment : pendingEnrollments) {
                            int enrollmentId = (Integer) enrollment.get("enrollmentId");
                            int studentId = (Integer) enrollment.get("studentId");
                            String firstName = (String) enrollment.get("firstName");
                            String lastName = (String) enrollment.get("lastName");
                            String courseCode = (String) enrollment.get("courseCode");
                            String courseName = (String) enrollment.get("courseName");
                            java.sql.Timestamp enrollmentDate = (java.sql.Timestamp) enrollment.get("enrollmentDate");
                    %>
                    <tr>
                        <td><%= enrollmentId %></td>
                        <td><%= studentId %></td>
                        <td><%= firstName %> <%= lastName %></td>
                        <td><%= courseCode %></td>
                        <td><%= courseName %></td>
                        <td><%= enrollmentDate %></td>
                        <td>
                            <form method="POST" action="${pageContext.request.contextPath}/AdminEnrollmentApprovalServlet" style="display:inline;">
                                <input type="hidden" name="enrollmentId" value="<%= enrollmentId %>">
                                <input type="hidden" name="action" value="APPROVE">
                                <button type="submit" class="btn-approve">Approve</button>
                            </form>
                            <form method="POST" action="${pageContext.request.contextPath}/AdminEnrollmentApprovalServlet" style="display:inline;">
                                <input type="hidden" name="enrollmentId" value="<%= enrollmentId %>">
                                <input type="hidden" name="action" value="REJECT">
                                <button type="submit" class="btn-reject">Reject</button>
                            </form>
                        </td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
            
            <%
                }
            %>
            
            <div style="margin-top: 20px;">
                <a href="${pageContext.request.contextPath}/admin/admindash.jsp" class="btn-back">Back to Dashboard</a>
            </div>
        </div>
        
        <%@ include file="../assets/footer.html" %>
    </body>
</html>
