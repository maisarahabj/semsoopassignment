# Database Quick Reference - Semester Features

## Tables Overview

### 1. semesters
Stores academic semester information.

```sql
CREATE TABLE semesters (
    semester_id INT PRIMARY KEY AUTO_INCREMENT,
    semester_name VARCHAR(100) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status ENUM('ACTIVE', 'ENDED') NOT NULL DEFAULT 'ACTIVE',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Sample Data:**
```sql
INSERT INTO semesters (semester_name, start_date, end_date, status) VALUES
('Fall 2024', '2024-08-15', '2024-12-15', 'ENDED'),
('Spring 2025', '2025-01-10', '2025-05-20', 'ACTIVE');
```

**Indexes:**
- PRIMARY KEY on `semester_id`
- UNIQUE KEY on `semester_name`

---

### 2. calendar_events
Stores academic calendar events associated with semesters.

```sql
CREATE TABLE calendar_events (
    event_id INT PRIMARY KEY AUTO_INCREMENT,
    semester_id INT NOT NULL,
    event_title VARCHAR(200) NOT NULL,
    event_type ENUM('EXAM', 'REGISTRATION', 'DEADLINE', 'HOLIDAY', 'OTHER') NOT NULL,
    event_date DATE NOT NULL,
    end_date DATE NULL,
    description TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (semester_id) REFERENCES semesters(semester_id) ON DELETE CASCADE,
    INDEX idx_event_date (event_date),
    INDEX idx_event_type (event_type)
);
```

**Sample Data:**
```sql
INSERT INTO calendar_events (semester_id, event_title, event_type, event_date, end_date, description) VALUES
(2, 'Course Registration Opens', 'REGISTRATION', '2025-01-05', '2025-01-12', 'Online registration period for Spring 2025'),
(2, 'Midterm Exams', 'EXAM', '2025-03-10', '2025-03-14', 'Midterm examination week'),
(2, 'Spring Break', 'HOLIDAY', '2025-03-17', '2025-03-21', 'No classes during this week'),
(2, 'Final Exam - CS101', 'EXAM', '2025-05-15', NULL, 'Computer Science 101 final examination');
```

**Indexes:**
- PRIMARY KEY on `event_id`
- INDEX on `event_date` (for date range queries)
- INDEX on `event_type` (for filtering by type)
- FOREIGN KEY on `semester_id` → `semesters.semester_id` with CASCADE DELETE

---

### 3. Modified Tables

#### courses (added semester_id)
```sql
ALTER TABLE courses 
ADD COLUMN semester_id INT,
ADD CONSTRAINT fk_course_semester 
    FOREIGN KEY (semester_id) 
    REFERENCES semesters(semester_id) 
    ON DELETE SET NULL;

CREATE INDEX idx_course_semester ON courses(semester_id);
```

#### enrollments (added semester_id)
```sql
ALTER TABLE enrollments 
ADD COLUMN semester_id INT,
ADD CONSTRAINT fk_enrollment_semester 
    FOREIGN KEY (semester_id) 
    REFERENCES semesters(semester_id) 
    ON DELETE CASCADE;

CREATE INDEX idx_enrollment_semester ON enrollments(semester_id);
```

#### evaluations (added semester_id)
```sql
ALTER TABLE evaluations 
ADD COLUMN semester_id INT,
ADD CONSTRAINT fk_evaluation_semester 
    FOREIGN KEY (semester_id) 
    REFERENCES semesters(semester_id) 
    ON DELETE CASCADE;

CREATE INDEX idx_evaluation_semester ON evaluations(semester_id);
```

---

## Common Queries

### Semester Management

**Get Active Semester:**
```sql
SELECT * FROM semesters 
WHERE status = 'ACTIVE' 
LIMIT 1;
```

**Get All Semesters (Ordered by Date):**
```sql
SELECT * FROM semesters 
ORDER BY created_date DESC;
```

**End a Semester:**
```sql
UPDATE semesters 
SET status = 'ENDED' 
WHERE semester_id = ?;
```

**Create New Semester (with auto-end previous):**
```sql
-- First, end current active semester
UPDATE semesters SET status = 'ENDED' WHERE status = 'ACTIVE';

