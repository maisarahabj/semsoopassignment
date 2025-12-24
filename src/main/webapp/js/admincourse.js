/**
 * admincourse.js - Handles Modals and AJAX for Course Management
 */

function showDeleteModal(id, code, name) {
    document.getElementById('modalCourseId').value = id;
    document.getElementById('deleteMessage').innerHTML =
            "Confirm deletion of <strong>" + code + " " + name + "</strong> permanently?";
    document.getElementById('deleteOverlay').style.display = 'flex';
}

function closeDeleteModal() {
    document.getElementById('deleteOverlay').style.display = 'none';
}

function filterCourses() {
    const input = document.getElementById('courseSearch').value.toLowerCase();
    const tableRows = document.querySelectorAll('.admin-table tbody tr');

    tableRows.forEach(row => {
        const courseCode = row.cells[0].textContent.toLowerCase();
        const courseName = row.cells[1].textContent.toLowerCase();
        row.style.display = (courseCode.includes(input) || courseName.includes(input)) ? "" : "none";
    });
}

function loadEnrolledStudents(courseId, courseCode) {
    const overlay = document.getElementById('enrolledStudentsOverlay');
    const title = document.getElementById('enrolledCourseTitle');
    const tbody = document.getElementById('studentListBody');

    title.innerText = "Students enrolled in " + courseCode;
    tbody.innerHTML = '<tr><td colspan="2" style="text-align:center;">Loading...</td></tr>';
    overlay.style.display = 'flex';

    // FIX: Using relative URL to avoid 'undefined/' errors
    const url = 'AdminGetStudentCoursesServlet?courseId=' + courseId;

    fetch(url)
            .then(response => {
                if (!response.ok)
                    throw new Error('Network response was not ok (404/500)');
                return response.text(); // Servlet returns HTML rows, so we use .text()
            })
            .then(html => {
                tbody.innerHTML = html; // Directly inject the HTML rows
            })
            .catch(error => {
                console.error('Error fetching students:', error);
                tbody.innerHTML = '<tr><td colspan="2" style="text-align:center; color:red;">Error: ' + error.message + '</td></tr>';
            });
}

function closeEnrolledModal() {
    document.getElementById('enrolledStudentsOverlay').style.display = 'none';
}

// Global click handler for closing modals
window.onclick = function (event) {
    const deleteOverlay = document.getElementById('deleteOverlay');
    const enrolledOverlay = document.getElementById('enrolledStudentsOverlay');

    if (event.target === deleteOverlay)
        closeDeleteModal();
    if (event.target === enrolledOverlay)
        closeEnrolledModal();
};