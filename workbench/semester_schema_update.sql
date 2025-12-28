-- Database Schema Update
-- Note to Mais: Add to Your SQL Workbench

-- 1. Create semesters table
CREATE TABLE IF NOT EXISTS semesters (
    semester_id INT AUTO_INCREMENT PRIMARY KEY,
    semester_name VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status ENUM('ACTIVE', 'ENDED') DEFAULT 'ACTIVE',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_dates (start_date, end_date)
);

-- 2. Add semester_id to courses table
ALTER TABLE courses 
ADD COLUMN semester_id INT DEFAULT NULL,
ADD CONSTRAINT fk_course_semester 
    FOREIGN KEY (semester_id) REFERENCES semesters(semester_id) 
    ON DELETE SET NULL;

-- 3. Add semester_id to enrollments table
ALTER TABLE enrollments 
ADD COLUMN semester_id INT DEFAULT NULL,
ADD CONSTRAINT fk_enrollment_semester 
    FOREIGN KEY (semester_id) REFERENCES semesters(semester_id) 
    ON DELETE SET NULL;

-- 4. Add semester_id to evaluations table
ALTER TABLE evaluations 
ADD COLUMN semester_id INT DEFAULT NULL,
ADD CONSTRAINT fk_evaluation_semester 
    FOREIGN KEY (semester_id) REFERENCES semesters(semester_id) 
    ON DELETE SET NULL;

-- 5. Create indexes for better query performance
CREATE INDEX idx_course_semester ON courses(semester_id);
CREATE INDEX idx_enrollment_semester ON enrollments(semester_id);
CREATE INDEX idx_evaluation_semester ON evaluations(semester_id);

-- 6. Create calendar_events table for academic calendar
CREATE TABLE IF NOT EXISTS calendar_events (
    event_id INT AUTO_INCREMENT PRIMARY KEY,
    semester_id INT NOT NULL,
    event_title VARCHAR(200) NOT NULL,
    event_type ENUM('EXAM', 'REGISTRATION', 'DEADLINE', 'HOLIDAY', 'OTHER') NOT NULL,
    event_date DATE NOT NULL,
    end_date DATE DEFAULT NULL,
    description TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (semester_id) REFERENCES semesters(semester_id) ON DELETE CASCADE,
    INDEX idx_event_date (event_date),
    INDEX idx_event_type (event_type)
);


-- Notes:
-- - Only one semester should have 'ACTIVE' status at a time
-- - When admin starts a new semester, previous active semester should be ended
-- - Students can only register for courses in the active semester
-- - Past semesters remain accessible for viewing results and evaluations
