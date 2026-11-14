package com.example.classorganizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.classorganizer.Browser.Page;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** The whole app walked once the way a student uses it, and what they see when the database fails. */
class FlowIT {

    private final Browser browser = new Browser();

    @Test
    void aStudentWalksThroughTheWholeApp() throws Exception {
        String username = Browser.newUsername("katherine");
        assertTrue(browser.signUp(username, "Katherine", "Johnson").shows("Account Created: Proceed to Login"));
        assertTrue(browser.logIn(username, Browser.PASSWORD).shows("Welcome User: Katherine Johnson"));

        assertTrue(browser.addClass(Browser.classForm("Intro to Java", "1234", "09:30")).shows("Class Added"));
        Map<String, String> calculus = Browser.classForm("Calculus", "2345", "08:00");
        calculus.put("class grade", "85");
        calculus.put("class credit", "2");
        browser.addClass(calculus);

        // Earliest class first; an A over 4 credits and a B over 2 make (16 + 6) / 6 = 3.67.
        Page schedule = browser.get("table");
        assertTrue(schedule.body().indexOf("Calculus") < schedule.body().indexOf("Intro to Java"));
        assertTrue(schedule.shows("My GPA: 3.67"));

        assertTrue(browser.removeClass("Intro to Java", "1234").shows("Class Removed Successfully"));
        assertFalse(browser.get("table").shows("Intro to Java"));

        Page account = browser.get("account");
        assertTrue(account.shows("Username: " + username));
        assertTrue(account.shows("Number-of-Classes: 1"));
        assertTrue(account.shows("TotalCredits: 2"));

        assertTrue(browser.get("logout").shows("Register"));
        assertEquals(401, browser.get("Landing.jsp").status());
    }

    @Test
    void aDatabaseFailureShowsAPlainLineNotAStackTrace() throws Exception {
        browser.newStudent("Unlucky");
        try (Connection database = DriverManager.getConnection(System.getProperty("it.databaseUrl"));
             Statement sql = database.createStatement()) {
            // Hide the classes table for a moment, so the schedule page's query fails.
            sql.execute("ALTER TABLE userclasses RENAME TO userclasses_hidden");
            try {
                Page page = browser.get("table");
                assertEquals(500, page.status());
                assertTrue(page.shows(Pages.SERVER_PROBLEM));
                assertFalse(page.shows("Exception"));
                assertFalse(page.shows("userclasses"));
            } finally {
                sql.execute("ALTER TABLE userclasses_hidden RENAME TO userclasses");
            }
        }
    }
}
