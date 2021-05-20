package com.example.loginregister.ui.home.utils;

import android.content.Context;

import com.example.loginregister.ui.home.bean.DateInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Utils {

    public static SimpleDateFormat monthFormat = new SimpleDateFormat("M");
    public static SimpleDateFormat dayFormat = new SimpleDateFormat("d");
    public static SimpleDateFormat weekFormat = new SimpleDateFormat("EEE", Locale.ENGLISH);
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d", Locale.ENGLISH);

    public static List<DateInfo> getDate() {
        List<DateInfo> list = new ArrayList<DateInfo>();
        for (int i = 0; i <= 10; i++) {
            DateInfo dateInfo = new DateInfo();
            Calendar mCalendar = Calendar.getInstance(); // Creating a Calendar object
            mCalendar.set(Calendar.DATE, mCalendar.get(Calendar.DATE) + i);// The date seven days prior to the current date
            String month = monthFormat.format(mCalendar.getTime());
            String day = dayFormat.format(mCalendar.getTime());
            String week = weekFormat.format(mCalendar.getTime());
            String date = dateFormat.format(mCalendar.getTime());
            dateInfo.setMonth(month);
            dateInfo.setDay(day);
            dateInfo.setWeek(week);
            dateInfo.setDate(date);
            list.add(dateInfo);
        }
        return list;
    }

    public static void main(String[] args) {
        List<DateInfo> list = getDate();
        for (DateInfo dateInfo : list) {
            System.out.println(dateInfo.getDay());
            System.out.println(dateInfo.getWeek());
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}