package com.example.kalongip.chatapp.Model;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by timothy on 8/4/2016.
 */
public class RealmMessages extends RealmObject {
    private String sender,receiver,content;
    private boolean fromMe, isPhoto;
    private Date date;

    public RealmMessages(){

    }
    public RealmMessages(String sender, String receiver, String content, boolean fromMe, boolean isPhoto, Date date) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.fromMe = fromMe;
        this.isPhoto = isPhoto;
        this.date = date;
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

    public boolean isFromMe() {
        return fromMe;
    }

    public void setFromMe(boolean fromMe) {
        this.fromMe = fromMe;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isPhoto() {
        return isPhoto;
    }

    public void setIsPhoto(boolean isPhoto) {
        this.isPhoto = isPhoto;
    }
}
