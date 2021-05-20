package com.example.loginregister.ui.home.bean;

import java.util.ArrayList;
import java.util.List;

public class DateInfo {

    private String date;
    private String year;
    private String month;
    private String week;
    private String day;
    private List<com.example.loginregister.ui.home.bean.NoteInfo> nodes = new ArrayList<>();

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getYear() {
        return year;
    }
    public void setYear(String year) {
        this.year = year;
    }
    public String getMonth() {
        return month;
    }
    public void setMonth(String month) {
        this.month = month;
    }
    public String getWeek() {
        return week;
    }
    public void setWeek(String week) {
        this.week = week;
    }
    public String getDay() {
        return day;
    }
    public void setDay(String day) {
        this.day = day;
    }

    public List<com.example.loginregister.ui.home.bean.NoteInfo> getNodes() {
        return nodes;
    }

    public void setNodes(List<com.example.loginregister.ui.home.bean.NoteInfo> nodes) {
        this.nodes = nodes;
    }
}