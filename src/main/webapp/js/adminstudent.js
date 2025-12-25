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
function showProfileModal(id, fName, lName, email, phone, address, dob, gpa, regOn) {
    currentViewedStudentId = id;

    // Populate Header Info (Viewing Mode)
    document.getElementById('viewFullName').innerText = fName + " " + lName;
    document.getElementById('viewStudentId').innerText = "Student ID: #" + id;

    // Populate Registered On Date
    const regElement = document.getElementById('viewRegisteredOn');
    if (regElement) {
        regElement.innerText = "Registered on: " + (regOn && regOn !== "null" ? regOn : "N/A");
    }

    // Populate Body Data (Viewing Mode)
    document.getElementById('viewEmail').innerText = email;
    document.getElementById('viewPhone').innerText = (phone && phone !== "null") ? phone : "Not Provided";
    document.getElementById('viewDob').innerText = dob;
    document.getElementById('viewGpa').innerText = parseFloat(gpa).toFixed(2);
    document.getElementById('viewAddress').innerText = (address && address !== "null") ? address : "No address on file";

    // --- PRE-FILL INPUT FIELDS (Editing Mode) ---
    document.getElementById('editStudentId').value = id;
    document.getElementById('editFName').value = fName;
    document.getElementById('editLName').value = lName;
    document.getElementById('editEmail').value = email;
    document.getElementById('editPhone').value = (phone && phone !== "null") ? phone : "";
    document.getElementById('editDob').value = dob;
    document.getElementById('editGpa').value = gpa;
    document.getElementById('editAddress').value = (address && address !== "null") ? address : "";

    // Reset UI to view-only mode
    resetAdminModalState();

    // Fetch live course data
    loadEnrolledCourses(id);

    document.getElementById('profileOverlay').style.display = 'flex';
    document.body.style.overflow = 'hidden'; // Lock background
}

/**
 * Swaps between the Header Title and the First/Last Name Inputs
 */
function toggleAdminEdit() {
    console.log("Pencil clicked! Toggling mode...");

    const nameHeading = document.getElementById('viewFullName');
    const nameInputs = document.getElementById('editNameContainer');
    const actions = document.getElementById('adminSaveActions');

    if (!nameHeading || !nameInputs) {
        console.error("Error: Could not find nameHeading or nameInputs IDs in the JSP!");
        return;
    }

    // Toggle Header
    nameHeading.classList.toggle('hidden');
    nameInputs.classList.toggle('hidden');

    // Toggle the rest of the form fields
    const viewData = document.querySelectorAll('#profileOverlay .view-data');
    const inputs = document.querySelectorAll('#profileOverlay .modal-input');

    viewData.forEach(el => {
        if (el.id !== 'viewFullName')
            el.classList.toggle('hidden');
    });

    inputs.forEach(el => el.classList.toggle('hidden'));

    if (actions)
        actions.classList.toggle('hidden');

    console.log("Toggle complete.");
}

/**
 * Returns modal to View Mode
 */
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
                    showToast("Student enrolled successfully!");
                    loadEnrolledCourses(currentViewedStudentId);
                    document.getElementById('enrollCourseSelect').value = "";
                } else {
                    alert("Failed to enroll.");
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
            document.body.style.overflow = 'auto'; // Unlock scroll
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

/**
 * Filter student directory table based on search input
 */
function filterStudents() {
    const input = document.getElementById('studentSearch').value.toLowerCase();
    const tableRows = document.querySelectorAll('.admin-table tbody tr');

    tableRows.forEach(row => {
        // We get the text content of the whole row (ID, Name, Email, etc.)
        const rowText = row.textContent.toLowerCase();

        if (rowText.includes(input)) {
            row.style.display = ""; // Show row
        } else {
            row.style.display = "none"; // Hide row
        }
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
    // Optional: Clean the URL so the popup doesn't keep appearing on refresh
    const newUrl = window.location.pathname + window.location.search.replace(/[\?&]error=missing_prereq/, '').replace(/&studentId=\d+/, '');
    window.history.replaceState({}, document.title, newUrl);
}