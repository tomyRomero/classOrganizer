package com.example.classorganizer;



public class myClass {
    private int userId;
    private String className;
    private String classSubject;
    private String time;
    private Boolean monday;
    private Boolean tuesday;
    private Boolean wednesday;
    private Boolean thursday;
    private Boolean friday;
    private Boolean saturday;
    private int crn;
    private int grade;
    private String professor;
    private int credits;


    public myClass(int myUserId, String myClassName, String myClassSubject, String myTime, Boolean myDay1, Boolean myDay2, Boolean myDay3, Boolean myDay4, Boolean myDay5, Boolean myDay6, String myPro, int myCrn, int myGrade , int credits)
    {
        this.userId = myUserId;
        this.className = myClassName;
        this.classSubject = myClassSubject;
        this.time = myTime;
        this.monday = myDay1;
        this.tuesday = myDay2;
        this.wednesday = myDay3;
        this.thursday = myDay4;
        this.friday = myDay5;
        this.saturday = myDay6;
        this.crn = myCrn;
        this.grade = myGrade;
        this.professor = myPro;
        this.credits = credits;
    }



    public String getClassSubject() {
        return classSubject;
    }

    public Boolean getFriday() {
        return friday;
    }

    public Boolean getMonday() {
        return monday;
    }

    public Boolean getTuesday() {
        return tuesday;
    }

    public Boolean getWednesday() {
        return wednesday;
    }

    public Boolean getThursday() {
        return thursday;
    }

    public Boolean getSaturday() {
        return saturday;
    }

    public String getTime() {
        return time;
    }

    public int getUserId() {
        return userId;
    }

    public String getClassName() {
        return className;
    }

    public int getGrade() {
        return grade;
    }

    public int getCrn() {
        return crn;
    }

    public String getProfessor() {
        return professor;
    }

    public int getCredits() {
        return credits;
    }
}
