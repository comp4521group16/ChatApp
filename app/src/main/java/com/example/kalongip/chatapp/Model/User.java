package com.example.kalongip.chatapp.Model;

/**
 * Created by kalongip on 12/4/16.
 */
public class User {
    private String email;
    private String uid;
    private String username;

    public User() {
    }

    public User(String email, String username, String uid) {
        this.email = email;
        this.username = username;
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "User{" +
            "email='" + email + '\'' +
            ", uid='" + uid + '\'' +
            ", username='" + username + '\'' +
            '}';
    }
}
