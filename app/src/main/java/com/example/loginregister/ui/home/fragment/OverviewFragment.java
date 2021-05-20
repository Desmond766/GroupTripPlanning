package com.example.loginregister.ui.home.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.example.loginregister.R;
import com.example.loginregister.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverviewFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final boolean ISTIME = true;
    private static final boolean ISBUDGET = false;
    private String mParam1;
    private String mParam2;
    private Button buttonBudget;
    private Button buttonTime;
    private FirebaseDatabase mDatabase;

    private String tripID;
    private Context mContext;
    private RecyclerView timeVote;
    private RecyclerView budgetVote;
    public String userid;
    private Button addTime;
    private Button addBudget;
    PopupWindow popup;
    private Dialog dialog;
    private Date startDate;
    private Date endDate;
    String start;
    String end;
    String tempstart;
    String tempend;
    ScrollView constraintLayout;
    public OverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OverviewFragment.
     */
    public static OverviewFragment newInstance(String param1, String param2) {
        OverviewFragment fragment = new OverviewFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        mDatabase = FirebaseDatabase.getInstance();
        mContext = getActivity();
        constraintLayout = view.findViewById(R.id.overviewLayout);
        buttonBudget = view.findViewById(R.id.addBudjetvote);
        buttonTime = view.findViewById(R.id.add_time_vote);
        timeVote = view.findViewById(R.id.list_view_time);
        timeVote.setLayoutManager(new LinearLayoutManager(mContext));
        budgetVote = view.findViewById(R.id.list_view_budget);
        budgetVote.setLayoutManager(new LinearLayoutManager(mContext));
        budgetVote.addItemDecoration(new DividerItemDecoration(mContext,LinearLayoutManager.VERTICAL));
        Intent intent = getActivity().getIntent();
        tripID = intent.getStringExtra(HomeFragment.USERID);
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        addTime = view.findViewById(R.id.addTime);
        addBudget = view.findViewById(R.id.addBudget);
        timeVote.setLayoutManager(new LinearLayoutManager(mContext));



        setDuration();
        setBudget();

        addTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("'''''''''''''''''''''''clicked");
                addTimeVote();
            }
        });
        addBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = new EditText(mContext);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                new AlertDialog.Builder(mContext)
                        .setTitle("Please add your propose")
                        .setMessage("Please enter in Euros")
                        .setView(input)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String pBudget = input.getText().toString();
                                Vote vote = new Vote(pBudget,0);
                                mDatabase.getReference().child("Vote").child(tripID).child("Budget").child(pBudget).setValue(vote);
                            }
                        })
                        .setNegativeButton("Cancel",null).show();
            }
        });



        return view;
    }




    public void setBudget(){
        DatabaseReference voteBase = mDatabase.getReference().child("Vote").child(tripID).child("Budget");
        voteBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList <VoteInfo> votes = new ArrayList<>();
                //ds is the vote id;
                for (DataSnapshot ds : snapshot.getChildren()){
                    Iterator<DataSnapshot> iterator = ds.getChildren().iterator();
                    VoteInfo vote = new VoteInfo();
                    while (iterator.hasNext()){
                        DataSnapshot  temp = iterator.next();

                        String key = temp.getKey();
                        if (key.equals("count")){
                            vote.setCount((Long) temp.getValue());
                        }
                        else if(key.equals("description")){
                            vote.setDescription(temp.getValue().toString());
                        }
                    }
                    votes.add(vote);
                }
                VoteAdapter voteAdapter = new VoteAdapter(mContext,votes);

                budgetVote.setAdapter(voteAdapter);
                buttonBudget.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(voteAdapter.getSelected() != null){
                            VoteInfo selected = voteAdapter.getSelected();
                            String selectedBudget = selected.getDescription();
                            long selectedCount = selected.getCount();
                            DatabaseReference voteCheck =  mDatabase.getReference().child("UID").child(userid).child(tripID).child("BudgetVoted");
                            voteCheck.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.getResult().getValue().equals(true)){
                                        vote(ISBUDGET,selectedBudget,selectedCount);
                                    }
                                    else{
                                        Toast.makeText(mContext,"You have already voted once.",Toast.LENGTH_SHORT).show();
                                        Log.d("app","Vote denied");
                                    }
                                }
                            });


                        }
                    }
                });

                budgetVote.setAdapter(voteAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setDuration (){
        DatabaseReference voteBase = mDatabase.getReference().child("Vote").child(tripID).child("Duration");
        voteBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList <VoteInfo> votes = new ArrayList<>();
//                ds is the vote id;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Iterator<DataSnapshot> iterator = ds.getChildren().iterator();
                    VoteInfo vote = new VoteInfo();
                    while (iterator.hasNext()) {
                        DataSnapshot temp = iterator.next();
                        String key = temp.getKey();
                        if (key.equals("count")) {
                            vote.setCount((Long) temp.getValue());
                        } else if (key.equals("description")) {
                            vote.setDescription(temp.getValue().toString());
                        }
                    }
                    votes.add(vote);
                }
                VoteAdapter voteAdapter = new VoteAdapter(mContext,votes);
                timeVote.setAdapter(voteAdapter);

                buttonTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(voteAdapter.getSelected() != null){
                            VoteInfo selected = voteAdapter.getSelected();
                            String startTime = selected.getDescription();
                            long selectedCount = selected.getCount();
                            DatabaseReference voteCheck =  mDatabase.getReference().child("UID").child(userid).child(tripID).child("TimeVoted");
                            voteCheck.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.getResult().getValue().equals(true)){
                                        vote(ISTIME,startTime,selectedCount);
                                    }
                                    else{
                                        Toast.makeText(mContext,"You have already voted once.",Toast.LENGTH_SHORT).show();
                                        Log.d("app","Vote denied");
                                    }

                                }
                            });

                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void vote(boolean isTime,String decription,long count){
        DatabaseReference voteDatabase;
        DatabaseReference voteCheck;
        //Means it is a vote of Budget
        if (!isTime){
            voteDatabase = mDatabase.getReference().child("Vote").child(tripID).child("Budget").child(decription);
            voteCheck = mDatabase.getReference().child("UID").child(userid).child(tripID).child("BudgetVoted") ;
        }else{
            voteDatabase = mDatabase.getReference().child("Vote").child(tripID).child("Duration").child(decription);
            voteCheck = mDatabase.getReference().child("UID").child(userid).child(tripID).child("TimeVoted") ;
        }
        HashMap<String,Object> updates = new HashMap<>();
        long newCount = count + 1;
        updates.put("count",newCount);
        voteDatabase.updateChildren(updates);
        voteCheck.setValue(true);
    }
    public void addTimeVote(){
        dialog = new Dialog(getActivity());
        View test = LayoutInflater.from(getActivity()).inflate(R.layout.testalanderpickerview, null);
        DateRangeCalendarView calanderpicker = test.findViewById(R.id.calendarnew);
        TextView save = test.findViewById(R.id.save);
        dialog.setContentView(test);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialogWindow.setGravity( Gravity.BOTTOM);
        dialog.show();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(calanderpicker.getStartDate()!= null && calanderpicker.getEndDate()!= null){
                    startDate = calanderpicker.getStartDate().getTime();
                    endDate = calanderpicker.getEndDate().getTime();
                    Calendar c1 = Calendar.getInstance();
                    Calendar c2 = Calendar.getInstance();
                    c1.setTime(startDate);
                    c2.setTime(endDate);
                    tempstart = c1.getDisplayName(c1.MONTH,c1.SHORT, Locale.US) +"-"+ c1.getTime().getDate();
                    tempend = c2.getDisplayName(c2.MONTH,c2.SHORT, Locale.US) +"-"+ c2.getTime().getDate();
                    dialog.cancel();
                    String pTime = tempstart+" to "+tempend;

                    Vote vote = new Vote(pTime,0);
                    mDatabase.getReference().child("Vote").child(tripID).child("Duration").child(pTime).setValue(vote);
                }else{
                    Toast.makeText(mContext,"Please also choose the end date please🙂",Toast.LENGTH_LONG).show();
                }
            }

                });




    }

}
