package com.example.classorganizer;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "account", value = "/account")
public class account extends HttpServlet {
    private String message;

    public void init() {
        message = "Data Processed";
    }

    Database myDatabase = new Database();
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        PrintWriter out = response.getWriter();

        HttpSession mySess = request.getSession();

        int myID = (Integer) mySess.getAttribute("sID");

        String stringId = Integer.toString(myID);

        String fName = myDatabase.getWhereEqualsSQL("usertable","firstname","id",stringId);
        String lName = myDatabase.getWhereEqualsSQL("usertable","lastname","id",stringId);
        String email = myDatabase.getWhereEqualsSQL("usertable","email","id",stringId);
        String age = myDatabase.getWhereEqualsSQL("usertable","age","id",stringId);
        String username = myDatabase.getWhereEqualsSQL("usertable","username","id",stringId);
        String numClass = Integer.toString(myDatabase.getNumberClasses(myID ,"userclasses"));
        String numCredits = Integer.toString(myDatabase.getNumberCredits(myID,"userclasses"));

        out.println("<html>\n" +
                "<head>\n" +
                " <title>View Account</title>\n" +
                " <link href=\"main.css\" rel=\"stylesheet\" type=\"text/css\">" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"card\">" +
                " <img style='weight:80%; height:auto' src=\"books.png\" alt=\"bookslogo\">" +
                "    <h1>Personal Information</h1>\n" +
                "    <h3 class='title'>FirstName: " + fName + "</h3>\n" +
                "    <h3 class='title'>LastName: " + lName + "</h3>\n" +
                "    <h3 class='title'>Email: " + email + "</h3>\n" +
                "    <h3 class='title'>Age: " + age + "</h3>\n" +
                "    <h3 class='title'>Username: " + username + "</h3>\n" +
                "    <h3 class='title'>Number-of-Classes: " + numClass + "</h3>\n" +
                "    <h3 class='title'>TotalCredits: " + numCredits + "</h3>\n" +
                "</div>" +
                "\n" +
                " <a href=\"Landing.jsp\">Home</a>\n" +
                "</body>\n" +
                "</html>");
    }


    public void destroy()
    {

    }
}