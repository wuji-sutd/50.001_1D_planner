package com.example.a50001_1d_planner;


public class Task {

    private long taskID;
    private long userID; // Declaring task using user ID in case if two persons have the same name
    private String title;
    private String estHours;
    private String dueDate;
    private String weeklyR;

    public Task() {}

    public Task(long taskID, String title, String estHours, String dueDate, String weeklyR) {
        this.taskID = taskID;
        this.title = title;
        this.estHours = estHours;
        this.dueDate = dueDate;
        this.weeklyR = weeklyR;
    }

    public void setTaskID(long taskID) { this.taskID = taskID; }
    public long getTaskID() { return taskID; }

    public void setUserID(long userID) { this.userID = userID; }
    public long getUserID() { return userID; }

    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }

    public void setEstHours(String estHours) { this.estHours = estHours; }
    public String getEstHours() { return estHours; }

    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public String getDueDate() { return dueDate; }

    public void setWeeklyR(String weeklyR) { this.weeklyR = weeklyR; }
    public String getWeeklyR() { return weeklyR; }
}