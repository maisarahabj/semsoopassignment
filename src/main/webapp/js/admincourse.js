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

// show eval
function loadCourseEvaluations(courseId, courseCode) {
    document.getElementById('evalCourseTitle').innerText = "Feedback for " + courseCode;
    const container = document.getElementById('evalListContainer');
    const avgText = document.getElementById('avgRatingText');
    const totalText = document.getElementById('totalReviewsText');

    container.innerHTML = '<p style="text-align:center;">Loading evaluations...</p>';
    document.getElementById('evaluationsOverlay').style.display = 'flex';

    // Update the URL to match your Servlet mapping
    fetch(`EvaluationServlet?action=getReviews&courseId=${courseId}`)
            .then(response => {
                if (!response.ok)
                    throw new Error("Server Error");
                return response.json();
            })
            .then(data => {
                container.innerHTML = '';
                if (data.length === 0) {
                    avgText.innerText = "0.0";
                    totalText.innerText = "0";
                    container.innerHTML = '<div style="text-align:center; padding:20px; color:#64748b;">No reviews yet.</div>';
                } else {
                    let sum = 0;
                    data.forEach(review => {
                        sum += review.rating;
                        let stars = '';
                        for (let i = 1; i <= 5; i++) {
                            stars += `<i class="${i <= review.rating ? 'fas' : 'far'} fa-star" style="color: #facc15;"></i>`;
                        }

                        container.innerHTML += `
                        <div class="eval-card" style="text-align: left; background: #f8fafc; padding: 15px; border-radius: 10px; margin-bottom: 12px; border-left: 4px solid #facc15;">
                            <div style="display: flex; justify-content: space-between; font-size: 0.8rem; color: #64748b; margin-bottom: 5px;">
                                <span>${review.submittedDate}</span>
                                <span id="reveal-text-${review.evalId}">
                                    <button onclick="revealStudent('${review.evalId}')" style="background:none; border:none; color:#3b82f6; cursor:pointer; font-size:0.75rem;">
                                        <i class="fas fa-eye"></i> Reveal Identity
                                    </button>
                                </span>
                            </div>
                            <div>${stars}</div>
                            <div style="margin-top: 8px; font-size: 0.9rem; color: #1e293b; font-style: italic;">"${review.comments}"</div>
                        </div>`;
                    });
                    avgText.innerText = (sum / data.length).toFixed(1);
                    totalText.innerText = data.length;
                }
            })
            .catch(err => {
                container.innerHTML = '<p style="color:red; text-align:center;">Failed to load data. Check console for details.</p>';
                console.error(err);
            });
}

function revealStudent(evalId) {
    const password = prompt("Enter Admin Security Password:");
    if (!password)
        return;

    fetch('EvaluationServlet', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: `action=revealIdentity&evalId=${evalId}&securityPassword=${password}`
    })
            .then(response => {
                if (response.ok)
                    return response.text();
                throw new Error('Access Denied');
            })
            .then(name => {
                document.getElementById(`reveal-text-${evalId}`).innerHTML =
                        `<b style="color:#e11d48"><i class="fas fa-user-shield"></i> ${name}</b>`;
            })
            .catch(err => alert("Incorrect Password. Identity remains hidden."));
}

function closeEvalModal() {
    document.getElementById('evaluationsOverlay').style.display = 'none';
}