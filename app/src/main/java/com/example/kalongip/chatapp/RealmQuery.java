package com.example.kalongip.chatapp;

import android.content.Context;

import com.example.kalongip.chatapp.Model.RealmFriendList;
import com.example.kalongip.chatapp.Model.RealmMessages;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * This class handles all the queries towards the local DB
 */
public class RealmQuery {
    Realm realm;

    public RealmQuery(Context context){
        realm = Realm.getInstance(context);
    }

    public RealmResults<RealmMessages> retrieveChatHistoryByUserName(String friend){
        return realm.where(RealmMessages.class).beginGroup()
                .equalTo("sender", friend).or().equalTo("receiver", friend).endGroup().findAll();
    }

    public RealmResults<RealmFriendList> retrieveFriendList(){
        return realm.where(RealmFriendList.class).findAll();
    }

}
