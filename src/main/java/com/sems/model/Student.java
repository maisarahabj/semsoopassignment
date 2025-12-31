package com.sems.model;
import java.sql.Date;

/**
 *
 * @author maisarahabjalil
 */
public class Student {
    private int studentId;      
    private int userId;        
    private String firstName;   
    private String lastName;   
    private String email;      
    private String phone;    
    private String address;     
    private Date dob;         
    private Date enrollmentDate;
    private String status;      
    private double gpa;         
    private String profilePicture; // Path to profile picture         

    // Constructors
    public Student() {
        this.status = "active";
        this.gpa = 0.00;       
    }

    public Student(int userId, String firstName, String lastName, String email, Date dob) {
        this();
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dob = dob;
    }

    //Getters and Setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Date getDob() { return dob; }
    public void setDob(Date dob) { this.dob = dob; }

    public Date getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(Date enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }
    
    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    // Helper Method
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "Student [ID=" + studentId + ", Name=" + getFullName() + ", GPA=" + gpa + "]";
    }
}