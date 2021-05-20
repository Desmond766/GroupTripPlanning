package com.example.loginregister.ui.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.R;
import com.example.loginregister.ui.home.bean.GroupUser;

import java.util.ArrayList;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberHolder> {
    private int checkedPosition = -1;
    private LayoutInflater mInflater;
    private ArrayList<GroupUser> members;

    public MemberAdapter(Context context, ArrayList<GroupUser> members){

        this.mInflater = LayoutInflater.from(context);
        this.members = members;
    }


    @NonNull
    @Override
    public MemberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.memberitem,parent,false);
        return new MemberHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberHolder holder, int position) {
        GroupUser member = members.get(position);
        String username = member.getUsername();
        String uid = member.getUid();
        holder.username.setText(username);
        holder.bind(members.get(position));
    }

    @Override
    public int getItemCount() {
        return members.size();
    }
    public GroupUser getSelected(){
        if (checkedPosition != -1){
            return members.get(checkedPosition);
        }
        return null;
    }

    public class MemberHolder extends RecyclerView.ViewHolder{
        TextView username;
        ImageView checked;
        MemberHolder(View itemView){
            super(itemView);
            username = itemView.findViewById(R.id.memusername);
            checked = itemView.findViewById(R.id.ccheck2);
        }

        void bind(final GroupUser mem){
            if(checkedPosition == -1){
                checked.setBackgroundResource(R.drawable.unselected_icon);
            }else{
                if (checkedPosition == getAdapterPosition()){
                    checked.setBackgroundResource(R.drawable.selected_icon);
                }else{
                    checked.setBackgroundResource(R.drawable.unselected_icon);
                }
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checked.setBackgroundResource(R.drawable.selected_icon);
                    if(checkedPosition != getAdapterPosition()){
                        notifyItemChanged(checkedPosition);
                        checkedPosition = getAdapterPosition();

                    }
                }
            });
        }
    }
}
