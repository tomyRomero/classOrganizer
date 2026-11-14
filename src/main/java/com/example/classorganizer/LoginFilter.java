package com.example.classorganizer;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Guards every page that needs a logged-in student, in one place instead of in each servlet.
 * A visitor gets the login page with "Please login first" (status 401) instead of the page.
 */
@WebFilter(urlPatterns = {"/Landing.jsp", "/AddClass.jsp", "/RemoveClass.jsp", "/table", "/account",
        "/process-class", "/process-remove-class"})
public class LoginFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (CurrentUser.id(request) != null) {
            chain.doFilter(request, response);
            return;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Pages.show(request, response, "/Login.jsp", "Please login first", List.of());
    }
}
