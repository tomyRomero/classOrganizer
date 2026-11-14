package com.example.classorganizer;

import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * A small browser for the integration tests: it keeps its own cookies (so each Browser is one
 * visitor), follows redirects and posts forms, against the app that "./mvnw verify" starts.
 */
final class Browser {

    static final String PASSWORD = "correct horse 1";

    private static final URI BASE = URI.create(System.getProperty("app.baseUrl", "http://localhost:8080/"));
    private static final AtomicInteger COUNTER = new AtomicInteger();

    private final CookieManager cookies = new CookieManager();
    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(cookies)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    record Page(int status, String body) {
        boolean shows(String text) {
            return body.contains(text);
        }
    }

    /** A username no other test uses: every test shares one running app and its database. */
    static String newUsername(String prefix) {
        return prefix + (System.nanoTime() % 100_000) + "x" + COUNTER.incrementAndGet();
    }

    Page get(String path) throws IOException, InterruptedException {
        return send(client, HttpRequest.newBuilder(BASE.resolve(path)).GET());
    }

    Page post(String path, Map<String, String> form) throws IOException, InterruptedException {
        String body = form.entrySet().stream()
                .map(field -> encode(field.getKey()) + "=" + encode(field.getValue()))
                .collect(Collectors.joining("&"));
        return send(client, HttpRequest.newBuilder(BASE.resolve(path))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body)));
    }

    /** This browser's session id, or null before the app has given it one. */
    String sessionId() {
        return cookies.getCookieStore().getCookies().stream()
                .filter(cookie -> cookie.getName().equals("JSESSIONID"))
                .map(HttpCookie::getValue)
                .findFirst()
                .orElse(null);
    }

    /** A request carrying only this session id, as if someone else had copied it. */
    static Page getWithSessionId(String path, String sessionId) throws IOException, InterruptedException {
        return send(HttpClient.newHttpClient(),
                HttpRequest.newBuilder(BASE.resolve(path)).header("Cookie", "JSESSIONID=" + sessionId).GET());
    }

    static Map<String, String> signUpForm(String username, String firstName, String lastName) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("firstname", firstName);
        form.put("lastname", lastName);
        form.put("username", username);
        form.put("password", PASSWORD);
        form.put("email", username + "@example.com");
        form.put("age", "23");
        return form;
    }

    static Map<String, String> classForm(String name, String crn, String time) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("class-name", name);
        form.put("class-subject", "Science");
        form.put("class time", time);
        form.put("Monday", "Monday");
        form.put("Wednesday", "Wednesday");
        form.put("class professor", "Dr.Fiction");
        form.put("class crn", crn);
        form.put("class grade", "95");
        form.put("class credit", "4");
        return form;
    }

    Page signUp(String username, String firstName, String lastName) throws IOException, InterruptedException {
        return post("process-signup", signUpForm(username, firstName, lastName));
    }

    Page logIn(String username, String password) throws IOException, InterruptedException {
        return post("process-login", Map.of("username", username, "password", password));
    }

    /** Signs up a new student called firstName Student, logs in, and returns the username. */
    String newStudent(String firstName) throws IOException, InterruptedException {
        String username = newUsername(firstName.toLowerCase());
        signUp(username, firstName, "Student");
        logIn(username, PASSWORD);
        return username;
    }

    Page addClass(Map<String, String> form) throws IOException, InterruptedException {
        return post("process-class", form);
    }

    Page removeClass(String name, String crn) throws IOException, InterruptedException {
        return post("process-remove-class", Map.of("class-name", name, "class crn", crn));
    }

    private static Page send(HttpClient client, HttpRequest.Builder request) throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(request.build(), HttpResponse.BodyHandlers.ofString());
        return new Page(response.statusCode(), response.body());
    }

    private static String encode(String text) {
        return URLEncoder.encode(text, StandardCharsets.UTF_8);
    }
}
