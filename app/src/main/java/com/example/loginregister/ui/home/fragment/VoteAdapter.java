package com.example.loginregister.ui.home.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.R;

import java.util.ArrayList;

public class VoteAdapter extends RecyclerView.Adapter<VoteAdapter.VoteHolder> {
    private LayoutInflater mInflater;
    private ArrayList<VoteInfo> votes;
    private int checkedPosition = -1;


    public class VoteHolder extends RecyclerView.ViewHolder {
        TextView voteDescription;
        TextView voteCount;
        ImageView checked;
        VoteHolder(View itemView) {
            super(itemView);
            voteDescription = itemView.findViewById(R.id.voteDescription);
            voteCount  = itemView.findViewById(R.id.voteCount);
            checked = itemView.findViewById(R.id.check);
        }
        void bind(final VoteInfo vote){
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

    public VoteAdapter(Context context, ArrayList<VoteInfo> votes){
        this.mInflater = LayoutInflater.from(context);
        this.votes = votes;

    }
    public void setVotes(ArrayList<VoteInfo> votes){
        this.votes = new ArrayList<>();
        this.votes = votes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.voteitem,parent,false);
        return new VoteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoteHolder holder, int position) {
        String description = votes.get(position).getDescription();
        int count = (int)votes.get(position).getCount();
        holder.voteDescription.setText(description);
        holder.voteCount.setText(Integer.toString(count));
        holder.bind(votes.get(position));
    }

    @Override
    public int getItemCount() {
        return votes.size();
    }
    public VoteInfo getSelected(){
        if (checkedPosition != -1){
            return votes.get(checkedPosition);
        }
        return null;
    }

}

