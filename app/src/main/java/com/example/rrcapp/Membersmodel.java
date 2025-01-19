package com.example.rrcapp;

public class Membersmodel {
    private String username;
    private String emailID;

    public Membersmodel() {

    }

    public Membersmodel(String username, String emailID) {
        this.username = username;
        this.emailID = emailID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }
}
