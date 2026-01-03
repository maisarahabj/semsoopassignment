/**
 * STUDENT & ADMIN VIEW: view / edit profile logic
 **/

document.addEventListener("DOMContentLoaded", function () {
    // 1. Auto-hide alerts (Success/Error) after 3 seconds
    const successAlert = document.getElementById("successAlert");
    const profileAlert = document.getElementById("profileAlert");

    const hideAlert = (el) => {
        if (el) {
            setTimeout(() => {
                el.style.transition = 'opacity 0.5s ease';
                el.style.opacity = "0";
                setTimeout(() => el.remove(), 500);
            }, 3000);
        }
    };

    hideAlert(successAlert);
    hideAlert(profileAlert);

    // 2. Client-side validation for the Profile Form
    const profileForm = document.querySelector(".profile-form");
    if (profileForm) {
        profileForm.addEventListener("submit", function (e) {
            // Use querySelector to safely check if elements exist before getting value
            const phoneInput = document.querySelector('input[name="phone"]');
            const passwordInput = document.querySelector('input[name="password"]');

            const phone = phoneInput ? phoneInput.value : "";
            const password = passwordInput ? passwordInput.value : "";

            // A. Password Constraint Check (Only if they typed a new password)
            if (password.length > 0) {
                const passRegex = /^(?=.*[A-Z])(?=.*\d).{8,}$/;
                if (!passRegex.test(password)) {
                    alert("⚠️ Password is too weak!\n\nRequirements:\n- Minimum 8 characters\n- At least one Uppercase letter\n- At least one Number");
                    e.preventDefault();
                    return;
                }
            }

            // B. Phone Number Validation (Ensure it's numeric/valid symbols)
            if (phone.trim().length > 0) {
                const cleanPhone = phone.replace(/\s/g, '').replace('+', '').replace('-', '');
                if (isNaN(cleanPhone)) {
                    alert("Please enter a valid phone number.");
                    e.preventDefault();
                }
            }
        });
    }
});

/**
 * Toggles between view-only mode and edit-mode for profile fields
 * Works for both Student and Admin views
 * @param {HTMLButtonElement} button - The edit button clicked
 */
function toggleFieldEdit(button) {
    // Find the container for this specific input group
    const container = button.closest('.editable-input-group');
    if (!container)
        return;

    const displayValue = container.querySelector('.display-value');
    const inputField = container.querySelector('.edit-field');
    const actions = document.getElementById('formActions');

    // Toggle the 'hidden' class defined in your CSS
    if (displayValue && inputField) {
        displayValue.classList.toggle('hidden');
        inputField.classList.toggle('hidden');

        // Change the icon from Edit (pencil) to Close (times/X)
        const icon = button.querySelector('i');
        if (inputField.classList.contains('hidden')) {
            icon.classList.replace('fa-times', 'fa-edit');
            // If it's a password field, reset it on cancel for security
            if (inputField.type === "password")
                inputField.value = "";
        } else {
            icon.classList.replace('fa-edit', 'fa-times');
            inputField.focus(); // Auto-focus for better UX
        }
    }

    // Check if any other fields are currently being edited
    // If at least one is open, show the "Save Changes" button container
    const anyFieldVisible = document.querySelectorAll('.edit-field:not(.hidden)').length > 0;
    if (actions) {
        actions.classList.toggle('hidden', !anyFieldVisible);
    }
}