package com.example.classorganizer;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Server-side checks for every form. The browser's "required" only helps honest users; anyone
 * can send a request without it, so these are the real rules. Each returns the problems found,
 * in words a student understands, and an empty list when the form is fine.
 */
final class Validation {

    static final int NAME_MAX = 50;
    static final int USERNAME_MAX = 30;
    static final int EMAIL_MAX = 100;
    static final int PASSWORD_MIN = 8;
    static final int PASSWORD_MAX = 100;
    /** The values of the ClassSubject list on AddClass.jsp. */
    static final List<String> SUBJECTS =
            List.of("Math", "Humanities", "SocialScience", "PhysicalEducation", "Science");

    private Validation() {
    }

    static List<String> checkSignUp(String firstName, String lastName, String username, String password,
                                    String email, String age) {
        List<String> problems = new ArrayList<>();
        checkText(problems, "Firstname", firstName, NAME_MAX);
        checkText(problems, "Lastname", lastName, NAME_MAX);
        checkText(problems, "Username", username, USERNAME_MAX);
        if (password.length() < PASSWORD_MIN || password.length() > PASSWORD_MAX) {
            problems.add("Password must be " + PASSWORD_MIN + " to " + PASSWORD_MAX + " characters.");
        }
        if (checkText(problems, "Email", email, EMAIL_MAX) && !email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            problems.add("Email must look like name@example.com.");
        }
        checkNumber(problems, "Age", age, 1, 120);
        return problems;
    }

    static List<String> checkClass(String className, String subject, String time, String professor,
                                   String crn, String grade, String credits) {
        List<String> problems = new ArrayList<>();
        checkText(problems, "Classname", className, NAME_MAX);
        if (!SUBJECTS.contains(subject)) {
            problems.add("ClassSubject must be one of the subjects in the list.");
        }
        if (time.isBlank()) {
            problems.add("Time is required.");
        } else if (parseTime(time).isEmpty()) {
            problems.add("Time must be a time of day, like 09:30.");
        }
        checkText(problems, "Professor", professor, NAME_MAX);
        checkNumber(problems, "CRN", crn, 1, 99_999_999);
        checkNumber(problems, "Grade", grade, 0, 100);
        checkNumber(problems, "Credits", credits, 1, 12);
        return problems;
    }

    static List<String> checkRemoveClass(String className, String crn) {
        List<String> problems = new ArrayList<>();
        checkText(problems, "Classname", className, NAME_MAX);
        checkNumber(problems, "CRN", crn, 1, 99_999_999);
        return problems;
    }

    /** "09:30" as a time of day, or empty when it is not one. The form's time field sends HH:mm. */
    static Optional<LocalTime> parseTime(String text) {
        try {
            return Optional.of(LocalTime.parse(text));
        } catch (DateTimeParseException notATime) {
            return Optional.empty();
        }
    }

    /** Adds a problem and returns false when the text is empty or too long. */
    private static boolean checkText(List<String> problems, String label, String value, int max) {
        if (value.isBlank()) {
            problems.add(label + " is required.");
            return false;
        }
        if (value.strip().length() > max) {
            problems.add(label + " must be at most " + max + " characters.");
            return false;
        }
        return true;
    }

    private static void checkNumber(List<String> problems, String label, String value, int min, int max) {
        if (value.isBlank()) {
            problems.add(label + " is required.");
            return;
        }
        try {
            int number = Integer.parseInt(value.strip());
            if (number < min || number > max) {
                problems.add(label + " must be between " + min + " and " + max + ".");
            }
        } catch (NumberFormatException notANumber) {
            problems.add(label + " must be a whole number.");
        }
    }
}
