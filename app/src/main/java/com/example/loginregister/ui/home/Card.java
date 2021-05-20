package com.example.loginregister.ui.home;

public class Card {
    private String title;
    private int number;
    public String ownerId;

    public Card(String title){
        this.title = title;

    }
    public String getOwnerID(){
        return this.ownerId;
    }
    public void setOwnerID(String ownerID){
        this.ownerId = ownerID;
    }


    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setnum(int i){
        this.number = i;
    }

    public int getNumber() {
        return number;
    }
}
