package es.ufv.dis.students2025.mab;


public class Student {
    private String name;
    private String enrollmentId;
    private String career;
    private double gpa;
    private String[] subjects;

    // Constructor
    public Student(String name, String enrollmentId, String career, double gpa, String[] subjects) {
        this.name = name;
        this.enrollmentId = enrollmentId;
        this.career = career;
        this.gpa = gpa;
        this.subjects = subjects;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public String[] getSubjects() {
        return subjects;
    }

    public void setSubjects(String[] subjects) {
        this.subjects = subjects;
    }
}

