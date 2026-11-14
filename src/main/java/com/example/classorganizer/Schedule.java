package com.example.classorganizer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;

/** The rules behind the schedule page: letter grades, the GPA and the order classes are listed in. */
final class Schedule {

    private Schedule() {
    }

    static String letterGrade(int grade) {
        if (grade < 0 || grade > 100) {
            throw new IllegalArgumentException("A grade is 0 to 100, not " + grade);
        }
        if (grade >= 97) return "A+";
        if (grade >= 93) return "A";
        if (grade >= 90) return "A-";
        if (grade >= 87) return "B+";
        if (grade >= 83) return "B";
        if (grade >= 80) return "B-";
        if (grade >= 77) return "C+";
        if (grade >= 73) return "C";
        if (grade >= 70) return "C-";
        if (grade >= 67) return "D+";
        if (grade >= 65) return "D";
        return "F";
    }

    static double gradePoints(String letter) {
        return switch (letter) {
            case "A+", "A" -> 4.0;
            case "A-" -> 3.7;
            case "B+" -> 3.3;
            case "B" -> 3.0;
            case "B-" -> 2.7;
            case "C+" -> 2.3;
            case "C" -> 2.0;
            case "C-" -> 1.7;
            case "D+" -> 1.3;
            case "D" -> 1.0;
            default -> 0.0;
        };
    }

    /** The GPA weighted by credits and rounded to two places, or empty when there is nothing to average. */
    static OptionalDouble gpa(List<SchoolClass> classes) {
        double points = 0;
        int credits = 0;
        for (SchoolClass schoolClass : classes) {
            points += gradePoints(letterGrade(schoolClass.grade())) * schoolClass.credits();
            credits += schoolClass.credits();
        }
        if (credits == 0) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(BigDecimal.valueOf(points / credits).setScale(2, RoundingMode.HALF_UP).doubleValue());
    }

    /** Earliest class first, then by name, so the list reads like a day. Returns a new list. */
    static List<SchoolClass> inWeekOrder(List<SchoolClass> classes) {
        return classes.stream()
                .sorted(Comparator.comparing(SchoolClass::time)
                        .thenComparing(SchoolClass::name, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }
}
