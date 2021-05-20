package com.example.loginregister.ui.home.fragment;

public class Activity {
    public String date;
    public String title;
    public String description;
    public String startTime;
    public String endTime;
    public Integer likes;
    public Integer disLikes;
    public double estimatedCost;

    public Activity(){

    }
    public Activity(String date, String title, String description, String startTime, String endTime, Integer likes, Integer disLikes, double estimatedCost ){
        this.date = date;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.likes = likes;
        this.disLikes = disLikes;
        this.estimatedCost = estimatedCost;
    }

}
