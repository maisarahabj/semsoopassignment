package com.sems.model;
import java.sql.Timestamp;
/**
 *
 * @author maisarahabjalil
 */

public class Enrollment {
    private int enrollmentId;      // enrollment_id
    private int studentId;         // student_id (Foreign Key)
    private int courseId;          // course_id (Foreign Key)
    private String status;         // status (ENUM: enrolled, dropped, etc.)
    private Timestamp enrollmentDate; // enrollment_date

    // Constructors
    public Enrollment() {
        this.status = "enrolled";  // Matches SQL DEFAULT 'enrolled'
    }

    public Enrollment(int studentId, int courseId) {
        this();
        this.studentId = studentId;
        this.courseId = courseId;
    }

    // Getters and Setters
    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int enrollmentId) { this.enrollmentId = enrollmentId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(Timestamp enrollmentDate) { this.enrollmentDate = enrollmentDate; }


    @Override
    public String toString() {
        return "Enrollment [StudentID=" + studentId + ", CourseID=" + courseId + ", Status=" + status + "]";
    }
}