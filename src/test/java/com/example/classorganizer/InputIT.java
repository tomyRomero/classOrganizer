package com.example.classorganizer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.classorganizer.Browser.Page;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Every form refuses bad input with a plain message on the same page, and saves nothing. */
class InputIT {

    private final Browser browser = new Browser();

    @Test
    void emptySignUpFieldsAreRefused() throws Exception {
        String username = Browser.newUsername("empty");
        Map<String, String> form = Browser.signUpForm(username, "Ada", "Lovelace");
        form.put("firstname", "  ");
        form.put("email", "");

        Page page = browser.post("process-signup", form);
        assertTrue(page.shows("Error creating account, please try again"));
        assertTrue(page.shows("Firstname is required."));
        assertTrue(page.shows("Email is required."));
        assertTrue(page.shows("Create Account")); // still on the sign up page
        assertTrue(browser.logIn(username, Browser.PASSWORD).shows("Incorrect Username or Password"));
    }

    @Test
    void overLongSignUpFieldsAreRefused() throws Exception {
        Map<String, String> form = Browser.signUpForm(Browser.newUsername("long"), "x".repeat(51), "Lovelace");
        Page page = browser.post("process-signup", form);
        assertTrue(page.shows("Firstname must be at most 50 characters."));
    }

    @Test
    void aTakenUsernameIsRefused() throws Exception {
        String username = Browser.newUsername("twice");
        browser.signUp(username, "Ada", "Lovelace");
        Page page = browser.signUp(username, "Someone", "Else");
        assertTrue(page.shows("That username is already taken, please pick another one."));
    }

    @Test
    void aClassWithBadValuesIsRefusedAndNotSaved() throws Exception {
        browser.newStudent("Careful");
        Map<String, String> form = Browser.classForm("Bad Class", "5555", "25:00");
        form.put("class grade", "101");
        form.put("class credit", "0");

        Page page = browser.addClass(form);
        assertTrue(page.shows("Some Fields Were Incorrect or Empty Please Try Again"));
        assertTrue(page.shows("Time must be a time of day, like 09:30."));
        assertTrue(page.shows("Grade must be between 0 and 100."));
        assertTrue(page.shows("Credits must be between 1 and 12."));
        assertFalse(browser.get("table").shows("Bad Class"));
    }

    @Test
    void theSameCrnTwiceIsRefused() throws Exception {
        browser.newStudent("Repeat");
        browser.addClass(Browser.classForm("Intro to Java", "6060", "09:30"));
        Page page = browser.addClass(Browser.classForm("Intro to Java Again", "6060", "10:30"));
        assertTrue(page.shows("You already have a class with CRN 6060."));
    }

    @Test
    void removeNeedsANameAndANumber() throws Exception {
        browser.newStudent("Remover");
        Page page = browser.removeClass("", "abc");
        assertTrue(page.shows("Failed to Remove Class, try again"));
        assertTrue(page.shows("Classname is required."));
        assertTrue(page.shows("CRN must be a whole number."));
    }
}
