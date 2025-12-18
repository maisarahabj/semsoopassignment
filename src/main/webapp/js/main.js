/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


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
