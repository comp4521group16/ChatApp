package com.example.kalongip.chatapp.Value;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.kalongip.chatapp.Model.User;

/**
 * Created by kalongip on 11/4/16.
 */
public class Cache {
    private Context context;

    public Cache(Context context){
        this.context = context;
    }

    public void setUser(User user) {
        String username = user.getUsername();
        String uid = user.getUid();
        SharedPreferences prefs = context.getSharedPreferences("SHARE_PREFERENCES", Context.MODE_PRIVATE);
        prefs.edit().putString("UserName", username).apply();
        prefs.edit().putString("UID", uid).apply();
    }

    public User getUser() {
        SharedPreferences prefs = context.getSharedPreferences("SHARE_PREFERENCES", Context.MODE_PRIVATE);
        String username =  prefs.getString("UserName", null);
        String uid = prefs.getString("UID", null);
        User result = new User(username, uid);
        return result;
    }

    public void setLoggedIn(boolean loggedIn){
        SharedPreferences prefs = context.getSharedPreferences("SHARE_PREFERENCES", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("loggedIn", loggedIn).apply();
    }

    public Boolean getLoggedIn() {
        boolean loggedIn;
        SharedPreferences prefs = context.getSharedPreferences("SHARE_PREFERENCES", Context.MODE_PRIVATE);
        loggedIn = prefs.getBoolean("loggedIn", false);
        return loggedIn;
    }

    public void clearUser(){
        SharedPreferences prefs = context.getSharedPreferences("SHARE_PREFERENCES", Context.MODE_PRIVATE);
        prefs.edit().clear();
        prefs.edit().apply();
    }
}
