package com.example.classorganizer;

import jakarta.servlet.http.HttpServletResponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class Database {

    private final String sqlUrl;
    private final String  classForName;
    private final String user;
    private final String password;

    public Database()
    {
        this.classForName = "com.mysql.cj.jdbc.Driver";
        this.sqlUrl = System.getenv("SQL_Connection");
        this.user = System.getenv("user");
        this.password = System.getenv("password");

    }


     public  int getLastID(String table, String column) {
         try {
             Class.forName(classForName);
             Connection connection = DriverManager.getConnection(sqlUrl,user,password);
             System.out.println("Connection" + connection);
             Statement statement = connection.createStatement();
             ResultSet resultset = statement.executeQuery("select " + column + " from " + table + " ORDER BY id DESC LIMIT 1;");
             String season = null;
             if(resultset.next())
             {
                 season = resultset.getString(1);
             }
             int data = Integer.parseInt(season);
             return data;

         } catch (Exception e) {
             e.printStackTrace();
             return 0;
         }
     }

    public  Boolean insertData(Customer c, HttpServletResponse response)
    {
        Boolean test= false;
        try {

            System.out.println("Connecting ...");
            Class.forName(classForName);
            Connection connection = DriverManager.getConnection(sqlUrl,user,password);
            System.out.println("Trying ..Connecting to a selected database..." + connection);
            Statement st = connection.createStatement();

            String SQL_INSERT = "INSERT INTO usertable(id,firstname,lastname,email,age,username, password) VALUES (?,?,?,?,?,?,?)";
            PreparedStatement prepStat= connection.prepareStatement(SQL_INSERT);

            System.out.println("Trying 2..Connecting to a selected database..." + prepStat);

            prepStat.setInt(1,c.getId());
            prepStat.setString(2,c.getFirstName());
            prepStat.setString(3,c.getLastName());
            prepStat.setString(4,c.getEmail());
            prepStat.setInt(5,c.getAge());
            prepStat.setString(6,c.getUsername());
            prepStat.setString(7,c.getPassword());

            System.out.println("Trying 2..Connecting to a selected database...");
            int row = prepStat.executeUpdate();
            if (row> 0) {
                System.out.println("A new user was inserted successfully!");
                test = true;
            }

            System.out.println("Store into the database");

        }
        catch (Exception ex)
        {
            System.out.println("Error in connection:" + ex.getCause());
        }
        return test;
    }

    public  String getWhereEqualsSQL(String table, String get, String where, String equal)
    {

        try {
            Class.forName(classForName);
            Connection connection = DriverManager.getConnection(sqlUrl,user,password);
            System.out.println("Connection" + connection);
            Statement statement = connection.createStatement();

            ResultSet resultset = statement.executeQuery("select "+ get + " from " + table + " where "+ where + "='" + equal +"';");


            String season = null;
            if(resultset.next())
            {
                season = resultset.getString(1);
            }

            return season;


        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid";
        }
    }

    public int getNumberClasses(int Id, String table)
    {
        List<ArrayList> myClasses = new ArrayList<ArrayList>();
        try{

            Class.forName(classForName);
            Connection conn = DriverManager.getConnection(sqlUrl,user,password);

            Statement stat = null;
            ResultSet result = null;

            stat = conn.createStatement();
            result = stat.executeQuery("SELECT * FROM " + table);
            int count = 0;
            while (result.next())
            {
                if (result.getInt("idUserClasses")== Id)
                {
                    count += 1;
                }
            }
            return count;
        }
        catch (Exception e)
        {
            System.err.println("SQLException in Query.java");
            return 0;
        }
    }

    public int getNumberCredits(int Id, String table)
    {
        List<ArrayList> myClasses = new ArrayList<ArrayList>();
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(sqlUrl,user,password);
            Statement stat = null;
            ResultSet result = null;
            int totalCredits =0;
            stat = conn.createStatement();
            result = stat.executeQuery("SELECT * FROM " + table);
            while (result.next())
            {
                if (result.getInt("idUserClasses")== Id)
                {
                    totalCredits += result.getInt("Credits");
                }
            }
            return totalCredits;
        }
        catch (Exception e)
        {
            System.err.println("SQLException in Query.java");
            return 0;
        }
    }


    public static int getBooleanDay(Boolean myInput)
    {
        if (myInput == true)
        {
            return 1;
        }else if(myInput == false)
        {
            return 0;
        }else
        {
            System.out.println("Error in translating Boolean input");
            return -1;
        }
    }

    public  Boolean insertClassData(myClass c)
    {
        Boolean access =false;
        try {
            Class.forName(classForName);
            Connection conn = DriverManager.getConnection(sqlUrl, user, password);

            Statement stmt = conn.createStatement();

            int idUserClassesV = c.getUserId();
            String ClassNameV = c.getClassName();
            String subjectV = c.getClassSubject();
            String timeV = c.getTime();
            int mondayV = getBooleanDay(c.getMonday());
            int tuesdayV = getBooleanDay(c.getTuesday());
            int wednesdayV = getBooleanDay(c.getWednesday());
            int thursdayV = getBooleanDay(c.getThursday());
            int fridayV = getBooleanDay(c.getFriday());
            int saturdayV = getBooleanDay(c.getSaturday());
            String professorV = c.getProfessor();
            int crnV = c.getCrn();
            int gradeV = c.getGrade();
            int creditV = c.getCredits();

            String SQL_INSERT = "INSERT INTO userclasses(idUserClasses,ClassName,Subject,Time,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Professor,CRN,Grade,Credits) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement prepStat= conn.prepareStatement(SQL_INSERT);

            System.out.println("Trying 2..Connecting to a selected database..." + prepStat);

            prepStat.setInt(1,idUserClassesV);
            prepStat.setString(2,ClassNameV);
            prepStat.setString(3,subjectV);
            prepStat.setString(4,timeV);
            prepStat.setInt(5,mondayV);
            prepStat.setInt(6,tuesdayV);
            prepStat.setInt(7,wednesdayV);
            prepStat.setInt(8,thursdayV);
            prepStat.setInt(9,fridayV);
            prepStat.setInt(10,saturdayV);
            prepStat.setString(11,professorV);
            prepStat.setInt(12,crnV);
            prepStat.setInt(13,gradeV);
            prepStat.setInt(14,creditV);

            int row = prepStat.executeUpdate();

            if (row> 0) {
                System.out.println("A new class was inserted successfully!");
                access = true;
            }

            conn.close();

        }catch(Exception e)
        {
            System.out.println(e);
        }

        return access;
    }

    public Boolean DeleteRow(String deleteN,String tableN, String isEqual) {
        Boolean didIt = false;
        try{
            Class.forName(classForName);
            Connection conn = DriverManager.getConnection(sqlUrl, user, password);

            String sql = "DELETE FROM " + tableN  + " WHERE " + deleteN + " =?";


            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, isEqual);

            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println(" delete successfully!");
                didIt =true;
            }
        }catch(Exception e)
        {
            System.out.println(e);

        }
        return didIt;
    }

    public static Boolean ifDay(int i)
    {
        if(i == 1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public ArrayList<ArrayList> getClasses(int Id, String table)
    {
        List<ArrayList> myClasses = new ArrayList<ArrayList>();
        try{

            Class.forName(classForName);
            Connection conn = DriverManager.getConnection(sqlUrl,user,password);

            Statement stat = null;
            ResultSet result = null;

            stat = conn.createStatement();
            result = stat.executeQuery("SELECT * FROM " + table);

            while (result.next())
            {
                if (result.getInt("idUserClasses")== Id)
                {
                    List<String> indexClass = new ArrayList<String>();
                    indexClass.add(result.getString("ClassName"));
                    indexClass.add(result.getString("Subject"));
                    indexClass.add(result.getString("Time"));

                    if (ifDay(result.getInt("Monday")))
                    {
                        indexClass.add("&#10003;");
                    }else
                    {
                        indexClass.add(" ");
                    }
                    if (ifDay(result.getInt("Tuesday")))
                    {
                        indexClass.add("&#10003;");
                    }else
                    {
                        indexClass.add(" ");
                    }

                    if (ifDay(result.getInt("Wednesday")))
                    {
                        indexClass.add("&#10003;");
                    }else
                    {
                        indexClass.add(" ");
                    }

                    if (ifDay(result.getInt("Thursday")))
                    {
                        indexClass.add("&#10003;");
                    }else
                    {
                        indexClass.add(" ");
                    }

                    if (ifDay(result.getInt("Friday")))
                    {
                        indexClass.add("&#10003;");
                    }else
                    {
                        indexClass.add(" ");
                    }

                    if (ifDay(result.getInt("Saturday")))
                    {
                        indexClass.add("&#10003;");
                    }else
                    {
                        indexClass.add(" ");
                    }

                    indexClass.add(result.getString("Professor"));
                    indexClass.add(Integer.toString(result.getInt("CRN")));
                    indexClass.add(Integer.toString(result.getInt("Grade")));
                    indexClass.add(Integer.toString(result.getInt("Credits")));
                    myClasses.add((ArrayList) indexClass);
                }
            }
            return (ArrayList<ArrayList>) myClasses;
        }
        catch (Exception e)
        {
            System.err.println("SQLException in Query.java");
            return (ArrayList<ArrayList>) myClasses;
        }

    }
    public String getClassForName() {
         return classForName;
     }

     public String getSqlUrl() {
         return sqlUrl;
     }
 }
