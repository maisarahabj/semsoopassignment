/* * main.js - Barfact University SEMS
 * Author: maisarahabjalil
 */

// --------- PASSWORD VALIDATION FUNCTION -------------
// Validates password meets requirements
function validatePassword(password) {
    const minLength = 8;
    const hasUpperCase = /[A-Z]/.test(password);
    const hasNumber = /[0-9]/.test(password);
    const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);
    
    if (password.length < minLength) {
        return "Password must be at least 8 characters long.";
    }
    if (!hasUpperCase) {
        return "Password must contain at least one uppercase letter.";
    }
    if (!hasNumber) {
        return "Password must contain at least one number.";
    }
    if (!hasSpecialChar) {
        return "Password must contain at least one special character (!@#$%^&*(),.?\":{}|<>).";
    }
    return null; // No error
}

// --------- 1. ROLE SELECTION LOGIC (Registration Page) -------------
// This handles showing/hiding the Student ID field based on the dropdown
window.onload = function() {
    const roleSelect = document.getElementById('role');
    const studentIdGroup = document.getElementById('studentIdGroup');

    if (roleSelect && studentIdGroup) {
        function toggleStudentId() {
            const val = roleSelect.value.toLowerCase();
            
            if (val === 'student') {
                // Toggles the class to show the input field
                studentIdGroup.classList.add('show-group');
            } else {
                studentIdGroup.classList.remove('show-group');
            }
        }

        // Initial check on load
        toggleStudentId();
        // Check every time the user changes the selection
        roleSelect.addEventListener('change', toggleStudentId);
    }
};

// --------- 2. PAGE LOAD LOGIC (Registration Success & Validation) -------------
document.addEventListener("DOMContentLoaded", () => {
    
    // --- A. REGISTRATION SUCCESS POPUP ---
    // Looks for ?registered=true in the URL (sent by RegisterServlet)
    const urlParams = new URLSearchParams(window.location.search);
    
    if (urlParams.has('registered')) {
        // Show the alert requested
        alert("Submitted! Please await approval from admin.");
        
        // This line cleans the URL (removes ?registered=true) so the 
        // popup doesn't appear again if the user refreshes the page manually.
        window.history.replaceState({}, document.title, window.location.pathname);
    }

    // --- B. FORM VALIDATION LOGIC ---
    // Handles empty field checks for any form with the "login-form" class
    const form = document.querySelector(".login-form");

    if (form) {
        form.addEventListener("submit", (e) => {
            const inputs = form.querySelectorAll("input");
            let isValid = true;

            for (let input of inputs) {
                // We skip hidden inputs (like the studentId in modals)
                if (input.type !== "hidden" && input.value.trim() === "") {
                    isValid = false;
                    break;
                }
            }

            if (!isValid) {
                alert("Please fill in all fields");
                e.preventDefault(); // Stop the form from submitting
            }
        });
    }
});