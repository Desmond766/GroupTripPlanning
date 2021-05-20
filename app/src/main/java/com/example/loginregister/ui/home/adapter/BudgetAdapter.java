package com.example.loginregister.ui.home.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.example.loginregister.R;
import com.example.loginregister.ui.home.bean.Budget;

import java.util.List;

public class BudgetAdapter extends BaseAdapter {

    private List<Budget> lists;
    private Context mContext;

    public BudgetAdapter(Context context, List<Budget> lists) {
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
            convertView = View.inflate(mContext, R.layout.item_checked, null);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        Budget budget = lists.get(position);
        holder.checkbox.setText(budget.getPrice());
        holder.checkbox.setChecked(budget.isChecked());
        return convertView;
    }

    public class Holder {
        CheckBox checkbox;
    }
}