-- Then create new semester
INSERT INTO semesters (semester_name, start_date, end_date, status) 
VALUES (?, ?, ?, 'ACTIVE');
```

---

### Course Migration

**Get All Courses from Source Semester:**
```sql
SELECT course_code, course_name, credits, prerequisite, instructor 
FROM courses 
WHERE semester_id = ?;
```

**Bulk Insert Courses to Target Semester:**
```sql
INSERT INTO courses (course_code, course_name, credits, prerequisite, instructor, semester_id, enrolled_students)
SELECT course_code, course_name, credits, prerequisite, instructor, ?, 0
FROM courses
WHERE semester_id = ?;
```

---

### GPA Calculation

**Calculate Semester GPA for Student:**
```sql
SELECT 
    SUM(
        CASE e.grade
            WHEN 'A' THEN 4.0 * c.credits
            WHEN 'B' THEN 3.0 * c.credits
            WHEN 'C' THEN 2.0 * c.credits
            WHEN 'D' THEN 1.0 * c.credits
            ELSE 0.0
        END
    ) / SUM(c.credits) AS semester_gpa
FROM enrollments e
JOIN courses c ON e.course_id = c.course_id
WHERE e.student_id = ? 
  AND e.semester_id = ?
  AND e.grade IS NOT NULL;
```

**Get All Semester GPAs for a Student:**
```sql
SELECT 
    s.semester_id,
    s.semester_name,
    SUM(
        CASE e.grade
            WHEN 'A' THEN 4.0 * c.credits
            WHEN 'B' THEN 3.0 * c.credits
            WHEN 'C' THEN 2.0 * c.credits
            WHEN 'D' THEN 1.0 * c.credits
            ELSE 0.0
        END
    ) / SUM(c.credits) AS semester_gpa
FROM enrollments e
JOIN courses c ON e.course_id = c.course_id
JOIN semesters s ON e.semester_id = s.semester_id
WHERE e.student_id = ?
  AND e.grade IS NOT NULL
GROUP BY s.semester_id, s.semester_name
ORDER BY s.created_date DESC;
```

---

### Academic Calendar

**Get All Events for a Semester:**
```sql
SELECT 
    ce.*,
    s.semester_name
FROM calendar_events ce
JOIN semesters s ON ce.semester_id = s.semester_id
WHERE ce.semester_id = ?
ORDER BY ce.event_date ASC;
```

**Get Upcoming Events (Next 30 Days):**
```sql
SELECT 
    ce.*,
    s.semester_name
FROM calendar_events ce
JOIN semesters s ON ce.semester_id = s.semester_id
WHERE ce.event_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY)
ORDER BY ce.event_date ASC;
```

**Get All Events (Across All Semesters):**
```sql
SELECT 
    ce.*,
    s.semester_name
FROM calendar_events ce
JOIN semesters s ON ce.semester_id = s.semester_id
ORDER BY ce.event_date ASC;
```

**Create Event:**
```sql
INSERT INTO calendar_events 
    (semester_id, event_title, event_type, event_date, end_date, description) 
VALUES (?, ?, ?, ?, ?, ?);
```

**Delete Event:**
```sql
DELETE FROM calendar_events 
WHERE event_id = ?;
```

---

### Student Semester Results

**Get Student Enrollments for Specific Semester:**
```sql
SELECT 
    e.enrollment_id,
    c.course_code,
    c.course_name,
    c.credits,
    c.instructor,
    e.grade,
    e.enrollment_date
FROM enrollments e
JOIN courses c ON e.course_id = c.course_id
WHERE e.student_id = ? 
  AND e.semester_id = ?
ORDER BY c.course_code;
```

**Get Student's All Semesters with Enrollments:**
```sql
SELECT DISTINCT 
    s.semester_id,
    s.semester_name,
    s.start_date,
    s.end_date,
    s.status
