package com.example.goarbit_ism.ui.user;

public class User {
    public String name;
    public String lastName;
    public String userName;
    public String email;
    public String tYc;
    public String date;

    public String photoUrl;

    public User() {
        // ...
    }

    public User(String name, String lastName, String userName, String email, String tYc, String date, String photoUrl) {
        this.name = name;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.tYc = tYc;
        this.date = date;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String gettYc() {
        return tYc;
    }

    public void settYc(String tYc) {
        this.tYc = tYc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

}
