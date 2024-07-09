package com.example.reminderapp_todo;

public class Reminder {
    private String title;
    private String dateTime;

    public Reminder(String title, String dateTime) {
        this.title = title;
        this.dateTime = dateTime;
    }

    public String getTitle() {
        return title;
    }

    public String getDateTime() {
        return dateTime;
    }
}
