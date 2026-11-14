package com.example.classorganizer;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * One class a student takes, as stored in userclasses. Named SchoolClass because "class" is a
 * Java keyword and myClass said nothing about what it holds.
 */
record SchoolClass(int userId, String name, String subject, LocalTime time, Set<DayOfWeek> days,
                   String professor, int crn, int grade, int credits) {

    /** The days a class can meet, in the order the schedule shows them. */
    static final List<DayOfWeek> SCHOOL_DAYS = List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY);

    SchoolClass {
        days = Set.copyOf(days); // our own copy, so nobody can change the days after the fact
    }

    boolean meetsOn(DayOfWeek day) {
        return days.contains(day);
    }

    /** "Monday" for MONDAY: the name of the day's checkbox on the form and of its database column. */
    static String label(DayOfWeek day) {
        return day.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }
}
