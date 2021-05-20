package com.example.loginregister.chatBox.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loginregister.R;
import com.example.loginregister.chatBox.MessageActivity;
import com.example.loginregister.chatBox.Model.Chat;
import com.example.loginregister.chatBox.Model.GroupChat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class GroupMessageAdapter  extends RecyclerView.Adapter<GroupMessageAdapter.ViewHolder>{

    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<GroupChat> groupChats;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference("userPhoto");
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


    public GroupMessageAdapter(Context context, List<GroupChat> mChat){
        this.groupChats = mChat;
        this.mContext = context;
    }

    @NonNull
    @Override
    public GroupMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new GroupMessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new GroupMessageAdapter.ViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull GroupMessageAdapter.ViewHolder holder, int position) {
        GroupChat chat = groupChats.get(position);

        holder.show_message.setText(chat.getMessage());
        // thie sets the image of each message(either left or right)
        //download userSelfie from storage if it exits
        final long ONE_MEGABYTE = 1024 * 1024;
        StorageReference photoRef = storageRef.child(chat.getSenderId());
        //System.out.println("photoRef is "+ photoRef);
        photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //System.out.println("here1 ");
                Glide.with(mContext).load(uri).into(holder.profile_image);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public ImageView profile_image;
        //public ImageView profile_image_right;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            //profile_image_right = itemView.findViewById(R.id.profile_image_right);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (groupChats.get(position).getSenderId().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
