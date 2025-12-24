/**
 * adminstudent.js
 * Comprehensive logic for Student Directory Management
 */

// --- Global State ---
let currentViewedStudentId = null; // Tracks the student currently being viewed in the modal
let pendingDropCourseId = null;    // Tracks the course ID marked for unenrollment

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

function closeUnenrollModal() {
    document.getElementById('unenrollConfirmOverlay').style.display = 'none';
    pendingDropCourseId = null;
}

// --- Student Deletion Logic (Full Account Removal) ---

function showDeleteModal(studentId, userId, firstName, lastName) {
    // Fill the hidden inputs that the Main Servlet will read
    document.getElementById('modalStudentId').value = studentId;
    document.getElementById('modalUserId').value = userId;

    // Update the confirmation text
    document.getElementById('removeMessage').innerHTML = 
        `Are you sure you want to permanently remove <strong>${firstName} ${lastName}</strong> and their login account?`;

    document.getElementById('deleteOverlay').style.display = 'flex';
}

// --- Profile View & Course Management Logic ---

/**
 * Opens the profile modal and populates student details + enrolled courses
 */
function showProfileModal(id, fName, lName, email, phone, address, dob, gpa) {
    currentViewedStudentId = id; // Store ID for enrollment/drop actions

    // Populate Basic Info
    document.getElementById('viewFullName').innerText = fName + " " + lName;
    
    const idElement = document.getElementById('viewStudentIdDisplay') || document.getElementById('viewStudentId');
    if (idElement) idElement.innerText = "Student ID: #" + id;

    document.getElementById('viewEmail').innerText = email;
    document.getElementById('viewPhone').innerText = (phone && phone !== "null") ? phone : "Not Provided";
    document.getElementById('viewDob').innerText = dob;
    document.getElementById('viewGpa').innerText = parseFloat(gpa).toFixed(2);
    document.getElementById('viewAddress').innerText = (address && address !== "null") ? address : "No address on file";

    // Fetch live course data from the server
    loadEnrolledCourses(id);

    document.getElementById('profileOverlay').style.display = 'flex';
}

/**
 * Fetches course list via AJAX and handles the "null null" schedule display
 */
function loadEnrolledCourses(studentId) {
    const tbody = document.getElementById('viewCourseList');
    tbody.innerHTML = '<tr><td colspan="4" style="text-align:center;">Loading...</td></tr>';

    fetch(`${window.contextPath}/AdminGetStudentCoursesServlet?studentId=${studentId}`)
        .then(response => response.json())
        .then(data => {
            tbody.innerHTML = '';
            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="4" style="text-align:center;">No courses found.</td></tr>';
            } else {
                data.forEach(c => {
                    // Logic to prevent "null null" display
                    const scheduleDisplay = (c.day && c.time && c.day !== 'null') ? `${c.day} ${c.time}` : "TBA";
                    
                    tbody.innerHTML += `
                        <tr>
                            <td><span class="modal-code-badge">${c.code}</span></td>
                            <td><strong>${c.name}</strong></td>
                            <td style="color: #64748b;">${scheduleDisplay}</td>
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

/**
 * Action function to Enroll a student in a new course
 */
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
            loadEnrolledCourses(currentViewedStudentId); // Refresh table
            document.getElementById('enrollCourseSelect').value = ""; // Reset dropdown
        } else {
            alert("Failed to enroll. Check if course is full or student is already enrolled.");
        }
    });
}

/**
 * Opens the pretty custom unenrollment overlay
 */
function dropCourseAction(courseId) {
    pendingDropCourseId = courseId;
    
    const overlay = document.getElementById('unenrollConfirmOverlay');
    if (overlay) {
        overlay.style.display = 'flex';
    }

    // Link the "Yes" button in the overlay to the execution function
    const confirmBtn = document.getElementById('confirmUnenrollBtn');
    if (confirmBtn) {
        confirmBtn.onclick = function() {
            executeUnenroll();
        };
    }
}

/**
 * Sends the DROP request to the server after user confirms in the overlay
 */
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
            loadEnrolledCourses(currentViewedStudentId); // Refresh table
            closeUnenrollModal(); // Hide overlay
        } else {
            alert("Error: Could not drop the course.");
        }
    });
}

// --- Global Click Listener (Closes modals when clicking outside the box) ---
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
            // Specific cleanup if unenroll modal is closed
            if (overlay.id === 'unenrollConfirmOverlay') pendingDropCourseId = null;
        }
    });
};