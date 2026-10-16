package com.example.cgpa_calculator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GpaCalculator {

    private static final Map<String, Double> GRADE_SCALE = new LinkedHashMap<>();

    static {
        GRADE_SCALE.put("O (10.0)", 10.0);
        GRADE_SCALE.put("A+ (9.5)", 9.5);
        GRADE_SCALE.put("A (9.0)", 9.0);
        GRADE_SCALE.put("B+ (8.0)", 8.0);
        GRADE_SCALE.put("B (7.0)", 7.0);
        GRADE_SCALE.put("C (6.0)", 6.0);
        GRADE_SCALE.put("P (5.0)", 5.0);
        GRADE_SCALE.put("F (0.0)", 0.0);
    }

    public static Map<String, Double> getGradeScale() {
        return GRADE_SCALE;
    }

    public static String[] getGradeOptions() {
        return GRADE_SCALE.keySet().toArray(new String[0]);
    }

    public static double getGradePointForGrade(String gradeKey) {
        if (gradeKey == null) return 0.0;
        if (GRADE_SCALE.containsKey(gradeKey)) {
            Double pt = GRADE_SCALE.get(gradeKey);
            return pt != null ? pt : 0.0;
        }
        // Fallback checks for simple letters
        String clean = gradeKey.trim().toUpperCase();
        if (clean.startsWith("O")) return 10.0;
        if (clean.startsWith("A+")) return 9.5;
        if (clean.startsWith("A")) return 9.0;
        if (clean.startsWith("B+")) return 8.0;
        if (clean.startsWith("B")) return 7.0;
        if (clean.startsWith("C")) return 6.0;
        if (clean.startsWith("P")) return 5.0;
        return 0.0;
    }

    public static double calculateGpa(List<Subject> subjects) {
        if (subjects == null || subjects.isEmpty()) {
            return 0.0;
        }

        double totalGradePoints = 0.0;
        double totalCredits = 0.0;

        for (Subject subject : subjects) {
            double credits = subject.getCredits();
            double gradePoint = subject.getGradePoint();
            if (credits > 0) {
                totalGradePoints += (credits * gradePoint);
                totalCredits += credits;
            }
        }

        if (totalCredits == 0.0) {
            return 0.0;
        }

        return totalGradePoints / totalCredits;
    }

    public static double calculateTotalCredits(List<Subject> subjects) {
        if (subjects == null) return 0.0;
        double total = 0.0;
        for (Subject s : subjects) {
            total += s.getCredits();
        }
        return total;
    }

    public static double convertGpaToPercentage(double gpa) {
        // Standard formula: Percentage = GPA * 9.5
        return Math.min(100.0, Math.max(0.0, gpa * 9.5));
    }

    public static String getPerformanceRating(double gpa) {
        if (gpa >= 9.0) return "Outstanding Performance! 🌟";
        if (gpa >= 8.0) return "Excellent Work! 🎯";
        if (gpa >= 7.0) return "Very Good! 👍";
        if (gpa >= 6.0) return "Good Job! 😊";
        if (gpa >= 5.0) return "Average Performance! 💪";
        if (gpa >= 4.0) return "Passed. Keep Striving! 📚";
        return "Needs Improvement. Don't give up! 💡";
    }
}