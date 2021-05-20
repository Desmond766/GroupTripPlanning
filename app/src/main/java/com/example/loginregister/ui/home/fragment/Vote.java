package com.example.loginregister.ui.home.fragment;

public class Vote {
    public String description;
    public long count;
    public boolean isChecked = false;
    public Vote(){

    }
    public Vote(String title, int count){
        this.description = title;
        this.count = count;
    }
    public boolean isChecked(){
        return  isChecked;
    }

    public void setChecked(boolean checked){
        isChecked = checked;
    }
}
