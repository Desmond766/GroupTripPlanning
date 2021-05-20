package com.example.loginregister.ui.home.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.Login_Register.SaveLoginState;
import com.example.loginregister.R;
import com.example.loginregister.chatBox.Adapter.GroupMessageAdapter;
import com.example.loginregister.chatBox.Adapter.MessageAdapter;
import com.example.loginregister.chatBox.GroupChatActivity;
import com.example.loginregister.chatBox.MessageActivity;
import com.example.loginregister.chatBox.Model.Chat;
import com.example.loginregister.chatBox.Model.GroupChat;
import com.example.loginregister.chatBox.chatActivity;
import com.example.loginregister.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class DiscussFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public String tripID;
    Button btn_createChat;
    private  View groupFragmentView;
    private DatabaseReference GroupRef;
    RecyclerView recyclerView;

    private Toolbar mToolbar;
    private ImageButton SendMessageButton;
    private EditText userMessageInput;
    //private ScrollView mScrollView;
    private TextView displayTextMessages;

    private DatabaseReference UsersRef;
    private DatabaseReference ChatNameRef;

    private String currentUserID;
    private String currentUserName;


    private CircleImageView image_left;
    private CircleImageView image_right;
    List<GroupChat> mChat;
    GroupMessageAdapter groupMessageAdapter;

    StorageReference storageRef = FirebaseStorage.getInstance().getReference("userPhoto");

    public static DiscussFragment newInstance(String param1, String param2) {
        DiscussFragment fragment = new DiscussFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        tripID = intent.getStringExtra(HomeFragment.USERID);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("users");
        ChatNameRef = FirebaseDatabase.getInstance().getReference().child("Chats").child("GroupChat").child(tripID);
        mChat = new ArrayList<>();



        groupFragmentView = inflater.inflate(R.layout.fragment_explore, container, false);
        GroupRef = FirebaseDatabase.getInstance().getReference().child("Chats").child("GroupChat");
        InitializeFields();

        GetUserInfo();

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try {
                    SaveMessageInfoToDatabase();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                userMessageInput.setText("");

            }
        });
        DisplayMessages();



        return groupFragmentView;
    }

    private void InitializeFields()
    {
        mToolbar = (Toolbar) groupFragmentView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(tripID);

        SendMessageButton = (ImageButton) groupFragmentView.findViewById(R.id.send_message_button);
        userMessageInput = (EditText) groupFragmentView.findViewById(R.id.input_group_message);
//        displayTextMessages = (TextView) groupFragmentView.findViewById(R.id.group_chat_text_display);
//        mScrollView = (ScrollView) groupFragmentView.findViewById(R.id.my_scroll_view);
//        image_left = (CircleImageView) groupFragmentView.findViewById(R.id.image_left);

        recyclerView = groupFragmentView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }



    private void GetUserInfo()
    {
        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    currentUserName = dataSnapshot.child("username").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }




    private void SaveMessageInfoToDatabase() throws ParseException {
        String message = userMessageInput.getText().toString();

        if (TextUtils.isEmpty(message))
        {
            Toast.makeText(getContext(), "Please write message first...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            String currentTime = currentTimeFormat.format(calForTime.getTime());

            String senderId = currentUserID;
            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);
            messageInfoMap.put("senderId",senderId);
            ChatNameRef.push().setValue(messageInfoMap);
        }
    }

    private void DisplayMessages() {
        ChatNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    GroupChat groupChat = snapshot.getValue(GroupChat.class);
                    assert groupChat != null;
                    if (!groupChat.getMessage().equals("")){
                        mChat.add(groupChat);
                    }


                }
                groupMessageAdapter = new GroupMessageAdapter(getContext(), mChat);
                recyclerView.setAdapter(groupMessageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
