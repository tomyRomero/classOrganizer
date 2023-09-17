package com.example.classorganizer;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "table", value = "/table")
public class table extends HttpServlet
{




    public static String getLetterGrade(int grade)
    {
        if (grade >= 97 && grade <= 100)
        {
            return "A+";
        }else if (grade >= 93 && grade <= 96)
        {
            return "A";
        }else if(grade >= 90 && grade <= 92)
        {
            return "A-";
        }else if(grade >= 87 && grade <= 89)
        {
            return "B+";
        }else if (grade >= 83 && grade <= 86)
        {
            return "B";
        }else if (grade >= 80 && grade <= 82)
        {
            return "B-";
        }else if (grade >= 77 && grade <= 79)
        {
            return "C+";
        }else if (grade >= 73 && grade <= 76)
        {
            return "C";
        }else if (grade >= 70 && grade <= 72)
        {
            return "C-";
        }else if (grade >= 67 && grade <= 69)
        {
            return "D+";
        }else if (grade >= 65 && grade <= 66)
        {
            return "D";
        }else if ( grade < 65)
        {
            return "F";
        }else{
            return "Grade too high or low";
        }
    }

    public static double getScore(String letter)
    {
        if (letter.equals("A+")||letter.equals("A"))
        {
            return 4.0;
        }else if (letter.equals("A-")){
            return 3.7;
        }else if (letter.equals("B+"))
        {
            return 3.3;
        }else if (letter.equals("B"))
        {
            return 3.0;
        }else if(letter.equals("B-"))
        {
            return 2.7;
        }else if (letter.equals("C+"))
        {
            return 2.3;
        }else if (letter.equals("C"))
        {
            return 2.0;
        }else if (letter.equals("C-"))
        {
            return 1.7;
        }else if (letter.equals("D+"))
        {
            return 1.3;
        }else if (letter.equals("D"))
        {
            return 1.0;
        }else if (letter.equals("D-"))
        {
            return 0.7;
        }else if (letter.equals("F")){
            return 0.0;
        }else{
            return 0;
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double getGPA(ArrayList myClasses)
    {
        double totalScore=0;
        int totalHours=0;

        for (int i = 0; i <myClasses.size() ; i++)
        {
            List<ArrayList> myclass = (List<ArrayList>) myClasses.get(i);

            int grade = Integer.parseInt(String.valueOf(myclass.get(11)));
            int credits = Integer.parseInt(String.valueOf(myclass.get(12)));

            String letter= getLetterGrade(grade);

            double score = getScore(letter);

            double temp = score * credits;

            totalScore += temp;

            totalHours += credits;
        }

        double tempGpa=  totalScore/totalHours;
        double gpa = round(tempGpa,2);

        return gpa;

    }

    Database myDatabase = new Database();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        PrintWriter out = response.getWriter();

        HttpSession mySess = request.getSession();

        int myID = (Integer) mySess.getAttribute("sID");

        List<ArrayList> myClasses = myDatabase.getClasses(myID, "userclasses");

        try {
            out.println("<html>\n" +
                            "<head>\n" +
                            " <title>View Account</title>\n" +
                            " <link href=\"main.css\" rel=\"stylesheet\" type=\"text/css\">" +
                            "</head>\n" +
                            "<body>\n");

            out.println("<img src=\"books.png\" alt=\"bookslogo\">");

            out.println("<h1> My Classes </h1> ");

            String sGPA = Double.toString(getGPA((ArrayList) myClasses));

            out.println("<h3> My GPA: " + sGPA + "</h3>");

            out.println("<table border='1' class='styled-table' ");

            out.println("<tr>");
            out.println("<td>");
            out.println("Class Name");
            out.println("</td>");

            out.println("<td>");
            out.println("Subject");
            out.println("</td>");

            out.println("<td>");
            out.println("Time");
            out.println("</td>");

            out.println("<td>");
            out.println("Monday");
            out.println("</td>");

            out.println("<td>");
            out.println("Tuesday");
            out.println("</td>");

            out.println("<td>");
            out.println("Wednesday");
            out.println("</td>");

            out.println("<td>");
            out.println("Thursday");
            out.println("</td>");

            out.println("<td>");
            out.println("Friday");
            out.println("</td>");

            out.println("<td>");
            out.println("Saturday");
            out.println("</td>");

            out.println("<td>");
            out.println("Professor");
            out.println("</td>");

            out.println("<td>");
            out.println("CRN");
            out.println("</td>");

            out.println("<td>");
            out.println("Grade");
            out.println("</td>");

            out.println("<td>");
            out.println("Credits");
            out.println("</td>");

            out.println("</tr>");

            for (int i = 0; i <myClasses.size() ; i++)
            {
                List<ArrayList> myclass = myClasses.get(i);
                out.println("<tr>");
                for (int j = 0; j < 13; j++)
                {
                    out.println("<td>");
                    out.println(myclass.get(j));
                    out.println("</td>");
                }
                out.println("</tr>");
            }

            out.println("</table>");

            out.println("<br>");
            out.println("<br>");
            out.println("<br>");

            out.println("<a href=\"Landing.jsp\">Home</a>");
            out.println("</body>");
            out.println("</html>");

        }
        catch (Exception e)
        {
            System.out.println(e);
            String htmlResponse = "<html>";
            htmlResponse += "<head> <link href=\\\\\\\"main.css\\\\\\\" rel=\\\\\\\"stylesheet\\\\\\\" type='text/css'></head>";
            htmlResponse += "<body> " +
                    "<h3>Classes are empty, try adding one and coming back</h3>";
            htmlResponse +=("<a href=\"Landing.jsp\">Home</a>");
            htmlResponse += "</body> </html>";
            out.println(htmlResponse);
        }
    }

    public void destroy()
    {

    }


}