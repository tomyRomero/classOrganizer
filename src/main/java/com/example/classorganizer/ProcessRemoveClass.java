package com.example.classorganizer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/** The Remove Class form. Removes a class only from the logged-in student's own schedule. */
@WebServlet(name = "ProcessRemoveClass", value = "/process-remove-class")
public class ProcessRemoveClass extends HttpServlet {

    private static final String FAILED = "Failed to Remove Class, try again";

    private Database database;

    @Override
    public void init() {
        database = Pages.database(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String className = Pages.field(request, "class-name");
        String crn = Pages.field(request, "class crn");
        List<String> problems = Validation.checkRemoveClass(className, crn);
        if (!problems.isEmpty()) {
            Pages.show(request, response, "/RemoveClass.jsp", FAILED, problems);
            return;
        }

        int userId = CurrentUser.id(request);
        boolean removed;
        try {
            removed = database.removeClass(userId, className, Integer.parseInt(crn));
        } catch (SQLException e) {
            Pages.serverProblem(request, response, "/RemoveClass.jsp", FAILED, e);
            return;
        }
        if (removed) {
            Pages.show(request, response, "/RemoveClass.jsp", "Class Removed Successfully",
                    List.of("Class: " + className, "CRN: " + crn, "Your ID: " + userId));
        } else {
            Pages.show(request, response, "/RemoveClass.jsp", FAILED,
                    List.of("You have no class named " + className + " with CRN " + crn + "."));
        }
    }
}
