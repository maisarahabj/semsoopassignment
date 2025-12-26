/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


// JavaScript to hide N/A rows for students only
const role = "<%= userRole %>";
if (role === 'student') {
    document.querySelectorAll('.grade-row').forEach(row => {
        if (row.getAttribute('data-grade') === 'N/A') {
            row.style.display = 'none';
        }
    });
}

<script src="${pageContext.request.contextPath}/js/viewgrades.js"></script>