package com.example.loginregister.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;

public class DashboardViewModel extends ViewModel {

//    private String location;
//    private Date startDate;
//    private Date endDate;
//
//
//    public DashboardViewModel(String location,Date startDate,Date endDate) {
//        this.location = location;
//        this.startDate = startDate;
//        this.endDate = endDate;
//
//    }
//    public String getLocation(){
//        return location;
//    }
//
//    public Date getStartDate() {
//        return startDate;
//    }
//    public Date getEndDate(){
//        return endDate;
//    }
private MutableLiveData<String> mText;

    public DashboardViewModel() {
//        mText = new MutableLiveData<>();
//        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}