package com.example.rrcapp;

public class Attendancedata {
    private String emailID;
    private String username;
    private boolean isSelected;

    private  int present;
    private int absent;

    public int getPresent() {
        return present;
    }

    public void setPresent(int present) {
        this.present = present;
    }

    public int getAbsent() {
        return absent;
    }

    public void setAbsent(int absent) {
        this.absent = absent;
    }

    public Attendancedata(String emailID, String username, int present, int absent) {
        this.emailID = emailID;
        this.username = username;
        this.present = present;
        this.absent = absent;
        this.isSelected = false;
    }


    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
