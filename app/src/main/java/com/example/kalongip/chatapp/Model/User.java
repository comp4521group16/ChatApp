package com.example.kalongip.chatapp.Model;

/**
 * Created by kalongip on 12/4/16.
 */
public class User {
    private String username;
    private String uid;


    public User(String uid) {
        this.uid = uid;
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
}
