package com.example.classorganizer;

class Customer {
    private int id;
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private int age;
    private String password;

    public Customer(int id, String fn, String ln, String email, int age, String username, String password)
    {
        this.id = id;
        this.firstname = fn;
        this.lastname = ln;
        this.email = email;
        this.age = age;
        this.password =password;
        this.username =username;
    }

    public int getId()
    {
        return id;
    }

    public String getFirstName()
    {
        return this.firstname;
    }

    public String getLastName()
    {
        return lastname;
    }

    public String getEmail()
    {
        return email;
    }

    public int getAge()
    {
        return age;
    }

    public String getPassword()
    {
        return password;
    }

    public void setId(int id)
    {
        this.id =id;
    }

    public void setFirstName(String fname)
    {
        this.firstname =fname;
    }

    public void setLastName(String lname)
    {
        this.lastname =lname;
    }

    public void setEmail(String eml)
    {
        this.email=eml;
    }

    public void setPassWord(String pass)
    {
        this.password=pass;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public String getUsername()
    {
        return this.username;
    }
}