FROM semesters s
JOIN enrollments e ON s.semester_id = e.semester_id
WHERE e.student_id = ?
ORDER BY s.created_date DESC;
```

---

## Data Integrity Rules

### Foreign Key Cascade Rules

1. **courses.semester_id → semesters.semester_id**
   - ON DELETE: SET NULL
   - Rationale: If semester is deleted, courses remain but become unassociated

2. **enrollments.semester_id → semesters.semester_id**
   - ON DELETE: CASCADE
   - Rationale: If semester is deleted, enrollments for that semester are removed

3. **evaluations.semester_id → semesters.semester_id**
   - ON DELETE: CASCADE
   - Rationale: If semester is deleted, evaluations for that semester are removed

4. **calendar_events.semester_id → semesters.semester_id**
   - ON DELETE: CASCADE
   - Rationale: If semester is deleted, its calendar events are removed

### Constraints

1. **Only One Active Semester**
   - Enforced at application level
   - When creating new semester, previous active semester is automatically ended

2. **Unique Semester Names**
   - Database constraint: UNIQUE on `semester_name`
   - Prevents duplicate semester entries

3. **Event Types**
   - ENUM constraint ensures only valid event types
   - Valid types: EXAM, REGISTRATION, DEADLINE, HOLIDAY, OTHER

4. **Semester Status**
   - ENUM constraint ensures only valid statuses
   - Valid statuses: ACTIVE, ENDED

---

## Performance Optimization

### Indexes Created

1. **semesters**
   - PRIMARY KEY on `semester_id` (clustered)
   - UNIQUE KEY on `semester_name`

2. **calendar_events**
   - PRIMARY KEY on `event_id` (clustered)
   - INDEX on `event_date` (for date range queries)
   - INDEX on `event_type` (for type filtering)

3. **courses**
   - INDEX on `semester_id` (for semester filtering)

4. **enrollments**
   - INDEX on `semester_id` (for semester filtering)

5. **evaluations**
   - INDEX on `semester_id` (for semester filtering)

### Query Optimization Tips

1. **Always filter by semester_id** when querying courses, enrollments, or evaluations
2. **Use date ranges** for calendar event queries instead of scanning all events
3. **Leverage indexes** by including indexed columns in WHERE clauses
4. **Batch operations** for bulk inserts (e.g., course migration)
5. **Use transactions** for operations that modify multiple tables

---

## Backup Recommendations

### Critical Data
- **semesters**: Contains academic timeline
- **calendar_events**: Important dates and deadlines
- **enrollments with semester_id**: Historical academic records
- **evaluations with semester_id**: Course feedback by semester

### Backup Strategy
```sql
-- Backup semesters
SELECT * INTO OUTFILE '/backup/semesters_backup.csv'
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\n'
FROM semesters;

-- Backup calendar events
SELECT * INTO OUTFILE '/backup/calendar_events_backup.csv'
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\n'
FROM calendar_events;
```

---

## Maintenance Queries

**Check Orphaned Courses (no semester):**
```sql
SELECT * FROM courses 
WHERE semester_id IS NULL;
```

**Check Orphaned Enrollments (no semester):**
```sql
SELECT * FROM enrollments 
WHERE semester_id IS NULL;
```

**Update Orphaned Records to Active Semester:**
```sql
-- Get active semester ID
SET @active_semester = (SELECT semester_id FROM semesters WHERE status = 'ACTIVE' LIMIT 1);

-- Update courses
UPDATE courses 
SET semester_id = @active_semester 
WHERE semester_id IS NULL;

-- Update enrollments
UPDATE enrollments 
SET semester_id = @active_semester 
WHERE semester_id IS NULL;
```

**Archive Old Semesters (older than 2 years):**
```sql
-- Create archive table
CREATE TABLE semesters_archive LIKE semesters;

-- Move old semesters
INSERT INTO semesters_archive 
SELECT * FROM semesters 
WHERE end_date < DATE_SUB(CURDATE(), INTERVAL 2 YEAR);

-- Note: Do NOT delete from semesters if referenced by other tables
```

---
