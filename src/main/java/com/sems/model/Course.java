package com.sems.model;
/**
 *
 * @author maisarahabjalil
 */

public class Course {
    private int courseId;        
    private String courseCode;    
    private String courseName;     
    private int credits;         
    private String department;     
    private int capacity;         
    private int enrolledCount;    
    private boolean isActive;     
    // Added fields for schedule
    private String courseDay;
    private String courseTime;

    // Constructors
    public Course() {
        this.capacity = 30;        
        this.enrolledCount = 0;   
        this.isActive = true;     
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

    // Added Getters and Setters for schedule
    public String getCourseDay() { return courseDay; }
    public void setCourseDay(String courseDay) { this.courseDay = courseDay; }

    public String getCourseTime() { return courseTime; }
    public void setCourseTime(String courseTime) { this.courseTime = courseTime; }

    @Override
    public String toString() {
        return "Course [" + courseCode + " - " + courseName + "]";
    }
}