// --- Modal Toggle Functions ---
function openAddModal() {
    document.getElementById('addStudentOverlay').style.display = 'flex';
}

function closeAddModal() {
    document.getElementById('addStudentOverlay').style.display = 'none';
}

function closeDeleteModal() {
    document.getElementById('deleteOverlay').style.display = 'none';
}

function closeProfileModal() {
    document.getElementById('profileOverlay').style.display = 'none';
}

// --- Logic for Delete Modal ---
function showDeleteModal(studentId, userId, firstName, lastName) {
    // Fill the hidden inputs that the Servlet will read
    document.getElementById('modalStudentId').value = studentId;
    document.getElementById('modalUserId').value = userId;

    // Update the confirmation text
    document.getElementById('removeMessage').innerHTML =
            "Are you sure you want to permanently remove <strong>" + firstName + " " + lastName + "</strong> and their login account?";

    document.getElementById('deleteOverlay').style.display = 'flex';
}

// --- Logic for View Profile Modal ---
// --- Updated Logic for View Profile Modal with Course Management ---
let currentViewedStudentId = null; // Global tracker for Enroll/Drop actions

function showProfileModal(id, fName, lName, email, phone, address, dob, gpa) {
    currentViewedStudentId = id; // Set the ID for current actions

    document.getElementById('viewFullName').innerText = fName + " " + lName;

    const idElement = document.getElementById('viewStudentIdDisplay') || document.getElementById('viewStudentId');
    if (idElement)
        idElement.innerText = "Student ID: #" + id;

    document.getElementById('viewEmail').innerText = email;
    document.getElementById('viewPhone').innerText = (phone && phone !== "null") ? phone : "Not Provided";
    document.getElementById('viewDob').innerText = dob;
    document.getElementById('viewGpa').innerText = parseFloat(gpa).toFixed(2);
    document.getElementById('viewAddress').innerText = (address && address !== "null") ? address : "No address on file";

    // Trigger the course list fetch
    loadEnrolledCourses(id);

    document.getElementById('profileOverlay').style.display = 'flex';
}

// NEW: Helper function to load courses via AJAX
function loadEnrolledCourses(studentId) {
    const tbody = document.getElementById('viewCourseList');
    tbody.innerHTML = '<tr><td colspan="4" style="text-align:center;">Loading...</td></tr>';

    // Calling the consistent Servlet name you created
    fetch(`${window.contextPath}/AdminGetStudentCoursesServlet?studentId=${studentId}`)
            .then(response => response.json())
            .then(data => {
                tbody.innerHTML = '';
                if (data.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="4" style="text-align:center;">No courses found.</td></tr>';
                } else {
                    data.forEach(c => {
                        tbody.innerHTML += `
                        <tr>
                            <td><span class="modal-code-badge">${c.code}</span></td>
                            <td>${c.name}</td>
                            <td>${c.day} ${c.time}</td>
                            <td style="text-align: right;">
                                <button type="button" class="btn-drop-mini" onclick="dropCourseAction(${c.id})">
                                    <i class="fas fa-trash-alt"></i>
                                </button>
                            </td>
                        </tr>`;
                    });
                }
            })
            .catch(err => {
                console.error("Fetch error:", err);
                tbody.innerHTML = '<tr><td colspan="4" style="text-align:center; color:red;">Error loading courses.</td></tr>';
            });
}

// NEW: Action function to Enroll
function enrollStudentAction() {
    const courseId = document.getElementById('enrollCourseSelect').value;
    if (!courseId) {
        alert("Please select a course first.");
        return;
    }

    const params = new URLSearchParams();
    params.append('action', 'ENROLL');
    params.append('studentId', currentViewedStudentId);
    params.append('courseId', courseId);

    fetch(`${window.contextPath}/AdminGetStudentCoursesServlet`, {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: params
    })
            .then(res => res.text())
            .then(res => {
                if (res.trim() === "success") {
                    loadEnrolledCourses(currentViewedStudentId);
                    document.getElementById('enrollCourseSelect').value = ""; // Reset dropdown
                } else {
                    alert("Failed to enroll. Check if course is full or student is already enrolled.");
                }
            });
}

// NEW: Action function to Drop
function dropCourseAction(courseId) {
    if (!confirm("Are you sure you want to unenroll the student from this course?"))
        return;

    const params = new URLSearchParams();
    params.append('action', 'DROP');
    params.append('studentId', currentViewedStudentId);
    params.append('courseId', courseId);

    fetch(`${window.contextPath}/AdminGetStudentCoursesServlet`, {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: params
    })
            .then(res => res.text())
            .then(res => {
                if (res.trim() === "success") {
                    loadEnrolledCourses(currentViewedStudentId);
                } else {
                    alert("Error: Could not drop the course.");
                }
            });
}

// --- SINGLE Window Click Listener (Handles all modals) ---
window.onclick = function (event) {
    const deleteModal = document.getElementById('deleteOverlay');
    const addModal = document.getElementById('addStudentOverlay');
    const profileModal = document.getElementById('profileOverlay');

    if (event.target == deleteModal)
        closeDeleteModal();
    if (event.target == addModal)
        closeAddModal();
    if (event.target == profileModal)
        closeProfileModal();
};