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
    const modal = document.getElementById('enrollModal');
    const modalText = document.getElementById('enrollModalText');
    const confirmBtn = document.getElementById('confirmEnrollBtn');

    // Update text dynamically
    modalText.innerHTML = `Are you sure you want to enroll in <strong>${courseCode}</strong>?`;

    // Show the modal
    modal.style.display = 'flex';

    // Set the action for the Confirm button
    confirmBtn.onclick = function () {
        const path = window.location.pathname.split('/')[1];
        window.location.href = "/" + path + "/EnrollmentServlet?action=enroll&courseId=" + courseId;
    };
}

function closeEnrollModal() {
    document.getElementById('enrollModal').style.display = 'none';
}

// Close modal if user clicks anywhere outside the box
window.onclick = function (event) {
    const modal = document.getElementById('enrollModal');
    if (event.target == modal) {
        closeEnrollModal();
    }
}