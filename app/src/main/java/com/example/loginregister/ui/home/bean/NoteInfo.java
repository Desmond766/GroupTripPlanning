package com.example.loginregister.ui.home.bean;

public class NoteInfo {

    private String location;
    private String date;
    private String startTime;
    private String endTime;
    private String cost;
    private String tripID;
    private int likes;
    private int dislikes;

    public  void  setTripID(String id){
        tripID = id;
    }
    public String  getTripID(){
        return  tripID;
    }

    public void setDate(String date){
        this.date = date;
    }
    public String getDate(){
        return  date;
    }
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
    public boolean isEmpty(){
        if (endTime == null){
            return true;
        }
        return false;
    }
    public void  setLikes(int likes){
        this.likes = likes;
    }
    public int getLikes(){
        return  likes;
    }
    public void setDislikes(int dislikes){
        this.dislikes = dislikes;
    }

    public int getDislikes(){
        return dislikes;
    }

}
