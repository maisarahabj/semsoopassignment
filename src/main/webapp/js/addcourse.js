/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

function toggleAddMode() {
    const btn = document.getElementById('toggleAddBtn');
    const cols = document.querySelectorAll('.add-column');
    
    btn.classList.toggle('active');
    cols.forEach(col => {
        col.style.display = (col.style.display === 'none' || col.style.display === '') ? 'table-cell' : 'none';
    });

    btn.innerHTML = btn.classList.contains('active') ? 
        '<i class="fas fa-check-circle"></i> Done Registering' : 
        '<i class="fas fa-plus-circle"></i> Register Mode';
}

function filterCourses() {
    let input = document.getElementById('courseSearch').value.toLowerCase();
    let rows = document.querySelectorAll('#courseTable tbody tr');

    rows.forEach(row => {
        let text = row.innerText.toLowerCase();
        row.style.display = text.includes(input) ? '' : 'none';
    });
}

function confirmEnroll(courseId, courseCode) {
    if (confirm("Enroll in " + courseCode + "?")) {
        const path = window.location.pathname.split('/')[1];
        window.location.href = "/" + path + "/EnrollmentServlet?action=enroll&courseId=" + courseId;
    }
}
