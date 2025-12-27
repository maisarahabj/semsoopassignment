function openEvalModal(id, name) {
    // Manually set the value of the hidden input
    const courseIdInput = document.getElementById('modalCourseId');
    const courseNameHeader = document.getElementById('modalCourseName');
    const modal = document.getElementById('evalModal');

    if (courseIdInput && courseNameHeader && modal) {
        courseIdInput.value = id;
        courseNameHeader.innerText = "Evaluate: " + name;
        modal.style.display = 'block';
    }
}

function closeEvalModal() {
    const modal = document.getElementById('evalModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// Close modal when clicking outside the modal content
window.onclick = function (event) {
    const modal = document.getElementById('evalModal');
    if (event.target === modal) {
        modal.style.display = "none";
    }
};