package com.example.kalongip.chatapp;

import android.content.Context;
import android.util.Log;

import com.example.kalongip.chatapp.Model.RealmFriendList;
import com.example.kalongip.chatapp.Model.RealmMessages;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * This class handles all the queries towards the local DB
 */
public class RealmQuery {
    Realm realm;
    private static final String TAG = "RealmQuery";
    public RealmQuery(Context context){
        realm = Realm.getInstance(context);
    }

    public RealmResults<RealmMessages> retrieveChatHistoryByUserName(String friend){
        Log.i(TAG, "Query Name: " + friend);
        RealmResults<RealmMessages> realmResults = realm.where(RealmMessages.class).beginGroup()
                .equalTo("sender", friend).or().equalTo("receiver", friend).endGroup().findAll();
        return realmResults;
    }

    public RealmResults<RealmFriendList> retrieveFriendList(){
        return realm.where(RealmFriendList.class).findAll();
    }

}
