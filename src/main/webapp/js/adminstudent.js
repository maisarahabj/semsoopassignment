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
function showProfileModal(id, fName, lName, email, phone, address, dob, gpa) {
    document.getElementById('viewFullName').innerText = fName + " " + lName;
    
    // Using 'viewStudentIdDisplay' to match the corrected JSP
    const idElement = document.getElementById('viewStudentIdDisplay') || document.getElementById('viewStudentId');
    if(idElement) idElement.innerText = "Student ID: #" + id;

    document.getElementById('viewEmail').innerText = email;
    document.getElementById('viewPhone').innerText = (phone && phone !== "null") ? phone : "Not Provided";
    document.getElementById('viewDob').innerText = dob;
    document.getElementById('viewGpa').innerText = parseFloat(gpa).toFixed(2);
    document.getElementById('viewAddress').innerText = (address && address !== "null") ? address : "No address on file";

    document.getElementById('profileOverlay').style.display = 'flex';
}

// --- SINGLE Window Click Listener (Handles all modals) ---
window.onclick = function(event) {
    const deleteModal = document.getElementById('deleteOverlay');
    const addModal = document.getElementById('addStudentOverlay');
    const profileModal = document.getElementById('profileOverlay');

    if (event.target == deleteModal) closeDeleteModal();
    if (event.target == addModal) closeAddModal();
    if (event.target == profileModal) closeProfileModal();
};