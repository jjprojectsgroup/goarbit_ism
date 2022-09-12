package com.example.goarbit_ism.ui.user;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User {

    private static String name;
    private static String lastName;
    private static String userName;
    private static String email;
    private static String tYc;
    private static String date;
    private static String photoUrl;

    public Map<String, Boolean> stars = new HashMap<>();

    public User() {
        // ...
    }

    public User(String name, String lastName, String userName, String email, String tYc, String date, String photoUrl) {
        User.name = name;
        User.lastName = lastName;
        User.userName = userName;
        User.email = email;
        User.tYc = tYc;
        User.date = date;
        User.photoUrl = photoUrl;
    }

    public void updateUser(String name, String lastName, String userName) {
        User.name = name;
        User.lastName = lastName;
        User.userName = userName;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("lastName", lastName);
        result.put("userName", userName);
        result.put("email", email);
        result.put("tYc", tYc);
        result.put("date", date);
        result.put("photoUrl", photoUrl);

        return result;
    }

    public static String getName() {
        return name;
    }

    public void setName(String name) {
        User.name = name;
    }

    public static String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        User.lastName = lastName;
    }

    public static String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        User.userName = userName;
    }

    public static String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        User.email = email;
    }

    public static String gettYc() {
        return tYc;
    }

    public void settYc(String tYc) {
        User.tYc = tYc;
    }

    public static String getDate() {
        return date;
    }

    public void setDate(String date) {
        User.date = date;
    }

    public static String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        User.photoUrl = photoUrl;
    }

}
