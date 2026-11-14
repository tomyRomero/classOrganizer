package com.example.classorganizer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordsTest {

    private static final String PASSWORD = "correct horse 1";

    @Test
    void theStoredValueIsNotThePassword() {
        String stored = Passwords.hash(PASSWORD);
        assertFalse(stored.contains(PASSWORD));
        assertTrue(stored.startsWith("pbkdf2-sha256$600000$"));
    }

    @Test
    void theSamePasswordGetsADifferentHashEachTime() {
        assertNotEquals(Passwords.hash(PASSWORD), Passwords.hash(PASSWORD));
    }

    @Test
    void onlyTheRightPasswordMatches() {
        String stored = Passwords.hash(PASSWORD);
        assertTrue(Passwords.matches(PASSWORD, stored));
        assertFalse(Passwords.matches("correct horse 2", stored));
        // A password saved as typed, like the 2023 version did, never matches.
        assertFalse(Passwords.matches(PASSWORD, PASSWORD));
    }
}
