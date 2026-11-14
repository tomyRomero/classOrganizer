package com.example.classorganizer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** The Login form. Finds the account by username, then checks the password against its hash. */
@WebServlet(name = "ProcessLogin", value = "/process-login")
public class ProcessLogin extends HttpServlet {

    private Database database;

    @Override
    public void init() {
        database = Pages.database(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = Pages.field(request, "username");
        String password = request.getParameter("password") == null ? "" : request.getParameter("password");

        Optional<Customer> account;
        try {
            account = database.findByUsername(username);
        } catch (SQLException e) {
            Pages.serverProblem(request, response, "/Login.jsp", "Could not log you in", e);
            return;
        }
        boolean passwordMatches = account.isPresent()
                ? Passwords.matches(password, account.get().passwordHash())
                : Passwords.matchesNobody(password);
        if (!passwordMatches) {
            // The same words for a wrong username and a wrong password, so neither is given away.
            Pages.show(request, response, "/Login.jsp", "Incorrect Username or Password", List.of());
            return;
        }
        CurrentUser.logIn(request, account.get());
        // A redirect, so refreshing the next page does not send the password again.
        response.sendRedirect("Landing.jsp");
    }
}
