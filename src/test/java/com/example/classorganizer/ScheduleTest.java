package com.example.classorganizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ScheduleTest {

    static SchoolClass aClass(String name, String time, int grade, int credits) {
        return new SchoolClass(1, name, "Science", LocalTime.parse(time), Set.of(DayOfWeek.MONDAY), "Dr.Fiction",
                1234, grade, credits);
    }

    @ParameterizedTest
    @CsvSource({"100, A+", "97, A+", "96, A", "93, A", "92, A-", "90, A-", "89, B+", "83, B", "80, B-", "77, C+",
            "73, C", "70, C-", "67, D+", "65, D", "64, F", "0, F"})
    void letterGradesFollowTheScale(int grade, String letter) {
        assertEquals(letter, Schedule.letterGrade(grade));
    }

    @Test
    void gradesOutsideZeroToHundredAreRefused() {
        assertThrows(IllegalArgumentException.class, () -> Schedule.letterGrade(101));
        assertThrows(IllegalArgumentException.class, () -> Schedule.letterGrade(-1));
    }

    @Test
    void theGpaIsWeightedByCredits() {
        // An A (4.0) over 4 credits and a B (3.0) over 2: (16 + 6) / 6 = 3.666..., shown as 3.67.
        double gpa = Schedule.gpa(List.of(aClass("Intro to Java", "09:30", 95, 4), aClass("Calculus", "08:00", 85, 2)))
                .orElseThrow();
        assertEquals(3.67, gpa);
    }

    @Test
    void noClassesMeansNoGpa() {
        assertTrue(Schedule.gpa(List.of()).isEmpty());
    }

    @Test
    void classesAreListedEarliestFirstThenByName() {
        SchoolClass biology = aClass("Biology", "10:00", 90, 3);
        SchoolClass calculus = aClass("calculus", "08:00", 90, 3);
        SchoolClass art = aClass("Art", "08:00", 90, 3);
        List<SchoolClass> asSaved = List.of(biology, calculus, art);

        assertEquals(List.of(art, calculus, biology), Schedule.inWeekOrder(asSaved));
        assertEquals(List.of(biology, calculus, art), asSaved); // the original list is left alone
    }
}
