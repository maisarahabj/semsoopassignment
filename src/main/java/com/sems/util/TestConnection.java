package com.sems.util;

import com.sems.dao.CourseDAO;
import com.sems.model.Course;
import java.util.List;

public class TestConnection {
    public static void main(String[] args) {
        CourseDAO courseDAO = new CourseDAO();
        
        // John Doe's ID is 1 according to our previous SQL setup
        int testStudentId = 1; 
        
        System.out.println("======= TESTING ENROLLMENT FOR STUDENT ID: " + testStudentId + " =======");
        
        List<Course> myClasses = courseDAO.getCoursesByStudentId(testStudentId);
        
        if (myClasses.isEmpty()) {
            System.out.println("No courses found for this student. Check your SQL enrollments!");
        } else {
            for (Course c : myClasses) {
                System.out.println("-------------------------------------------");
                System.out.println("Code:     " + c.getCourseCode());
                System.out.println("Name:     " + c.getCourseName());
                System.out.println("Credits:  " + c.getCredits());
                System.out.println("Schedule: " + c.getCourseDay() + " at " + c.getCourseTime());
                System.out.println("Status:   " + (c.getEnrolledCount() >= c.getCapacity() ? "FULL" : "OPEN"));
                System.out.println("Capacity: " + c.getEnrolledCount() + "/" + c.getCapacity());
            }
        }
        System.out.println("=================================================");
    }
}