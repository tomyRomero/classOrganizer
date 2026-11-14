package com.example.classorganizer;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Runs once when the app starts: builds the one Database every servlet shares and, for the
 * embedded database, creates its tables. If SQL_Connection is missing the app refuses to start,
 * with a message saying what to set, instead of failing later on the first page.
 */
@WebListener
public class DatabaseSetup implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(DatabaseSetup.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent event) {
        Database database = Database.fromSettings();
        String driver = database.isEmbedded() ? "org.h2.Driver" : "com.mysql.cj.jdbc.Driver";
        try {
            // Some servers list their drivers before the app starts and miss ours in WEB-INF/lib; loading it by name registers it.
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("The database driver " + driver + " is missing from the app", e);
        }
        if (database.isEmbedded()) {
            try {
                database.createTables();
            } catch (SQLException | IOException e) {
                throw new IllegalStateException("Could not create the tables in the embedded database", e);
            }
            LOG.info("Using the embedded H2 database. In memory (./mvnw jetty:run) it empties when the app stops.");
        }
        event.getServletContext().setAttribute(Database.class.getName(), database);
    }
}
