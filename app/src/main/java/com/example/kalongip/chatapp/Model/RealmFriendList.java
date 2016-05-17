package com.example.kalongip.chatapp.Model;

import io.realm.RealmObject;

/**
 * Created by kalongip on 17/5/16.
 */
public class RealmFriendList extends RealmObject {
  private int key;
  private String aFriend;

  public RealmFriendList() {
  }

  public RealmFriendList(int key, String aFriend) {
    this.key = key;
    this.aFriend = aFriend;
  }

  public int getKey() {
    return key;
  }

  public void setKey(int key) {
    this.key = key;
  }

  public String getaFriend() {
    return aFriend;
  }

  public void setaFriend(String aFriend) {
    this.aFriend = aFriend;
  }
}
