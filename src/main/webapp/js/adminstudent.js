/**
 * adminstudent.js
 * Comprehensive logic for Student Directory Management
 */

// --- Global State ---
let currentViewedStudentId = null;
let pendingDropCourseId = null;

// --- Modal Toggle Functions ---

function openAddModal() {
    document.getElementById('addStudentOverlay').style.display = 'flex';
    document.body.style.overflow = 'hidden'; // Lock background
}

function closeAddModal() {
    document.getElementById('addStudentOverlay').style.display = 'none';
    document.body.style.overflow = 'auto'; // Unlock background
}

function closeDeleteModal() {
    document.getElementById('deleteOverlay').style.display = 'none';
    document.body.style.overflow = 'auto';
}

function closeProfileModal() {
    document.getElementById('profileOverlay').style.display = 'none';
    document.body.style.overflow = 'auto'; // Unlock background
    resetAdminModalState(); // Reset toggle when closing
}

function closeUnenrollModal() {
    document.getElementById('unenrollConfirmOverlay').style.display = 'none';
    pendingDropCourseId = null;
}

// --- Student Deletion Logic ---

function showDeleteModal(studentId, userId, firstName, lastName) {
    document.getElementById('modalStudentId').value = studentId;
    document.getElementById('modalUserId').value = userId;
    document.getElementById('removeMessage').innerHTML =
            `Are you sure you want to permanently remove <strong>${firstName} ${lastName}</strong> and their login account?`;
    document.getElementById('deleteOverlay').style.display = 'flex';
    document.body.style.overflow = 'hidden';
}

// --- Profile View & Editing Logic ---

/**
 * Opens the profile modal and populates student details
 */
function showProfileModal(id, userId, fName, lName, email, phone, address, dob, gpa, regOn, username) {
    currentViewedStudentId = id;

    // 1. Populate View Mode (Text Labels)
    document.getElementById('viewFullName').innerText = fName + " " + lName;
    document.getElementById('viewStudentId').innerText = "Student ID: #" + id;
    document.getElementById('viewUsername').innerText = (username && username !== "null") ? username : "-";
    document.getElementById('viewEmail').innerText = email;
    document.getElementById('viewPhone').innerText = (phone && phone !== "null") ? phone : "Not Provided";
    document.getElementById('viewDob').innerText = dob;
    document.getElementById('viewGpa').innerText = parseFloat(gpa).toFixed(2);
    document.getElementById('viewAddress').innerText = (address && address !== "null") ? address : "No address";

    // 2. Populate Edit Mode (Form Inputs)
    document.getElementById('editStudentId').value = id;
    document.getElementById('editUserId').value = userId;
    document.getElementById('editFName').value = fName;
    document.getElementById('editLName').value = lName;
    document.getElementById('editUsername').value = (username && username !== "null") ? username : "";
    document.getElementById('editEmail').value = email;
    document.getElementById('editPhone').value = (phone && phone !== "null") ? phone : "";
    document.getElementById('editDob').value = dob;
    document.getElementById('editAddress').value = (address && address !== "null") ? address : "";

    // GPA hidden value (needed for form submit) but we won't show the input to user
    document.getElementById('editGpa').value = gpa;
    document.getElementById('editPassword').value = "";

    resetAdminModalState();
    loadEnrolledCourses(id); // Enrollment logic

    document.getElementById('profileOverlay').style.display = 'flex';
    document.body.style.overflow = 'hidden';
}

/**
 * Swaps between the View (Text) and Edit (Inputs) states
 */
function toggleAdminEdit() {
    // Toggle standard elements
    document.getElementById('viewFullName').classList.toggle('hidden');
    document.getElementById('editNameContainer').classList.toggle('hidden');
    document.getElementById('adminSaveActions').classList.toggle('hidden');

    const viewData = document.querySelectorAll('#profileOverlay .view-data');
    const inputs = document.querySelectorAll('#profileOverlay .modal-input');

    viewData.forEach(el => {
        // LOCK GPA: Do not hide viewGpa text so it stays visible while editing
        if (el.id !== 'viewGpa' && el.id !== 'viewFullName') {
            el.classList.toggle('hidden');
        }
    });

    inputs.forEach(el => {
        // LOCK GPA: Never show the GPA input field
        if (el.id !== 'editGpa') {
            el.classList.toggle('hidden');
        }
    });
}

