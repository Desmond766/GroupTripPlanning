package com.example.loginregister.ui.home.fragment;

public class VoteInfo {
    private String description;
    private long count;


    public String getDescription(){
        return description;
    }
    public long getCount(){
        return count;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public void setCount(long count){
        this.count = count;
    }
}
