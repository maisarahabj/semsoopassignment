function showDeleteModal(id, fName, lName) {
    document.getElementById('modalStudentId').value = id;
    document.getElementById('deleteMessage').innerHTML = 
        "Are you sure you want to permanently remove student <strong>" + fName + " " + lName + "</strong> (ID: #" + id + ") from the system?";
    document.getElementById('deleteOverlay').style.display = 'flex';
}

function closeDeleteModal() {
    document.getElementById('deleteOverlay').style.display = 'none';
}

window.onclick = function(event) {
    if (event.target == document.getElementById('deleteOverlay')) {
        closeDeleteModal();
    }
}