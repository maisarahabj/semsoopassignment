/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

// --------- START: STUDENT ID WHEN STUDENT IS SELECTED -------------
window.onload = function() {
    const roleSelect = document.getElementById('role');
    const studentIdGroup = document.getElementById('studentIdGroup');

    if (roleSelect && studentIdGroup) {
        function toggleStudentId() {
            const val = roleSelect.value.toLowerCase();
            
            if (val === 'student') {
                // Instead of .style.display, we toggle a class
                studentIdGroup.classList.add('show-group');
            } else {
                studentIdGroup.classList.remove('show-group');
            }
        }

        toggleStudentId();
        roleSelect.addEventListener('change', toggleStudentId);
    }
};

// --------- END: STUDENT ID WHEN STUDENT IS SELECTED -------------

document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector(".login-form");

    if (form) {
        form.addEventListener("submit", (e) => {
            const inputs = form.querySelectorAll("input");

            for (let input of inputs) {
                if (input.value.trim() === "") {
                    alert("Please fill in all fields");
                    e.preventDefault();
                    return;
                }
            }
        });
    }
});
