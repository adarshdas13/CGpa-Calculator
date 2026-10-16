package com.example.cgpa_calculator;

public class Subject {
    private long id;
    private long semesterId;
    private String subjectName;
    private double credits;
    private String grade;
    private double gradePoint;

    public Subject() {
        this.subjectName = "";
        this.credits = 3.0;
        this.grade = "A+ (9.0)";
        this.gradePoint = 9.0;
    }

    public Subject(String subjectName, double credits, String grade, double gradePoint) {
        this.subjectName = subjectName;
        this.credits = credits;
        this.grade = grade;
        this.gradePoint = gradePoint;
    }

    public Subject(long id, long semesterId, String subjectName, double credits, String grade, double gradePoint) {
        this.id = id;
        this.semesterId = semesterId;
        this.subjectName = subjectName;
        this.credits = credits;
        this.grade = grade;
        this.gradePoint = gradePoint;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(long semesterId) {
        this.semesterId = semesterId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public double getCredits() {
        return credits;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
        this.gradePoint = GpaCalculator.getGradePointForGrade(grade);
    }

    public double getGradePoint() {
        return gradePoint;
    }

    public void setGradePoint(double gradePoint) {
        this.gradePoint = gradePoint;
    }
}