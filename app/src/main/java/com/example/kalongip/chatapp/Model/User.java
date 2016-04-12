package com.example.kalongip.chatapp.Model;

/**
 * Created by kalongip on 12/4/16.
 */
public class User {
    public User(String username){
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;
}
