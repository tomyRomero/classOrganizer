package com.example.classorganizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.classorganizer.Browser.Page;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** The security faults the 2023 version had, each checked through the running app. */
class SecurityIT {

    @Test
    void logInFindsTheAccountByUsernameNotByPassword() throws Exception {
        // Both students have the same password. The 2023 version looked accounts up by password.
        Browser browser = new Browser();
        String grace = Browser.newUsername("grace");
        browser.signUp(Browser.newUsername("ada"), "Ada", "Lovelace");
        browser.signUp(grace, "Grace", "Hopper");

        assertTrue(browser.logIn(grace, Browser.PASSWORD).shows("Welcome User: Grace Hopper"));
        assertTrue(new Browser().logIn(grace, "not the password").shows("Incorrect Username or Password"));
    }

    @Test
    void aPasswordIsNeverStoredAsTyped() throws Exception {
        String username = Browser.newUsername("hash");
        new Browser().signUp(username, "Ada", "Lovelace");

        String stored;
        try (Connection connection = DriverManager.getConnection(System.getProperty("it.databaseUrl"));
             PreparedStatement statement =
                     connection.prepareStatement("SELECT password_hash FROM usertable WHERE username = ?")) {
            statement.setString(1, username);
            try (ResultSet row = statement.executeQuery()) {
                assertTrue(row.next());
                stored = row.getString(1);
            }
        }
        assertFalse(stored.contains(Browser.PASSWORD));
        assertTrue(Passwords.matches(Browser.PASSWORD, stored));
    }

    @Test
    void typedTextCannotChangeAQuery() throws Exception {
        assertTrue(new Browser().logIn("' OR '1'='1", "' OR '1'='1").shows("Incorrect Username or Password"));

        Browser browser = new Browser();
        browser.newStudent("Robert");
        browser.addClass(Browser.classForm("Intro to Java", "1234", "09:30"));
        String sneaky = "x'); DELETE FROM userclasses; --";
        assertTrue(browser.addClass(Browser.classForm(sneaky, "2345", "10:30")).shows("Class Added"));

        Page schedule = browser.get("table");
        assertTrue(schedule.shows("Intro to Java"));
        assertTrue(schedule.shows(Html.escape(sneaky)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Landing.jsp", "AddClass.jsp", "RemoveClass.jsp", "table", "account"})
    void pagesBehindTheLoginRefuseAVisitor(String path) throws Exception {
        Page page = new Browser().get(path);
        assertEquals(401, page.status());
        assertTrue(page.shows("Please login first"));
    }

    @Test
    void formsBehindTheLoginRefuseAVisitor() throws Exception {
        Browser visitor = new Browser();
        assertEquals(401, visitor.addClass(Browser.classForm("Sneaky", "1", "09:00")).status());
        assertEquals(401, visitor.removeClass("Sneaky", "1").status());
    }

    @Test
    void theSessionIdChangesAtLogin() throws Exception {
        Browser browser = new Browser();
        String username = Browser.newUsername("fixate");
        browser.signUp(username, "Ada", "Lovelace");
        String before = browser.sessionId();
        assertNotNull(before);

        browser.logIn(username, Browser.PASSWORD);
        String after = browser.sessionId();
        assertNotEquals(before, after);
        assertEquals(200, Browser.getWithSessionId("table", after).status());
    }

    @Test
    void logOutEndsTheSession() throws Exception {
        Browser browser = new Browser();
        browser.newStudent("Leaving");
        String sessionId = browser.sessionId();
        assertEquals(200, Browser.getWithSessionId("table", sessionId).status());

        browser.get("logout");
        assertEquals(401, Browser.getWithSessionId("table", sessionId).status());
    }

    @Test
    void oneStudentCanNeverReadOrRemoveAnothersClasses() throws Exception {
        Browser alice = new Browser();
        alice.newStudent("Alice");
        alice.addClass(Browser.classForm("Secret Seminar", "7777", "10:00"));
        Browser bob = new Browser();
        bob.newStudent("Bob");

        assertFalse(bob.get("table").shows("Secret Seminar"));
        assertTrue(bob.removeClass("Secret Seminar", "7777").shows("Failed to Remove Class, try again"));
        assertTrue(alice.get("table").shows("Secret Seminar"));

        // Bob may use the same CRN, and removing his class leaves Alice's alone.
        assertTrue(bob.addClass(Browser.classForm("Open Seminar", "7777", "11:00")).shows("Class Added"));
        assertTrue(bob.removeClass("Open Seminar", "7777").shows("Class Removed Successfully"));
        assertTrue(alice.get("table").shows("Secret Seminar"));
    }

    @Test
    void typedTextIsEscapedOnEveryPage() throws Exception {
        String script = "<script>alert(1)</script>";
        Browser browser = new Browser();
        String username = Browser.newUsername("xss");
        browser.signUp(username, script, "Student");
        browser.logIn(username, Browser.PASSWORD);
        browser.addClass(Browser.classForm(script, "4242", "09:00"));

        for (String path : new String[] {"Landing.jsp", "AddClass.jsp", "RemoveClass.jsp", "table", "account"}) {
            Page page = browser.get(path);
            assertFalse(page.shows(script), path);
            assertTrue(page.shows(Html.escape(script)), path);
        }
    }
}
