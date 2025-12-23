function showDeleteModal(id, code, name) {
    // 1. Set the hidden input value
    document.getElementById('modalCourseId').value = id;
    
    // 2. Set the custom message with Course Code and Name
    document.getElementById('deleteMessage').innerHTML = 
        "Confirm deletion of <strong>" + code + " " + name + "</strong> permanently?";
    
    // 3. Show the overlay
    document.getElementById('deleteOverlay').style.display = 'flex';
}

function closeDeleteModal() {
    document.getElementById('deleteOverlay').style.display = 'none';
}

// Close modal if user clicks outside the white box
window.onclick = function(event) {
    let overlay = document.getElementById('deleteOverlay');
    if (event.target == overlay) {
        closeDeleteModal();
    }
}
