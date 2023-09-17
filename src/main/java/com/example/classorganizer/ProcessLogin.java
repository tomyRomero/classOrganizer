package com.example.classorganizer;

import jakarta.servlet.RequestDispatcher;
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

@WebServlet(name = "ProcessLogin", value = "/process-login")
public class ProcessLogin extends HttpServlet
{
    private String message;

    public void init()
    {
        message = "Data Processed";
    }

    Database myDatabase = new Database();

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String sqlPassword = myDatabase.getWhereEqualsSQL("usertable","password","username",username);
        String sqlUsername = myDatabase.getWhereEqualsSQL("usertable","username","password", password);


        Boolean access = false;

        if (username.equals(sqlUsername) && password.equals(sqlPassword))
        {
            access = true;
        }


        PrintWriter out = response.getWriter();

        // build HTML code
        String htmlResponse = "<html>";
        htmlResponse += "<html>\n" +
                "<head>\n" +
                " <title>View Account</title>\n" +
                " <link href=\"main.css\" rel=\"stylesheet\" type=\"text/css\">" +
                "</head>\n" +
                "<body>\n";
        if (access)
        {
            HttpSession session1 = request.getSession();

            if (session1 != null) {
                try {
                    String fullName = myDatabase.getWhereEqualsSQL("usertable", "firstname", "password", password) + " " + myDatabase.getWhereEqualsSQL("usertable", "lastname", "username", username);

                    int sqlID = Integer.parseInt(myDatabase.getWhereEqualsSQL("usertable","id", "password", password));
                    session1.setAttribute("name", fullName);
                    session1.setAttribute("sUser", sqlUsername);
                    session1.setAttribute("sPass", sqlPassword);
                    session1.setAttribute("sID",sqlID);


                    RequestDispatcher requestDispatcher = request.getRequestDispatcher("/Landing.jsp");

                    requestDispatcher.forward(request, response);
                }
                catch (Exception e) {
                    htmlResponse += " <h3> error <h3>";
                }
            }
            else
            {
                htmlResponse += "<h1> Please login first <h1/>";
                try {
                    request.getRequestDispatcher("/Login.jsp").include(request, response);
                }
                catch (Exception e)
                {
                    System.out.println(e);
                }
            }
        }
        else
        {
            htmlResponse += "<h2> Incorrect Username or Password <h2/>";
            try {
                request.getRequestDispatcher("/Login.jsp").include(request, response);
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        }
        htmlResponse += "</body>";
        htmlResponse += "</html>";
        out.println(htmlResponse);
    }

    public void destroy()
    {

    }


}