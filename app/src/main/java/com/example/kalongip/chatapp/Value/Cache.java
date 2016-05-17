package com.example.kalongip.chatapp.Value;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.kalongip.chatapp.Model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalongip on 11/4/16.
 */
public class Cache {
    private static final String TAG = Cache.class.getSimpleName();
    private Context context;

    public Cache(Context context){
        this.context = context;
    }

    public void setUser(User user) {
        String email = user.getEmail();
        String username = user.getUsername();
        String uid = user.getUid();
        List<String> friends = user.getFriends();
        SharedPreferences prefs = context.getSharedPreferences("SHARE_PREFERENCES", Context.MODE_PRIVATE);
        prefs.edit().putString("Email", email).apply();
        prefs.edit().putString("UserName", username).apply();
        prefs.edit().putString("UID", uid).apply();
//        Set<String> set = new HashSet<String>();
//        set.addAll(friends);
//        prefs.edit().putStringSet("FriendList", set);
        prefs.edit().putInt("FriendListSize", friends.size());
        for (int i = 0; i < friends.size(); i++){
            String key = "friend" + i;
            String fd = friends.get(i);
            prefs.edit().putString(key, fd).apply();
        }
        Log.d(TAG, "Set fd list: " + friends);
    }

    public User getUser() {
        SharedPreferences prefs = context.getSharedPreferences("SHARE_PREFERENCES", Context.MODE_PRIVATE);
        String email =  prefs.getString("Email", null);
        String username =  prefs.getString("UserName", null);
        String uid = prefs.getString("UID", null);
        int length = prefs.getInt("FriendListSize", 0);
        List<String> friends = new ArrayList<String>();
        for (int i = 0; i < length; i++){
            String key = "friend" + i;
            String fd = prefs.getString(key, null);
            friends.add(i, fd);
        }
//        Set<String> set = prefs.getStringSet("FriendList", null);
//        List<String> friends = (List<String>) set;
        Log.d(TAG, "Set fd list: " + friends);
        User result = new User(email, uid, username, friends);
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
