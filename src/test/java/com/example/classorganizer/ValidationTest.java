package com.example.classorganizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class ValidationTest {

    @Test
    void aCompleteSignUpHasNoProblems() {
        assertEquals(List.of(),
                Validation.checkSignUp("Ada", "Lovelace", "ada01", "correct horse 1", "ada@example.com", "23"));
    }

    @Test
    void eachEmptySignUpFieldIsNamed() {
        List<String> problems = Validation.checkSignUp("", " ", "", "", "", "");
        assertEquals(List.of("Firstname is required.", "Lastname is required.", "Username is required.",
                "Password must be 8 to 100 characters.", "Email is required.", "Age is required."), problems);
    }

    @Test
    void overLongFieldsAreRefusedAndTheLimitItselfIsFine() {
        List<String> problems = Validation.checkSignUp("x".repeat(51), "Lovelace", "u".repeat(31), "p".repeat(101),
                "a".repeat(90) + "@example.com", "23");
        assertEquals(List.of("Firstname must be at most 50 characters.", "Username must be at most 30 characters.",
                "Password must be 8 to 100 characters.", "Email must be at most 100 characters."), problems);
        assertEquals(List.of(),
                Validation.checkSignUp("x".repeat(50), "L", "u".repeat(30), "p".repeat(8), "a@b.co", "1"));
    }

    @ParameterizedTest
    @CsvSource({"abc, Age must be a whole number.", "0, Age must be between 1 and 120.",
            "121, Age must be between 1 and 120."})
    void ageMustBeARealAge(String age, String problem) {
        assertEquals(List.of(problem),
                Validation.checkSignUp("Ada", "Lovelace", "ada01", "correct horse 1", "ada@example.com", age));
    }

    @Test
    void emailMustLookLikeAnEmail() {
        assertEquals(List.of("Email must look like name@example.com."),
                Validation.checkSignUp("Ada", "Lovelace", "ada01", "correct horse 1", "not-an-email", "23"));
    }

    @Test
    void aCompleteClassHasNoProblems() {
        assertEquals(List.of(),
                Validation.checkClass("Intro to Java", "Science", "09:30", "Dr.Fiction", "1234", "95", "4"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"25:00", "9.30", "noon"})
    void timeMustBeATimeOfDay(String time) {
        assertEquals(List.of("Time must be a time of day, like 09:30."),
                Validation.checkClass("Intro to Java", "Science", time, "Dr.Fiction", "1234", "95", "4"));
    }

    @Test
    void gradeCreditsAndSubjectMustBeInRange() {
        List<String> problems =
                Validation.checkClass("Intro to Java", "Astrology", "09:30", "Dr.Fiction", "0", "101", "0");
        assertEquals(List.of("ClassSubject must be one of the subjects in the list.",
                "CRN must be between 1 and 99999999.", "Grade must be between 0 and 100.",
                "Credits must be between 1 and 12."), problems);
    }

    @Test
    void removingNeedsANameAndANumber() {
        assertEquals(List.of("Classname is required.", "CRN must be a whole number."),
                Validation.checkRemoveClass("", "abc"));
        assertTrue(Validation.checkRemoveClass("Intro to Java", "1234").isEmpty());
    }
}
