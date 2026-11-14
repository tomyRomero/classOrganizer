package com.example.classorganizer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.List;

/** The View Classes page: the student's classes as a weekly table, with the GPA above it. */
@WebServlet(name = "Table", value = "/table")
public class Table extends HttpServlet {

    private static final List<String> HEADINGS = List.of("Class Name", "Subject", "Time", "Monday", "Tuesday",
            "Wednesday", "Thursday", "Friday", "Saturday", "Professor", "CRN", "Grade", "Credits");

    private Database database;

    @Override
    public void init() {
        database = Pages.database(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<SchoolClass> classes;
        try {
            classes = Schedule.inWeekOrder(database.classesFor(CurrentUser.id(request)));
        } catch (SQLException e) {
            Pages.serverProblem(request, response, "/Landing.jsp", "Could not load your classes", e);
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("""
                <html>
                <head>
                 <title>View Account</title>
                 <link href="main.css" rel="stylesheet" type="text/css">
                </head>
                <body>
                <img src="books.png" alt="bookslogo">
                <h1> My Classes </h1>""");
        if (classes.isEmpty()) {
            out.println("<h3>Classes are empty, try adding one and coming back</h3>");
        } else {
            out.println("<h3> My GPA: " + Schedule.gpa(classes).orElse(0) + "</h3>");
            out.println("<table border='1' class='styled-table'>");
            out.println("<tr>");
            for (String heading : HEADINGS) {
                out.println("<td>" + heading + "</td>");
            }
            out.println("</tr>");
            for (SchoolClass schoolClass : classes) {
                out.println("<tr>");
                cell(out, schoolClass.name());
                cell(out, schoolClass.subject());
                cell(out, schoolClass.time().toString());
                for (DayOfWeek day : SchoolClass.SCHOOL_DAYS) {
                    out.println("<td>" + (schoolClass.meetsOn(day) ? "&#10003;" : " ") + "</td>");
                }
                cell(out, schoolClass.professor());
                cell(out, String.valueOf(schoolClass.crn()));
                cell(out, String.valueOf(schoolClass.grade()));
                cell(out, String.valueOf(schoolClass.credits()));
                out.println("</tr>");
            }
            out.println("</table>");
        }
        out.println("""
                <br>
                <br>
                <br>
                <a href="Landing.jsp">Home</a>
                </body>
                </html>""");
    }

    private static void cell(PrintWriter out, String text) {
        out.println("<td>" + Html.escape(text) + "</td>");
    }
}
