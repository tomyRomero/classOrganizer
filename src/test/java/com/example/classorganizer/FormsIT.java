package com.example.classorganizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.classorganizer.Browser.Page;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** The forms refuse bad input with a plain message on the same page, and keep what was typed intact. */
class FormsIT {

    private final Browser browser = new Browser();

    @Test
    void aBlankSignUpGetsAMessageNotACrash() throws Exception {
        String username = Browser.newUsername("blank");
        Map<String, String> form = Browser.signUpForm(username, "  ", "Lovelace");
        form.put("age", "");

        Page page = browser.post("process-signup", form);
        assertEquals(200, page.status());
        assertTrue(page.shows("Firstname is required."));
        assertTrue(page.shows("Age is required."));
        assertFalse(page.shows("Exception"));
        assertTrue(browser.logIn(username, Browser.PASSWORD).shows("Incorrect Username or Password"));
    }

    @Test
    void aWrongClassFieldGetsAMessageAndNothingIsSaved() throws Exception {
        browser.newStudent("Careful");
        Map<String, String> form = Browser.classForm("Bad Class", "5555", "25:00");
        form.put("class grade", "101");

        Page page = browser.addClass(form);
        assertTrue(page.shows("Some Fields Were Incorrect or Empty Please Try Again"));
        assertTrue(page.shows("Time must be a time of day, like 09:30."));
        assertTrue(page.shows("Grade must be between 0 and 100."));
        assertFalse(browser.get("table").shows("Bad Class"));
    }

    @Test
    void aTakenUsernameIsRefused() throws Exception {
        String username = Browser.newUsername("twice");
        browser.signUp(username, "Ada", "Lovelace");
        Page page = browser.signUp(username, "Someone", "Else");
        assertTrue(page.shows("That username is already taken, please pick another one."));
    }

    @Test
    void theSameCrnTwiceIsRefused() throws Exception {
        browser.newStudent("Repeat");
        browser.addClass(Browser.classForm("Intro to Java", "6060", "09:30"));
        Page page = browser.addClass(Browser.classForm("Intro to Java Again", "6060", "10:30"));
        assertTrue(page.shows("You already have a class with CRN 6060."));
    }

    @Test
    void aNameWithAnAccentComesBackAsTyped() throws Exception {
        String username = Browser.newUsername("jose");
        browser.signUp(username, "José", "Núñez");
        assertTrue(browser.logIn(username, Browser.PASSWORD).shows("Welcome User: José Núñez"));
    }
}
