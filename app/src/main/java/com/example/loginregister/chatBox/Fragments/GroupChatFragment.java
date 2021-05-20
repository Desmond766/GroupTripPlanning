package com.example.loginregister.chatBox.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.loginregister.R;
import com.example.loginregister.chatBox.GroupChatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GroupChatFragment extends Fragment {

    Button btn_createChat;
    private  View groupFragmentView;
    private  ListView list_view;
    private  ArrayAdapter<String> arrayAdapter;
    private  ArrayList<String> list_of_groups = new ArrayList<>();

    private DatabaseReference GroupRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);

        btn_createChat = groupFragmentView.findViewById(R.id.btn_createGroup);
        GroupRef = FirebaseDatabase.getInstance().getReference().child("Chats").child("GroupChat");

        //initialize format of how groups shows up
         IntializedField();

        RetrieveAndDisplayGroups();

        btn_createChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestNewGroup();
            }
        });

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                String currentGroupName = adapterView.getItemAtPosition(position).toString();
                //jump to chatRoom to send message
                Intent groupChatIntent = new Intent(getContext(), GroupChatActivity.class);
                groupChatIntent.putExtra("groupName" , currentGroupName);
                startActivity(groupChatIntent);
            }
        });


        return groupFragmentView;
    }

    private void IntializedField() {
        list_view = (ListView) groupFragmentView.findViewById(R.id.list_view_Groups);
        arrayAdapter = new ArrayAdapter<String> (getContext(), android.R.layout.simple_expandable_list_item_1,list_of_groups);
        list_view.setAdapter(arrayAdapter);
    }

    private void RetrieveAndDisplayGroups() {
        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot)
            {
                Set<String> set = new HashSet<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    set.add(snapshot.getKey());
                }

                list_of_groups.clear();
                list_of_groups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    private void RequestNewGroup() {
        //AlertDialog is quite useful by poping out without input inadvance
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter Group Name");

        final EditText groupNameField = new EditText(getActivity());
        groupNameField.setHint("e.g  Happy gaming");
        builder.setView(groupNameField);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = groupNameField.getText().toString();
                if(TextUtils.isEmpty(groupName)){
                    Toast.makeText(getActivity(),"please input Group Name",Toast.LENGTH_SHORT).show();
                }else {
                    System.out.println("createNeGroup-----------");
                    createNewGroup(groupName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void createNewGroup(String groupName) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Chats").child("GroupChat").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                //if this groupName already exists
                if( dataSnapshot.exists() && dataSnapshot.getValue().toString().contains(groupName)){
                    Toast.makeText(getActivity(), "group already exists,try another one ",Toast.LENGTH_SHORT).show();
                }else{
                    //create this group when this group name is unique
                    mDatabase.child("Chats").child("GroupChat").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getActivity(), groupName+ " group is created successfully",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    };

}