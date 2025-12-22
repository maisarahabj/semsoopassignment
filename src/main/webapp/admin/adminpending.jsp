<%-- 
    Document   : adminpending
    Created on : 23 Dec 2025, 3:48:38 am
    Author     : maisarahabjalil
    
    approving pending neww users
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Admin Approval</title>
    <link rel="stylesheet" href="admin_pending.css">
</head>
<body>
    <h2>Users Awaiting Approval</h2>
    <table border="1">
        <tr>
            <th>Name</th>
            <th>Username</th>
            <th>Role</th>
            <th>Email</th>
            <th>Actions</th>
        </tr>
        <c:forEach var="user" items="${pendingUsers}">
            <tr>
                <td>${user.fullName}</td>
                <td>${user.username}</td>
                <td>${user.role}</td>
                <td>${user.email}</td>
                <td>
                    <form action="AdminPendingServlet" method="POST" style="display:inline;">
                        <input type="hidden" name="userId" value="${user.userId}">
                        <input type="hidden" name="action" value="approve">
                        <button type="submit">Approve</button>
                    </form>
                    <form action="AdminPendingServlet" method="POST" style="display:inline;">
                        <input type="hidden" name="userId" value="${user.userId}">
                        <input type="hidden" name="action" value="reject">
                        <button type="submit" onclick="return confirm('Delete this request?')">Reject</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>
