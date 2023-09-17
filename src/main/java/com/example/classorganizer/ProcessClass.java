package com.example.classorganizer;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "ProcessClass", value = "/process-class")
public class ProcessClass extends HttpServlet
{
    private String message;

    public void init()
    {
        message = "Data Processed";
    }

    Database myDatabase = new Database();

        public static Boolean getDayBoolean(String day){
            if (day != null)
            {
                return true;
            }
            else {
                return false;
            }
        }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public static String isFieldEmpty(HttpServletRequest request, String input)
    {
        Boolean check = false;

        if (isNumeric(input))
        {
            check =false;
        }else {
            check = true;
        }

        if(input.equals("")||input.equals(" ")||input.length()==0||input==null)
            check = false;
        else{
            check = true;
        }

        if (check)
        {
            return request.getParameter(input);
        }
        else
        {
            return "N/A";
        }
    }

    public static Boolean checkFields(String[] myFields)
    {
        for (String myField : myFields) {
            if (myField.equals("N/A")) {
                return false;
            }
        }
        return true;
    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        Boolean access = false;
        Boolean crnRepeat = false;

        String className = isFieldEmpty(request,"class-name");
        String classSubject = request.getParameter("class-subject");
        String monday = request.getParameter("Monday");
        String tuesday = request.getParameter("Tuesday");
        String wednesday = request.getParameter("Wednesday");
        String thursday = request.getParameter("Thursday");
        String friday = request.getParameter("Friday");
        String saturday = request.getParameter("Saturday");

        String professor = isFieldEmpty(request,"class professor");
        String crn = isFieldEmpty(request,"class crn");

        String grade = isFieldEmpty(request,"class grade");
        String time = isFieldEmpty(request,"class time");
        String credit = isFieldEmpty(request,"class credit");

        String[] fields ={className,professor,crn,grade,time,credit};

        String htmlResponse = "<html>";
        htmlResponse += "<head> <link href=\"main.css\" rel=\"stylesheet\" type=\"text/css\"> </head>";
        htmlResponse += "<body>";

        Boolean emptyFields = checkFields(fields);

        if (!emptyFields)
        {
            htmlResponse += "<h1>Some Fields Were Incorrect or Empty Please Try Again </h1>";
            try {
                request.getRequestDispatcher("/AddClass.jsp").include(request, response);
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
            out.println(htmlResponse);
            out.println("<br>");
            out.println("<a href=\"index.jsp\">Home</a>" + "</body> </html>");
            return;
        }

        int intCrn = Integer.parseInt(crn);
        int intGrade = Integer.parseInt(grade);
        int intCredit = Integer.parseInt(credit);

        Boolean day1 = getDayBoolean(monday);
        Boolean day2 = getDayBoolean(tuesday);
        Boolean day3 = getDayBoolean(wednesday);
        Boolean day4 = getDayBoolean(thursday);
        Boolean day5 = getDayBoolean(friday);
        Boolean day6 = getDayBoolean(saturday);

        HttpSession mySess = request.getSession();

        int myID = (Integer) mySess.getAttribute("sID");

        String stringId = Integer.toString(myID);

        String sqlCRN = myDatabase.getWhereEqualsSQL("userclasses", "crn", "idUserClasses", stringId);

        if (sqlCRN == null)
        {
            crnRepeat = false;
        }else if (sqlCRN.equals(crn)){
            crnRepeat = true;
        }

        myClass userClass = new myClass(myID,className,classSubject,time,day1,day2,day3,day4,day5,day6,professor, intCrn, intGrade , intCredit);

            if (!crnRepeat) {
                try {
                    access = myDatabase.insertClassData(userClass);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }


        if (access)
        {
            // build HTML code
            htmlResponse += "<h1> Class Added <h1/>";
            htmlResponse += "<h3> For: "  + request.getSession().getAttribute("name") + "<h3/>";
            out.println(htmlResponse);
            try {
                request.getRequestDispatcher("/Landing.jsp").include(request, response);
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
        }
        else{
            out.println(htmlResponse);
            out.println("<h1> Error adding class, please try again <h1/>");
            try {
                request.getRequestDispatcher("/AddClass.jsp").include(request,response);
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