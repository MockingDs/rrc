package com.example.rrcapp;

public class Mdatamodel {
    private String emailId;
    private String userName;
    private boolean checkboxVisible;
    private boolean isChecked;

    public Mdatamodel() {

    }

    public Mdatamodel(String emailId, String userName) {
        this.emailId = emailId;
        this.userName = userName;
    }


    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getUserName() {

        return userName;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

    public boolean isCheckboxVisible() {

        return checkboxVisible;
    }

    public void setCheckboxVisible(boolean checkboxVisible) {
        this.checkboxVisible = checkboxVisible;
    }

    public boolean isChecked() {

        return isChecked;
    }

    public void setChecked(boolean checked) {

        isChecked = checked;
    }
}
