package com.example.classorganizer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/** The Add Class form. Only reached when logged in (see LoginFilter). */
@WebServlet(name = "ProcessClass", value = "/process-class")
public class ProcessClass extends HttpServlet {

    private static final String FAILED = "Error adding class, please try again";

    private Database database;

    @Override
    public void init() {
        database = Pages.database(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String className = Pages.field(request, "class-name");
        String subject = Pages.field(request, "class-subject");
        String time = Pages.field(request, "class time");
        String professor = Pages.field(request, "class professor");
        String crn = Pages.field(request, "class crn");
        String grade = Pages.field(request, "class grade");
        String credits = Pages.field(request, "class credit");
        Set<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);
        for (DayOfWeek day : SchoolClass.SCHOOL_DAYS) {
            if (request.getParameter(SchoolClass.label(day)) != null) { // a ticked box is sent, an empty one is not
                days.add(day);
            }
        }

        List<String> problems = Validation.checkClass(className, subject, time, professor, crn, grade, credits);
        if (!problems.isEmpty()) {
            Pages.show(request, response, "/AddClass.jsp", "Some Fields Were Incorrect or Empty Please Try Again",
                    problems);
            return;
        }

        SchoolClass schoolClass = new SchoolClass(CurrentUser.id(request), className, subject,
                Validation.parseTime(time).orElseThrow(), days, professor, Integer.parseInt(crn),
                Integer.parseInt(grade), Integer.parseInt(credits));
        try {
            if (!database.addClass(schoolClass)) {
                Pages.show(request, response, "/AddClass.jsp", FAILED,
                        List.of("You already have a class with CRN " + crn + "."));
                return;
            }
        } catch (SQLException e) {
            Pages.serverProblem(request, response, "/AddClass.jsp", FAILED, e);
            return;
        }
        Pages.show(request, response, "/Landing.jsp", "Class Added", List.of("For: " + CurrentUser.name(request)));
    }
}
