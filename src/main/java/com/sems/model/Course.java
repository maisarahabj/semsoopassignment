package com.sems.model;
/**
 *
 * @author maisarahabjalil
 */

public class Course {
    private int courseId;          // course_id
    private String courseCode;     // course_code
    private String courseName;     // course_name
    private int credits;           // credits
    private String department;     // department
    private int capacity;          // capacity
    private int enrolledCount;     // enrolled_count
    private boolean isActive;      // is_active

    // Constructors
    public Course() {
        this.capacity = 30;        // Matches SQL DEFAULT 30
        this.enrolledCount = 0;    // Matches SQL DEFAULT 0
        this.isActive = true;      // Matches SQL DEFAULT TRUE
    }

    public Course(String courseCode, String courseName, int credits, String department) {
        this();
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.department = department;
    }

    // Getters and Setters
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getEnrolledCount() { return enrolledCount; }
    public void setEnrolledCount(int enrolledCount) { this.enrolledCount = enrolledCount; }

    public boolean isIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }

   
    @Override
    public String toString() {
        return "Course [" + courseCode + " - " + courseName + "]";
    }
}
