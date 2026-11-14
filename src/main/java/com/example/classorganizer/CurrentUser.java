package com.example.classorganizer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/** Who is logged in. Kept in the server-side session; the browser only holds the session's id. */
final class CurrentUser {

    static final String ID = "userId";
    /** The JSP pages show this one as ${name}. */
    static final String NAME = "name";

    private CurrentUser() {
    }

    /** The logged-in student's id, or null for a visitor. */
    static Integer id(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (Integer) session.getAttribute(ID);
    }

    static String name(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (String) session.getAttribute(NAME);
    }

    /**
     * Starts a brand-new session for the student. Throwing away the old one changes the session id,
     * so an id someone planted or saw before login is worth nothing after it.
     */
    static void logIn(HttpServletRequest request, Customer customer) {
        logOut(request);
        HttpSession session = request.getSession(true);
        session.setAttribute(ID, customer.id());
        session.setAttribute(NAME, customer.fullName());
    }

    static void logOut(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
