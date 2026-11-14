package com.example.classorganizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.classorganizer.Browser.Page;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Every flow of the app, walked through the running server the way a browser would. */
class FlowIT {

    private final Browser browser = new Browser();

    @Test
    void signUpCreatesAnAccount() throws Exception {
        String username = Browser.newUsername("signup");
        Page page = browser.signUp(username, "Ada", "Lovelace");
        assertEquals(200, page.status());
        assertTrue(page.shows("Account Created: Proceed to Login"));
        assertTrue(page.shows("Your username is: " + username));
    }

    @Test
    void logInOpensTheLandingPage() throws Exception {
        String username = Browser.newUsername("login");
        browser.signUp(username, "Ada", "Lovelace");
        Page page = browser.logIn(username, Browser.PASSWORD);
        assertEquals(200, page.status());
        assertTrue(page.shows("Welcome User: Ada Lovelace"));
        assertTrue(page.shows("View Classes"));
    }

    @Test
    void addClassSavesIt() throws Exception {
        browser.newStudent("Grace");
        Page page = browser.addClass(Browser.classForm("Intro to Java", "1234", "09:30"));
        assertTrue(page.shows("Class Added"));
        assertTrue(page.shows("For: Grace Student"));
        assertTrue(browser.get("table").shows("<td>Intro to Java</td>"));
    }

    @Test
    void theScheduleListsClassesEarliestFirstWithTheGpa() throws Exception {
        browser.newStudent("Alan");
        browser.addClass(Browser.classForm("Intro to Java", "1234", "09:30"));
        Map<String, String> calculus = Browser.classForm("Calculus", "2345", "08:00");
        calculus.put("class grade", "85");
        calculus.put("class credit", "2");
        browser.addClass(calculus);

        Page page = browser.get("table");
        assertTrue(page.shows("My GPA: 3.67"));
        assertTrue(page.body().indexOf("Calculus") < page.body().indexOf("Intro to Java"));
        assertTrue(page.shows("<td>08:00</td>"));
        assertTrue(page.shows("<td>&#10003;</td>"));
    }

    @Test
    void anEmptyScheduleSaysSo() throws Exception {
        browser.newStudent("Mary");
        assertTrue(browser.get("table").shows("Classes are empty, try adding one and coming back"));
    }

    @Test
    void removeClassTakesOnlyThatClassOffTheSchedule() throws Exception {
        browser.newStudent("Edsger");
        browser.addClass(Browser.classForm("Intro to Java", "1234", "09:30"));
        browser.addClass(Browser.classForm("Calculus", "2345", "08:00"));

        Page page = browser.removeClass("Intro to Java", "1234");
        assertTrue(page.shows("Class Removed Successfully"));
        assertTrue(page.shows("CRN: 1234"));
        Page schedule = browser.get("table");
        assertFalse(schedule.shows("Intro to Java"));
        assertTrue(schedule.shows("Calculus"));
    }

    @Test
    void accountInfoShowsTheStudentAndTheirTotals() throws Exception {
        String username = browser.newStudent("Katherine");
        browser.addClass(Browser.classForm("Intro to Java", "1234", "09:30"));
        browser.addClass(Browser.classForm("Calculus", "2345", "08:00"));

        Page page = browser.get("account");
        assertTrue(page.shows("FirstName: Katherine"));
        assertTrue(page.shows("LastName: Student"));
        assertTrue(page.shows("Email: " + username + "@example.com"));
        assertTrue(page.shows("Age: 23"));
        assertTrue(page.shows("Username: " + username));
        assertTrue(page.shows("Number-of-Classes: 2"));
        assertTrue(page.shows("TotalCredits: 8"));
    }

    @Test
    void logOutEndsTheSessionAndGoesHome() throws Exception {
        browser.newStudent("Barbara");
        Page home = browser.get("logout");
        assertTrue(home.shows("Class Organizer"));
        assertTrue(home.shows("Register"));

        Page after = browser.get("Landing.jsp");
        assertEquals(401, after.status());
        assertTrue(after.shows("Please login first"));
    }
}
