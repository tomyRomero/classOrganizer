package com.example.classorganizer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PasswordsTest {

    private static final String PASSWORD = "correct horse 1";

    @Test
    void theStoredValueIsNotThePassword() {
        String stored = Passwords.hash(PASSWORD);
        assertNotEquals(PASSWORD, stored);
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
        assertFalse(Passwords.matches("", stored));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", PASSWORD, "pbkdf2-sha256$abc$AAAA$AAAA", "pbkdf2-sha256$1$$AAAA", "md5$1$AAAA$AAAA"})
    void aDamagedOrPlainStoredValueNeverMatches(String stored) {
        assertFalse(Passwords.matches(PASSWORD, stored));
    }

    @Test
    void anUnknownUserNeverMatches() {
        assertFalse(Passwords.matchesNobody("no account has this password"));
        assertFalse(Passwords.matches(PASSWORD, null));
    }
}
