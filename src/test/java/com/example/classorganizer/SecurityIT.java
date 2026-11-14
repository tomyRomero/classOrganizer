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
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Passwords, sessions, one student's data against another's, and escaping, through the running app. */
class SecurityIT {

    @Test
    void theAppStoresAHashNeverThePassword() throws Exception {
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
        assertNotEquals(Browser.PASSWORD, stored);
        assertFalse(stored.contains(Browser.PASSWORD));
        assertTrue(Passwords.matches(Browser.PASSWORD, stored));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Landing.jsp", "AddClass.jsp", "RemoveClass.jsp", "table", "account"})
    void pagesBehindTheLoginRefuseAVisitor(String path) throws Exception {
        Page page = new Browser().get(path);
        assertEquals(401, page.status());
        assertTrue(page.shows("Please login first"));
        assertFalse(page.shows("Welcome User"));
    }

    @Test
    void formsBehindTheLoginRefuseAVisitor() throws Exception {
        Browser visitor = new Browser();
        assertEquals(401, visitor.addClass(Browser.classForm("Sneaky", "1", "09:00")).status());
        assertEquals(401, visitor.removeClass("Sneaky", "1").status());
    }

    @Test
    void aWrongPasswordOrUnknownNameDoesNotLogIn() throws Exception {
        Browser browser = new Browser();
        String username = Browser.newUsername("wrong");
        browser.signUp(username, "Ada", "Lovelace");

        assertTrue(browser.logIn(username, "not the password").shows("Incorrect Username or Password"));
        assertTrue(browser.logIn("nobody" + username, Browser.PASSWORD).shows("Incorrect Username or Password"));
        assertTrue(browser.logIn("' OR '1'='1", "' OR '1'='1").shows("Incorrect Username or Password"));
        assertEquals(401, browser.get("table").status());
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
        assertNotNull(after);
        assertNotEquals(before, after);
        assertEquals(401, Browser.getWithSessionId("table", before).status());
        assertEquals(200, Browser.getWithSessionId("table", after).status());
    }

    @Test
    void logOutMakesTheOldSessionIdUseless() throws Exception {
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
        String escaped = "&lt;script&gt;alert(1)&lt;/script&gt;";
        Browser browser = new Browser();
        String username = Browser.newUsername("xss");
        browser.signUp(username, script, "Student");
        browser.logIn(username, Browser.PASSWORD);
        browser.addClass(Browser.classForm(script, "4242", "09:00"));

        for (String path : new String[] {"Landing.jsp", "AddClass.jsp", "RemoveClass.jsp", "table", "account"}) {
            Page page = browser.get(path);
            assertFalse(page.shows(script), path);
            assertTrue(page.shows(escaped), path);
        }
    }

    @Test
    void badRequestsGetAPlainMessageNeverAStackTrace() throws Exception {
        Page signUp = new Browser().post("process-signup", Map.of());
        assertEquals(200, signUp.status());
        assertTrue(signUp.shows("Error creating account, please try again"));

        Browser browser = new Browser();
        browser.newStudent("Empty");
        Page addClass = browser.post("process-class", Map.of());
        assertTrue(addClass.shows("Some Fields Were Incorrect or Empty Please Try Again"));

        for (Page page : new Page[] {signUp, addClass}) {
            assertFalse(page.shows("Exception"));
            assertFalse(page.shows("at com.example"));
        }
    }
}
