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
        String userName = user.getUsername();
        SharedPreferences prefs = context.getSharedPreferences("SHARE_PREFERENCES", Context.MODE_PRIVATE);
        prefs.edit().putString("UserName", userName).apply();
    }

    public User getUser() {
        SharedPreferences prefs = context.getSharedPreferences("SHARE_PREFERENCES", Context.MODE_PRIVATE);
        String userName =  prefs.getString("UserName", null);
        User result = new User(userName);
        return result;
    }
}
