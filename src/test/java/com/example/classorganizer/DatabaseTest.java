package com.example.classorganizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** The Database class against a fresh embedded H2 database in MySQL mode, built from schema.sql. */
class DatabaseTest {

    private Database database;

    @BeforeEach
    void freshDatabase() throws Exception {
        database = new Database("jdbc:h2:mem:" + UUID.randomUUID()
                + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;IGNORECASE=TRUE;DB_CLOSE_DELAY=-1", null, null);
        database.createTables();
    }

    private int addStudent(String username) throws SQLException {
        assertTrue(database.addCustomer(new Customer(0, "Ada", "Lovelace", username + "@example.com", 23, username,
                Passwords.hash("correct horse 1"))));
        return database.findByUsername(username).orElseThrow().id();
    }

    private static SchoolClass aClass(int userId, String name, int crn) {
        return new SchoolClass(userId, name, "Science", LocalTime.of(9, 30),
                Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY), "Dr.Fiction", crn, 95, 4);
    }

    private List<String> classNames(int userId) throws SQLException {
        return database.classesFor(userId).stream().map(SchoolClass::name).toList();
    }

    @Test
    void theStoredPasswordIsAHashNotThePassword() throws SQLException {
        addStudent("ada01");
        String stored = database.findByUsername("ada01").orElseThrow().passwordHash();
        assertNotEquals("correct horse 1", stored);
        assertFalse(stored.contains("correct horse 1"));
        assertTrue(Passwords.matches("correct horse 1", stored));
    }

    @Test
    void aUsernameCanOnlyBeTakenOnceWhateverItsCase() throws SQLException {
        addStudent("ada01");
        Customer again = new Customer(0, "Other", "Person", "other@example.com", 30, "ADA01", Passwords.hash("x2345678"));
        assertFalse(database.addCustomer(again));
    }

    @Test
    void aClassComesBackExactlyAsSaved() throws SQLException {
        int ada = addStudent("ada01");
        SchoolClass saved = aClass(ada, "Intro to Java", 1234);
        assertTrue(database.addClass(saved));
        assertEquals(List.of(saved), database.classesFor(ada));
    }

    @Test
    void oneStudentCannotUseTheSameCrnTwice() throws SQLException {
        int ada = addStudent("ada01");
        assertTrue(database.addClass(aClass(ada, "Intro to Java", 1234)));
        assertFalse(database.addClass(aClass(ada, "Another Name", 1234)));
    }

    @Test
    void classesAreReadAndRemovedOnlyForTheirOwner() throws SQLException {
        int alice = addStudent("alice");
        int bob = addStudent("bob");
        database.addClass(aClass(alice, "Secret Seminar", 7777));
        database.addClass(aClass(bob, "Open Seminar", 7777)); // the same CRN is fine for another student

        assertEquals(List.of("Secret Seminar"), classNames(alice));
        assertFalse(database.removeClass(bob, "Secret Seminar", 7777));
        assertTrue(database.removeClass(bob, "Open Seminar", 7777));
        assertEquals(List.of("Secret Seminar"), classNames(alice));
        assertEquals(List.of(), classNames(bob));
    }

    @Test
    void aBrokenDatabaseThrowsInsteadOfHidingTheProblem() {
        Database noTables = new Database("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL", null, null);
        assertThrows(SQLException.class, () -> noTables.classesFor(1));
        assertThrows(SQLException.class, () -> noTables.findByUsername("ada01"));
    }
}
