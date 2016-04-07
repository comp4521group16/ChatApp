package com.example.kalongip.chatapp.Model;

import io.realm.RealmObject;

/**
 * Created by timothy on 8/4/2016.
 */
public class RealmMessages extends RealmObject {
    private String sender,receiver,content;

    public RealmMessages(){

    }
    public RealmMessages(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
