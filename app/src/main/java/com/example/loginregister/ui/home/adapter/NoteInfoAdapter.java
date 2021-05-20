package com.example.loginregister.ui.home.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.loginregister.R;
import com.example.loginregister.ui.home.bean.NoteInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NoteInfoAdapter extends BaseAdapter {

    private List<NoteInfo> lists;
    private Context mContext;
    private FirebaseDatabase mDataBase;
    public String userid;
    public NoteInfoAdapter(Context context, List<NoteInfo> lists, String userid) {
        mContext = context;
        this.lists = lists;
        this.userid = userid;
        mDataBase = FirebaseDatabase.getInstance();
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
            convertView = View.inflate(mContext, R.layout.item_note_info, null);
            holder.tvLocation = (TextView) convertView.findViewById(R.id.tv_location);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tvCost = (TextView) convertView.findViewById(R.id.tv_cost);
            holder.delete = convertView.findViewById(R.id.delete);
            holder.like = convertView.findViewById(R.id.like);
            holder.dislike = convertView.findViewById(R.id.dislike);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        NoteInfo noteInfo = lists.get(position);
        if (noteInfo != null) {
            String date = noteInfo.getDate();
            String tripid = noteInfo.getTripID();
            String activityId = tripid + noteInfo.getLocation();

            DatabaseReference temp = mDataBase.getReference().child("trip").child(tripid).child("Day").child(date).child(activityId);

            holder.tvLocation.setText(noteInfo.getLocation());
//            holder.tvLocation.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    HashMap <String,Object> newNote = new HashMap<>();
//                    final View textEntryView = LayoutInflater.from(mContext).inflate(R.layout.edit_location, null);
//                    final EditText etLocation = (EditText) textEntryView.findViewById(R.id.el2);
//                    AlertDialog.Builder ad1 = new AlertDialog.Builder(mContext);
//                    ad1.setTitle("Add Notes");
//                    ad1.setIcon(android.R.drawable.ic_dialog_info);
//                    ad1.setView(textEntryView);
//                    ad1.setPositiveButton("Yes",(dialog,i) ->{
//                        String location = etLocation.getText().toString();
//                        if (location != null){
//                            newNote.put("Location",location);
//                        }
//                        temp.updateChildren(newNote);
//                    });
//                    ad1.setNegativeButton("No",(dialog,i) ->{
//                        dialog.dismiss();
//                    });
//                    ad1.show();
//                    Log.d("app","I long clik the button!!1");
//                    return false;
//                }
//            });
            holder.tvTime.setText(noteInfo.getStartTime() + "-" + noteInfo.getEndTime());
            holder.tvTime.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    HashMap <String,Object> newNote = new HashMap<>();
                    final View textEntryView = LayoutInflater.from(mContext).inflate(R.layout.edit_time, null);
                    final EditText etStartTime = (EditText) textEntryView.findViewById(R.id.es2);
                    final EditText etEndTime = (EditText) textEntryView.findViewById(R.id.ee2);
                    AlertDialog.Builder ad1 = new AlertDialog.Builder(mContext);
                    ad1.setTitle("Add Notes");
                    ad1.setIcon(android.R.drawable.ic_dialog_info);
                    ad1.setView(textEntryView);
                    ad1.setPositiveButton("Yes",(dialog,i) ->{
                        String startTime = etStartTime.getText().toString();
                        String endTime = etEndTime.getText().toString();
                        if (startTime != null){
                            newNote.put("startTime",startTime);
                        }
                        if (endTime != null){
                            newNote.put("endTime",endTime);
                        }
                        temp.updateChildren(newNote);
                    });
                    ad1.setNegativeButton("No",(dialog,i) ->{
                        dialog.dismiss();
                    });
                    ad1.show();

                    Log.d("app","I long clik the button!!1");
                    return false;
                }
            });
            //double oldCost =Double.parseDouble(holder.tvCost.getText().toString()) ;
            holder.dislike.setText(String.valueOf(noteInfo.getDislikes()));
            holder.like.setText(String.valueOf(noteInfo.getLikes()));
            holder.tvCost.setText(noteInfo.getCost());

            holder.tvCost.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    DatabaseReference temp2 = mDataBase.getReference().child("trip").child(tripid).child("cost");
                    //HashMap <String,Object> newCost = new HashMap<>();
                    HashMap <String,Object> newNote = new HashMap<>();
                    final View textEntryView = LayoutInflater.from(mContext).inflate(R.layout.edit_cost, null);
                    final EditText etCost = (EditText) textEntryView.findViewById(R.id.ec2);
                    AlertDialog.Builder ad1 = new AlertDialog.Builder(mContext);
                    ad1.setTitle("Add Notes");
                    ad1.setIcon(android.R.drawable.ic_dialog_info);
                    ad1.setView(textEntryView);
                    ad1.setPositiveButton("Yes",(dialog,i) ->{
                        String cost = etCost.getText().toString();
                        if (cost != null){
                            newNote.put("estimatedCost",cost);
                            temp2.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    double nCost = Double.parseDouble(task.getResult().getValue().toString())  + Double.parseDouble(cost);
                                    temp2.setValue(nCost);
                                    temp.updateChildren(newNote);
                                }
                            });
                        }
                        temp.updateChildren(newNote);
                    });
                    ad1.setNegativeButton("No",(dialog,i) ->{
                        dialog.dismiss();
                    });
                    ad1.show();
                    return false;
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mDataBase.getReference().child("trip").child(tripid).child("Day").child(date).child(activityId).setValue(null);
                    DatabaseReference allCosts = mDataBase.getReference().child("trip").child(tripid).child("cost");

                    allCosts.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            double totalCost = Double.parseDouble(task.getResult().getValue().toString());
                            allCosts.setValue(totalCost - Double.parseDouble(noteInfo.getCost()));
                        }
                    });
                }

            });
            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference likesInTrip = mDataBase.getReference().child("trip").child(tripid).child("Day").child(date).child(activityId).child("likes");
                    likesInTrip.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            int oldLikes = Integer.parseInt(task.getResult().getValue().toString());

                            DatabaseReference setLike = mDataBase.getReference().child("Likes").child(tripid).child(activityId);
                            setLike.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    ArrayList <String> likedPeople = new ArrayList<>();
                                    for (DataSnapshot ds : task.getResult().getChildren()){
                                        likedPeople.add(ds.getKey());
                                    }
                                    if (!likedPeople.contains(userid)){
                                        Log.d("app","The children count is" + task.getResult().getChildrenCount());
                                        likesInTrip.setValue(oldLikes + 1);
                                        setLike.child(userid).setValue(true);
                                        Log.d("app","I set the value");
                                    }
                                    else{
                                        Toast.makeText(mContext,"You have showed your attitude already.",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });
                }
            });
            holder.dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference dislikesIntrip = mDataBase.getReference().child("trip").child(tripid).child("Day").child(date).child(activityId).child("dislikes");
                    dislikesIntrip.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            int oldLikes = Integer.parseInt(task.getResult().getValue().toString());
                            DatabaseReference setLike = mDataBase.getReference().child("Likes").child(tripid).child(activityId);
                            setLike.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    ArrayList <String> dislikedPeople = new ArrayList<>();
                                    for (DataSnapshot ds : task.getResult().getChildren()){
                                        dislikedPeople.add(ds.getKey());
                                    }
                                    if (!dislikedPeople.contains(userid)){
                                        Log.d("app","The children count is" + task.getResult().getChildrenCount());
                                        dislikesIntrip.setValue(oldLikes + 1);
                                        setLike.child(userid).setValue(true);
                                        Log.d("app","I set the value");
                                    }
                                    else{
                                        Toast.makeText(mContext,"You have showed your attitude already.",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }
        return convertView;
    }

    public class Holder {
        TextView tvLocation;
        TextView tvTime;
        TextView tvCost;
        ImageView delete;
        TextView like;
        TextView dislike;

    }

}