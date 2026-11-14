package com.example.classorganizer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Every SQL statement the app runs, in one place.
 *
 * Each method opens a connection and closes it, with its statement and results, through
 * try-with-resources (Java's version of C#'s "using"). Values always go in as "?" parameters,
 * never glued into the SQL text, so nothing a user types can change a query. Failures are thrown
 * as SQLException, never hidden; the servlets log them and show the student a plain line.
 */
public final class Database {

    private static final String CUSTOMER_COLUMNS = "id, firstname, lastname, email, age, username, password_hash";
    private static final String CLASS_COLUMNS = "idUserClasses, ClassName, Subject, Time, Monday, Tuesday,"
            + " Wednesday, Thursday, Friday, Saturday, Professor, CRN, Grade, Credits";

    private final String url;
    private final String user;
    private final String password;

    Database(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /** The same three settings the app has always read: SQL_Connection, user and password. */
    static Database fromSettings() {
        String url = Settings.read("SQL_Connection");
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("SQL_Connection is not set. Point it at your MySQL database,"
                    + " or start the app with ./mvnw jetty:run to use the embedded one.");
        }
        return new Database(url, Settings.read("user"), Settings.read("password"));
    }

    /** True for the in-memory H2 database of local runs and tests, which starts empty every time. */
    boolean isEmbedded() {
        return url.startsWith("jdbc:h2:");
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /** Runs schema.sql. The embedded database needs it on every start; MySQL gets it from compose.yaml. */
    void createTables() throws SQLException, IOException {
        String script;
        try (InputStream in = Database.class.getResourceAsStream("/schema.sql")) {
            if (in == null) {
                throw new IOException("schema.sql is missing from the classpath");
            }
            script = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        String withoutComments = script.lines()
                .filter(line -> !line.strip().startsWith("--"))
                .collect(Collectors.joining("\n"));
        try (Connection connection = connect()) {
            for (String sql : withoutComments.split(";")) {
                if (!sql.isBlank()) {
                    try (PreparedStatement statement = connection.prepareStatement(sql)) {
                        statement.execute();
                    }
                }
            }
        }
    }

    /**
     * Saves a new student and returns true, or returns false when the username is taken. The
     * UNIQUE constraint decides, so two people signing up at the same moment cannot both get it.
     */
    boolean addCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO usertable (firstname, lastname, email, age, username, password_hash)"
                + " VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customer.firstName());
            statement.setString(2, customer.lastName());
            statement.setString(3, customer.email());
            statement.setInt(4, customer.age());
            statement.setString(5, customer.username());
            statement.setString(6, customer.passwordHash());
            statement.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException usernameTaken) {
            return false;
        }
    }

    Optional<Customer> findByUsername(String username) throws SQLException {
        return findCustomer("SELECT " + CUSTOMER_COLUMNS + " FROM usertable WHERE username = ?", username);
    }

    Optional<Customer> findById(int id) throws SQLException {
        return findCustomer("SELECT " + CUSTOMER_COLUMNS + " FROM usertable WHERE id = ?", id);
    }

    private Optional<Customer> findCustomer(String sql, Object key) throws SQLException {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, key);
            try (ResultSet row = statement.executeQuery()) {
                if (!row.next()) {
                    return Optional.empty();
                }
                return Optional.of(new Customer(row.getInt("id"), row.getString("firstname"),
                        row.getString("lastname"), row.getString("email"), row.getInt("age"),
                        row.getString("username"), row.getString("password_hash")));
            }
        }
    }

    /** Saves a class and returns true, or returns false when this student already has a class with that CRN. */
    boolean addClass(SchoolClass schoolClass) throws SQLException {
        String sql = "INSERT INTO userclasses (" + CLASS_COLUMNS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, schoolClass.userId());
            statement.setString(2, schoolClass.name());
            statement.setString(3, schoolClass.subject());
            statement.setObject(4, schoolClass.time());
            int column = 5;
            for (DayOfWeek day : SchoolClass.SCHOOL_DAYS) {
                statement.setBoolean(column++, schoolClass.meetsOn(day));
            }
            statement.setString(11, schoolClass.professor());
            statement.setInt(12, schoolClass.crn());
            statement.setInt(13, schoolClass.grade());
            statement.setInt(14, schoolClass.credits());
            statement.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException crnTaken) {
            return false;
        }
    }

    /** Only this student's classes: the WHERE clause is what keeps one student out of another's. */
    List<SchoolClass> classesFor(int userId) throws SQLException {
        String sql = "SELECT " + CLASS_COLUMNS + " FROM userclasses WHERE idUserClasses = ?";
        List<SchoolClass> classes = new ArrayList<>();
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet row = statement.executeQuery()) {
                while (row.next()) {
                    Set<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);
                    for (DayOfWeek day : SchoolClass.SCHOOL_DAYS) {
                        if (row.getBoolean(SchoolClass.label(day))) {
                            days.add(day);
                        }
                    }
                    classes.add(new SchoolClass(row.getInt("idUserClasses"), row.getString("ClassName"),
                            row.getString("Subject"), row.getObject("Time", LocalTime.class), days,
                            row.getString("Professor"), row.getInt("CRN"), row.getInt("Grade"),
                            row.getInt("Credits")));
                }
            }
        }
        return classes;
    }

    /** Removes the class with this name and CRN, only if it belongs to this student. True when one was removed. */
    boolean removeClass(int userId, String className, int crn) throws SQLException {
        String sql = "DELETE FROM userclasses WHERE idUserClasses = ? AND ClassName = ? AND CRN = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setString(2, className);
            statement.setInt(3, crn);
            return statement.executeUpdate() > 0;
        }
    }
}
