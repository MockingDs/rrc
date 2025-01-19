package com.example.rrcapp;

public class MemberDetails {
    private String emailID;
    private String username;
    private String ismember;
    private int present;
    private int absent;

    public MemberDetails() {

    }

    public MemberDetails(String emailID, String username, String ismember, int present, int absent) {
        this.emailID = emailID;
        this.username = username;
        this.ismember = ismember;
        this.present = present;
        this.absent = absent;
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

    public String getIsmember() {
        return ismember;
    }

    public void setIsmember(String ismember) {
        this.ismember = ismember;
    }

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
}
