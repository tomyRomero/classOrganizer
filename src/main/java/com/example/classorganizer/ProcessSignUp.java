package com.example.classorganizer;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "ProcessSignUp", value = "/process-signup")
public class ProcessSignUp extends HttpServlet
{
    private String message;

    public void init()
    {
        message = "Data Acquired";
    }

    Database myDatabase = new Database();

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        Boolean access = true;
        String firstName = request.getParameter("firstname");
        String lastName = request.getParameter("lastname");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String age = request.getParameter("age");

        if (firstName.equals("") || lastName.equals("")|| username.equals("")|| password.equals("") || email.equals("") || age.equals("") ){
            access = false;
        }

        try {
            int ageNew = Integer.parseInt(age);
            Customer customer = new Customer(myDatabase.getLastID("usertable","id")+1,firstName, lastName,email, ageNew ,username,password);
            System.out.println("Success new Customer Class");
            access = myDatabase.insertData(customer, response);
        }
        catch (Exception e)
        {
            access = false;
            System.out.println(e);
            System.out.println("attempted but no luck");
        }

        PrintWriter out = response.getWriter();

        if (access)
        {
            // build HTML code
            String htmlResponse = "<html>";
            htmlResponse += "<div style= text-align: center> ";
            htmlResponse += "<h1> Account Created: Proceed to Login <h1/>";
            htmlResponse += "<h2>Your username is: " + username + "<h2/>";
            htmlResponse += "</div>";
            htmlResponse += "</html>";
            out.println(htmlResponse);

            try {
                request.getRequestDispatcher("/Login.jsp").include(request, response);
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
        }
        else{
            out.println("<div style= text-align: center> ");
            out.println("<h1> Error creating account, please try again" + "<h1/>");
            out.println("</div>");
            try {
                request.getRequestDispatcher("/SignUp.jsp").include(request,response);
            }
            catch (Exception e)
            {
                System.out.println(e);
            }

        }


    }

    public void destroy()
    {

    }


}