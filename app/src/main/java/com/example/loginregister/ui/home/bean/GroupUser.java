package com.example.loginregister.ui.home.bean;

public class GroupUser{
    public String uid;
    public String username;
    public GroupUser(){

    }
    public GroupUser(String uid,String username){
        this.uid = uid;
        this.username = username;
    }
    public String getUid(){
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
