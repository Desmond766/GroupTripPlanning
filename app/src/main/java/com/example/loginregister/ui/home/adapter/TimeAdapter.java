package com.example.loginregister.ui.home.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.loginregister.R;
import com.example.loginregister.ui.home.bean.DateInfo;

import java.util.List;

public class TimeAdapter extends BaseAdapter {

    private List<DateInfo> lists;
    private Context mContext;
    private String date;

    public void setDate(String date) {
        this.date = date;
    }

    public TimeAdapter(Context context, List<DateInfo> lists) {
        mContext = context;
        this.lists = lists;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_time, null);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        DateInfo dateInfo = lists.get(position);
        if (dateInfo != null) {
            holder.tvTime.setText(dateInfo.getWeek() + "," + dateInfo.getMonth() + "/" + dateInfo.getDay());
            if (date.equals(dateInfo.getDate())) {
                holder.tvTime.setTextColor(mContext.getResources().getColor(R.color.color_white));
                holder.tvTime.setBackgroundResource(R.drawable.drawable_shape_black);
            } else {
                holder.tvTime.setTextColor(mContext.getResources().getColor(R.color.color_content));
                holder.tvTime.setBackgroundResource(R.drawable.drawable_shape_gray);
            }
        }
        return convertView;
    }

    public class Holder {
        TextView tvTime;
    }

}