package com.example.classorganizer;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "ProcessRemoveClass", value = "/process-remove-class")
public class ProcessRemoveClass extends HttpServlet {
    private String message;

    public void init() {
        message = "Data Removed";
    }

    Database myDatabase = new Database();


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Boolean access = false;

        String className = request.getParameter("class-name");
        String crn = request.getParameter("class crn");

        HttpSession mySess = request.getSession();

        int myID = (Integer) mySess.getAttribute("sID");

        String stringId = Integer.toString(myID);

        String sqlClassname = myDatabase.getWhereEqualsSQL("userclasses","className","idUserClasses", stringId);

        String sqlCRN = myDatabase.getWhereEqualsSQL("userclasses", "crn", "idUserClasses", stringId);

        if (sqlClassname.equals(className) && sqlCRN.equals(crn))
        {
            access = myDatabase.DeleteRow("crn","userclasses", crn);
        }


        if (access)
        {
            PrintWriter out = response.getWriter();

            String htmlResponse = "<html>";
            htmlResponse += "<div style= 'text-align: center'> ";
            htmlResponse += "<h1> Class Removed Successfully <h1/>";
            htmlResponse += "<h2> Class: " + className + "<h2/>";
            htmlResponse += "<h2> CRN: " + crn + "<h2/>";
            htmlResponse += "<h2> Your ID: " + stringId + "<h2/>";
            htmlResponse += "</div>";
            htmlResponse += "</html>";
            out.println(htmlResponse);

            try {
                request.getRequestDispatcher("/RemoveClass.jsp").include(request, response);
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
        }
        else
        {
            PrintWriter out = response.getWriter();

            String htmlResponse = "<html>";
            htmlResponse += "<div style = 'text-align: center'> ";
            htmlResponse += "<h1> Failed to Remove Class, try again <h1/>";
            htmlResponse += "</div>";
            htmlResponse += "</html>";
            out.println(htmlResponse);

            try {
                request.getRequestDispatcher("/RemoveClass.jsp").include(request, response);
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
        }
    }


}