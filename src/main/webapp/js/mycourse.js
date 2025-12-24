/**
 * mycourse.js
 * Logic for student course details popup and drop functionality
 */

/**
 * Opens the course detail modal and populates it with data
 */
function showCourseModal(code, name, credits, day, time, dept, enrolled, capacity) {
    const overlay = document.getElementById('courseDetailOverlay');
    
    document.getElementById('modalCourseName').innerText = name;
    document.getElementById('modalCourseCode').innerText = code;
    document.getElementById('modalDepartment').innerText = dept;
    document.getElementById('modalCredits').innerText = credits + " Credits";
    document.getElementById('modalDay').innerText = day;
    document.getElementById('modalTime').innerText = time;
    document.getElementById('modalCapacity').innerText = enrolled + " / " + capacity + " Students Enrolled";

    overlay.style.display = 'flex';
    document.body.style.overflow = 'hidden'; 
}

function closeCourseModal() {
    const overlay = document.getElementById('courseDetailOverlay');
    overlay.style.display = 'none';
    document.body.style.overflow = 'auto'; 
}

// Close modal when clicking outside the white box
window.onclick = function(event) {
    const overlay = document.getElementById('courseDetailOverlay');
    if (event.target == overlay) {
        closeCourseModal();
    }
};

/**
 * Toggles the 'Drop Mode' view
 * Reveals/Hides the red X column
 */
function toggleDropMode() {
    const dropBtn = document.getElementById('toggleDropBtn');
    const dropColumns = document.querySelectorAll('.drop-column');
    
    dropBtn.classList.toggle('active');
    
    dropColumns.forEach(col => {
        // Toggle display between 'none' and 'table-cell'
        if (window.getComputedStyle(col).display === 'none') {
            col.style.setProperty('display', 'table-cell', 'important');
        } else {
            col.style.setProperty('display', 'none', 'important');
        }
    });

    if (dropBtn.classList.contains('active')) {
        dropBtn.innerHTML = '<i class="fas fa-check-circle"></i> Done';
    } else {
        dropBtn.innerHTML = '<i class="fas fa-minus-circle"></i> Drop Course';
    }
}

/**
 * Triggered when clicking the red X icon
 */
function confirmDrop(courseId, courseCode) {
    const confirmed = confirm("Are you sure you want to drop " + courseCode + "? This will remove you from the course and free up your seat.");
    
    if (confirmed) {
        // IMPROVEMENT: Get the context path dynamically to ensure it hits the Servlet correctly
        const contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));
        window.location.href = contextPath + "/EnrollmentServlet?action=drop&courseId=" + courseId;
    }
}