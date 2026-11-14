package com.example.classorganizer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/** The Create Account form. Checks the fields, stores a hash of the password, never the password. */
@WebServlet(name = "ProcessSignUp", value = "/process-signup")
public class ProcessSignUp extends HttpServlet {

    private static final String FAILED = "Error creating account, please try again";

    private Database database;

    @Override
    public void init() {
        database = Pages.database(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String firstName = Pages.field(request, "firstname");
        String lastName = Pages.field(request, "lastname");
        String username = Pages.field(request, "username");
        String email = Pages.field(request, "email");
        String age = Pages.field(request, "age");
        String password = request.getParameter("password") == null ? "" : request.getParameter("password");

        List<String> problems = Validation.checkSignUp(firstName, lastName, username, password, email, age);
        if (!problems.isEmpty()) {
            Pages.show(request, response, "/SignUp.jsp", FAILED, problems);
            return;
        }

        Customer customer = new Customer(0, firstName, lastName, email, Integer.parseInt(age), username,
                Passwords.hash(password));
        try {
            if (!database.addCustomer(customer)) {
                Pages.show(request, response, "/SignUp.jsp", FAILED,
                        List.of("That username is already taken, please pick another one."));
                return;
            }
        } catch (SQLException e) {
            Pages.serverProblem(request, response, "/SignUp.jsp", FAILED, e);
            return;
        }
        Pages.show(request, response, "/Login.jsp", "Account Created: Proceed to Login",
                List.of("Your username is: " + username));
    }
}
