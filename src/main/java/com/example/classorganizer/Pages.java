package com.example.classorganizer;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Small helpers every servlet uses to read a form and answer with a page. */
final class Pages {

    static final String SERVER_PROBLEM = "Something went wrong on our side, please try again.";

    private static final Logger LOG = Logger.getLogger(Pages.class.getName());

    private Pages() {
    }

    /** The one Database, created by DatabaseSetup when the app started. */
    static Database database(ServletContext context) {
        return (Database) context.getAttribute(Database.class.getName());
    }

    /** A form field without the spaces around it, or "" when the form did not send it. */
    static String field(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value.strip();
    }

    /**
     * Shows a JSP page with a heading and detail lines above it, the way this app has always
     * reported what happened. The page escapes both (WEB-INF/message.jspf).
     */
    static void show(HttpServletRequest request, HttpServletResponse response, String page, String heading,
                     List<String> details) throws ServletException, IOException {
        request.setAttribute("heading", heading);
        request.setAttribute("details", details);
        request.getRequestDispatcher(page).forward(request, response);
    }

    /** For a failure on our side: the log gets the cause and stack trace, the student gets a plain line. */
    static void serverProblem(HttpServletRequest request, HttpServletResponse response, String page, String heading,
                              Exception cause) throws ServletException, IOException {
        LOG.log(Level.SEVERE, "Request to " + request.getRequestURI() + " failed", cause);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        show(request, response, page, heading, List.of(SERVER_PROBLEM));
    }
}
