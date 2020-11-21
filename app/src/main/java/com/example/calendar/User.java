package com.example.calendar;

public class User {

    private long userID;
    private String name;
    private String email;
    private String password;

    public User() {}

    public User(long userID, String name, String email, String password) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void setUserID(long userID) { this.userID = userID; }
    public long getUserID() { return userID; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return email; }

    public void setPassword(String password) { this.password = password; }
    public String getPassword() { return password; }
}
