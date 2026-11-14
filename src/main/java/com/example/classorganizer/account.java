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

/** The View Account page: the student's details, how many classes and how many credits. */
@WebServlet(name = "Account", value = "/account")
public class Account extends HttpServlet {

    private Database database;

    @Override
    public void init() {
        database = Pages.database(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = CurrentUser.id(request);
        Optional<Customer> found;
        List<SchoolClass> classes;
        try {
            found = database.findById(userId);
            classes = database.classesFor(userId);
        } catch (SQLException e) {
            Pages.serverProblem(request, response, "/Landing.jsp", "Could not load your account", e);
            return;
        }
        if (found.isEmpty()) { // the account was deleted while its session was still open
            CurrentUser.logOut(request);
            response.sendRedirect("Login.jsp");
            return;
        }
        Customer customer = found.get();
        int totalCredits = classes.stream().mapToInt(SchoolClass::credits).sum();

        response.setContentType("text/html;charset=UTF-8");
        // A text block (""") keeps the page readable; every typed value goes through Html.escape.
        response.getWriter().println("""
                <html>
                <head>
                 <title>View Account</title>
                 <link href="main.css" rel="stylesheet" type="text/css">
                </head>
                <body>
                <div class="card">
                 <img style='height:auto' src="books.png" alt="bookslogo">
                    <h1>Personal Information</h1>
                    <h3 class='title'>FirstName: %s</h3>
                    <h3 class='title'>LastName: %s</h3>
                    <h3 class='title'>Email: %s</h3>
                    <h3 class='title'>Age: %d</h3>
                    <h3 class='title'>Username: %s</h3>
                    <h3 class='title'>Number-of-Classes: %d</h3>
                    <h3 class='title'>TotalCredits: %d</h3>
                </div>
                 <a href="Landing.jsp">Home</a>
                </body>
                </html>""".formatted(Html.escape(customer.firstName()), Html.escape(customer.lastName()),
                Html.escape(customer.email()), customer.age(), Html.escape(customer.username()), classes.size(),
                totalCredits));
    }
}
