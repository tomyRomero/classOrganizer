package com.example.classorganizer;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** The Log Out link. Ends the session on the server, then goes back to the home page. */
@WebServlet(name = "Logout", value = "/logout")
public class Logout extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CurrentUser.logOut(request);
        response.sendRedirect("index.jsp");
    }
}