function resetAdminModalState() {
    const actions = document.getElementById('adminSaveActions');
    const nameHeading = document.getElementById('viewFullName');
    const nameInputs = document.getElementById('editNameContainer');

    if (actions)
        actions.classList.add('hidden');
    if (nameHeading)
        nameHeading.classList.remove('hidden');
    if (nameInputs)
        nameInputs.classList.add('hidden');

    document.querySelectorAll('#profileOverlay .view-data').forEach(el => el.classList.remove('hidden'));
    document.querySelectorAll('#profileOverlay .modal-input').forEach(el => el.classList.add('hidden'));
}

// --- AJAX Course Logic ---

function loadEnrolledCourses(studentId) {
    const tbody = document.getElementById('viewCourseList');
    tbody.innerHTML = '<tr><td colspan="4" style="text-align:center;">Loading...</td></tr>';

    fetch(`${window.contextPath}/AdminGetStudentCoursesServlet?studentId=${studentId}`)
            .then(res => res.text())
            .then(html => {
                tbody.innerHTML = html;
            })
            .catch(err => {
                console.error("Connection Error:", err);
                tbody.innerHTML = '<tr><td colspan="4" style="text-align:center; color:red;">Connection Error.</td></tr>';
            });
}

function enrollStudentAction() {
    const studentId = document.getElementById('editStudentId').value;
    const courseId = document.getElementById('enrollCourseSelect').value;

    if (!courseId)
        return;

    fetch(`${window.contextPath}/AdminGetStudentCoursesServlet`, {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: `action=ENROLL&studentId=${studentId}&courseId=${courseId}`
    })
            .then(response => response.text())
            .then(data => {
                const trimmedData = data.trim();
                if (trimmedData === "prereq_missing") {
                    document.getElementById('prereqErrorModal').style.display = 'flex';
                } else if (trimmedData === "success") {
                    showToast("Student Enrolled Successfully!");
                    loadEnrolledCourses(studentId);
                } else {
                    showToast("Error: Enrollment failed.", true);
                }
            });
}

function dropCourseAction(courseId) {
    pendingDropCourseId = courseId;
    const overlay = document.getElementById('unenrollConfirmOverlay');
    if (overlay)
        overlay.style.display = 'flex';

    const confirmBtn = document.getElementById('confirmUnenrollBtn');
    if (confirmBtn) {
        confirmBtn.onclick = function () {
            executeUnenroll();
        };
    }
}

function executeUnenroll() {
    const params = new URLSearchParams();
    params.append('action', 'DROP');
    params.append('studentId', currentViewedStudentId);
    params.append('courseId', pendingDropCourseId);

    fetch(`${window.contextPath}/AdminGetStudentCoursesServlet`, {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: params
    })
            .then(res => res.text())
            .then(res => {
                if (res.trim() === "success") {
                    showToast("Course removed successfully!");
                    loadEnrolledCourses(currentViewedStudentId);
                    closeUnenrollModal();
                }
            });
}

// --- Utility Functions ---

window.onclick = function (event) {
    const overlays = [
        document.getElementById('deleteOverlay'),
        document.getElementById('addStudentOverlay'),
        document.getElementById('profileOverlay'),
        document.getElementById('unenrollConfirmOverlay')
    ];

    overlays.forEach(overlay => {
        if (event.target == overlay) {
            overlay.style.display = 'none';
            document.body.style.overflow = 'auto';
            if (overlay.id === 'profileOverlay')
                resetAdminModalState();
            if (overlay.id === 'unenrollConfirmOverlay')
                pendingDropCourseId = null;
        }
    });
};

function showToast(message) {
    const toast = document.getElementById('successToast');
    const msgSpan = document.getElementById('toastMessage');
    msgSpan.innerText = message;
    toast.classList.add('show');
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

function filterStudents() {
    const input = document.getElementById('studentSearch').value.toLowerCase();
    const tableRows = document.querySelectorAll('.admin-table tbody tr');

    tableRows.forEach(row => {
        const rowText = row.textContent.toLowerCase();
        row.style.display = rowText.includes(input) ? "" : "none";
    });
}

window.onload = function () {
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('error') === 'missing_prereq') {
        document.getElementById('prereqErrorModal').style.display = 'flex';
    }
};

function closePrereqModal() {
    document.getElementById('prereqErrorModal').style.display = 'none';
    const newUrl = window.location.pathname + window.location.search.replace(/[\?&]error=missing_prereq/, '').replace(/&studentId=\d+/, '');
    window.history.replaceState({}, document.title, newUrl);
}