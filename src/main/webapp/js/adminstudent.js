function openAddModal() {
    document.getElementById('addStudentOverlay').style.display = 'flex';
}

function closeAddModal() {
    document.getElementById('addStudentOverlay').style.display = 'none';
}

function showDeleteModal(id, fName, lName) {
    document.getElementById('modalStudentId').value = id;
    document.getElementById('removeMessage').innerHTML = 
        "Are you sure you want to permanently remove student <strong>" + fName + " " + lName + "</strong> (ID: #" + id + ")?";
    document.getElementById('deleteOverlay').style.display = 'flex';
}

function closeDeleteModal() {
    document.getElementById('deleteOverlay').style.display = 'none';
}

window.onclick = function(event) {
    if (event.target == document.getElementById('deleteOverlay')) closeDeleteModal();
    if (event.target == document.getElementById('addStudentOverlay')) closeAddModal();
}

// START -- admin pop up for view full profile 
function showProfileModal(id, fName, lName, email, phone, address, dob, gpa) {
    document.getElementById('viewFullName').innerText = fName + " " + lName;
    document.getElementById('viewStudentId').innerText = "Student ID: #" + id;
    document.getElementById('viewEmail').innerText = email;
    document.getElementById('viewPhone').innerText = (phone !== "null" && phone !== "") ? phone : "Not Provided";
    document.getElementById('viewDob').innerText = dob;
    document.getElementById('viewGpa').innerText = parseFloat(gpa).toFixed(2);
    document.getElementById('viewAddress').innerText = (address !== "null" && address !== "") ? address : "No address on file";

    document.getElementById('profileOverlay').style.display = 'flex';
}

function closeProfileModal() {
    document.getElementById('profileOverlay').style.display = 'none';
}

// Update the window click listener to handle the 3rd modal
window.onclick = function(event) {
    if (event.target == document.getElementById('deleteOverlay')) closeDeleteModal();
    if (event.target == document.getElementById('addStudentOverlay')) closeAddModal();
    if (event.target == document.getElementById('profileOverlay')) closeProfileModal();
}
// END -- admin pop up for view all profile