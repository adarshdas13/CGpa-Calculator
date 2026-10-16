package com.example.cgpa_calculator;

import java.util.ArrayList;
import java.util.List;

public class Semester {
    private long id;
    private String semesterName;
    private double gpa;
    private double totalCredits;
    private long timestamp;
    private List<Subject> subjectList;

    public Semester() {
        this.subjectList = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
    }

    public Semester(String semesterName, double gpa, double totalCredits, List<Subject> subjectList) {
        this.semesterName = semesterName;
        this.gpa = gpa;
        this.totalCredits = totalCredits;
        this.subjectList = subjectList != null ? subjectList : new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
    }

    public Semester(long id, String semesterName, double gpa, double totalCredits, long timestamp, List<Subject> subjectList) {
        this.id = id;
        this.semesterName = semesterName;
        this.gpa = gpa;
        this.totalCredits = totalCredits;
        this.timestamp = timestamp;
        this.subjectList = subjectList != null ? subjectList : new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public double getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(double totalCredits) {
        this.totalCredits = totalCredits;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<Subject> getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(List<Subject> subjectList) {
        this.subjectList = subjectList;
    }
}