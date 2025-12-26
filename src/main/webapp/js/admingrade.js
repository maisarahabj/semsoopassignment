/**
 * Logic to toggle between viewing and editing
 * This runs when the "Edit Grades" button is clicked
 */
function toggleEditMode() {
    const table = document.getElementById('adminGradeTable');
    const form = document.getElementById('gradingForm');

    // Only toggle if the elements exist (meaning a student has been loaded)
    if (table && form) {
        table.classList.toggle('edit-mode');
        form.classList.toggle('edit-mode');
    }
}

/**
 * Live filter for the subjects already on screen 
 */
const searchInput = document.getElementById('gradeSearch');

// SAFETY CHECK: This is the ONLY place this listener should be.
if (searchInput) {
    searchInput.addEventListener('keyup', function () {
        const input = this.value.toLowerCase();
        const rows = document.querySelectorAll('#adminGradeTable tbody tr');

        // This filters the rows of the table CURRENTLY shown
        rows.forEach(row => {
            const text = row.textContent.toLowerCase();
            row.style.display = text.includes(input) ? "" : "none";
        });
    });
}