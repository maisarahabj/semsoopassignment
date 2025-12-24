/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 *
 * STUDENT VIEW: view / edit profile
 *
 **/


document.addEventListener("DOMContentLoaded", function() {
    const alert = document.getElementById("profileAlert");
    
    // 1. Auto-hide alert after 3 seconds
    if (alert) {
        setTimeout(() => {
            alert.style.opacity = "0";
            setTimeout(() => alert.remove(), 500); // Remove from DOM after fade
        }, 3000);
    }

    // 2. Simple Client-side validation
    const profileForm = document.querySelector(".profile-form");
    if (profileForm) {
        profileForm.addEventListener("submit", function(e) {
            const phone = document.getElementsByName("phone")[0].value;
            
            // Basic check: Ensure phone isn't just letters
            if (phone && isNaN(phone.replace(/\s/g, '').replace('+', ''))) {
                alert("Please enter a valid phone number.");
                e.preventDefault(); // Stop form from submitting
            }
        });
    }
});

/**
 * Toggles between view-only mode and edit-mode for profile fields
 * @param {HTMLButtonElement} button - The edit button clicked
 */
function toggleFieldEdit(button) {
    // Find the container for this specific input
    const container = button.closest('.editable-input-group');
    const displayValue = container.querySelector('.display-value');
    const inputField = container.querySelector('.edit-field');
    const actions = document.getElementById('formActions');

    // Toggle the 'hidden' class defined in your CSS
    displayValue.classList.toggle('hidden');
    inputField.classList.toggle('hidden');
    
    // Change the icon from the Edit pencil to the Close 'X'
    const icon = button.querySelector('i');
    if (inputField.classList.contains('hidden')) {
        icon.classList.replace('fa-times', 'fa-edit');
    } else {
        icon.classList.replace('fa-edit', 'fa-times');
        inputField.focus(); // Auto-focus the input for the user
    }

    // Check if any other fields are currently being edited
    // If at least one is open, show the "Save Changes" button
    const isAnyEditing = document.querySelectorAll('.edit-field:not(.hidden)').length > 0;
    actions.classList.toggle('hidden', !isAnyEditing);
}

// Logic to auto-hide the success alert after 3 seconds
document.addEventListener('DOMContentLoaded', function() {
    const alert = document.getElementById('successAlert');
    if(alert) {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(() => alert.style.display = 'none', 500);
        }, 3000);
    }
});