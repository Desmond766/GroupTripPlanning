package com.example.loginregister.ui.home.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.loginregister.R;
import com.example.loginregister.ui.home.bean.DateInfo;
import com.example.loginregister.ui.home.view.ListViewInScrollView;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DateInfoAdapter extends BaseAdapter {

    private List<DateInfo> lists;
    private Context mContext;
    private FirebaseDatabase mDatabase;
    public String userid;


    public DateInfoAdapter(Context context, List<DateInfo> lists, String tripID, String userID) {
        mContext = context;
        this.lists = lists;
        this.userid = userID;
        mDatabase = FirebaseDatabase.getInstance();
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
            convertView = View.inflate(mContext, R.layout.item_date_info, null);
            holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
            holder.llNone = (LinearLayout) convertView.findViewById(R.id.ll_none);
            holder.listView = (ListViewInScrollView) convertView.findViewById(R.id.list_view);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        DateInfo dateInfo = lists.get(position);
        if (dateInfo != null) {
            holder.tvDate.setText(dateInfo.getDate());
            if (!dateInfo.getNodes().isEmpty()) {
                holder.llNone.setVisibility(View.GONE);
                holder.listView.setAdapter(new NoteInfoAdapter(mContext, dateInfo.getNodes(),userid));
            } else {
                holder.llNone.setVisibility(View.VISIBLE);
                holder.listView.setAdapter(null);
            }
            convertView.findViewById(R.id.ll_add).setOnClickListener(v -> {
                if (onChildClickListener != null) {
                    onChildClickListener.onClick(dateInfo);
                }
            });
        }
        return convertView;
    }

    public class Holder {
        TextView tvDate;
        LinearLayout llNone;
        ListViewInScrollView listView;
    }

    private OnChildClickListener onChildClickListener;

    public void setOnChildClickListener(OnChildClickListener onChildClickListener) {
        this.onChildClickListener = onChildClickListener;
    }

    public interface OnChildClickListener {
        void onClick(DateInfo dateInfo);
    }

}