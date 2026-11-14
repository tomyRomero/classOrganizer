package com.example.classorganizer;

/** Reads one setting, such as SQL_Connection, from the environment or from a JVM system property. */
final class Settings {

    private Settings() {
    }

    /**
     * The environment wins, so a real deployment's variables always apply. The system property is
     * how ./mvnw jetty:run hands the app the embedded database's URL (see pom.xml).
     */
    static String read(String name) {
        String value = System.getenv(name);
        return value != null ? value : System.getProperty(name);
    }
}
