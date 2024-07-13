package com.example.reminderapp_todo;

import android.os.Parcel;
import android.os.Parcelable;

public class Reminder implements Parcelable {
    private String id;
    private String title;
    private String date;
    private String time;
    private boolean isDone;

    public Reminder() {
        // Public no-arg constructor needed for Firestore
    }

    public Reminder(String title, String date, String time) {
        this.title = title;
        this.date = date;
        this.time = time;
    }

    protected Reminder(Parcel in) {
        id = in.readString();
        title = in.readString();
        date = in.readString();
        time = in.readString();
        isDone = in.readByte() != 0;
    }

    public static final Creator<Reminder> CREATOR = new Creator<Reminder>() {
        @Override
        public Reminder createFromParcel(Parcel in) {
            return new Reminder(in);
        }

        @Override
        public Reminder[] newArray(int size) {
            return new Reminder[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeByte((byte) (isDone ? 1 : 0));
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
