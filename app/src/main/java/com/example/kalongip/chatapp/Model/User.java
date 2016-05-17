package com.example.kalongip.chatapp.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalongip on 12/4/16.
 */
public class User {
    private String email;
    private String uid;
    private String username;
    private List<String> friends = new ArrayList<>();

    public User() {
    }

    public User(String email, String uid, String username, List<String> friends) {
        this.email = email;
        this.uid = uid;
        this.username = username;
        this.friends = friends;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    @Override
    public String toString() {
        return "User{" +
            "email='" + email + '\'' +
            ", uid='" + uid + '\'' +
            ", username='" + username + '\'' +
            ", friends=" + friends +
            '}';
    }
}
