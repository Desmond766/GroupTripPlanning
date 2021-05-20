package com.example.loginregister;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.example.loginregister.Login_Register.ShareLinkActivity;
import com.example.loginregister.ui.home.HomeFragment;
import com.example.loginregister.ui.home.adapter.MemberAdapter;
import com.example.loginregister.ui.home.adapter.PagerAdapter;
import com.example.loginregister.ui.home.bean.GroupUser;
import com.example.loginregister.ui.home.fragment.DiscussFragment;
import com.example.loginregister.ui.home.fragment.ItineraryFragment;
import com.example.loginregister.ui.home.fragment.OverviewFragment;
import com.example.loginregister.ui.home.view.magicindicator.MagicIndicator;
import com.example.loginregister.ui.home.view.magicindicator.ViewPagerHelper;
import com.example.loginregister.ui.home.view.magicindicator.buildins.commonnavigator.CommonNavigator;
import com.example.loginregister.ui.home.view.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import com.example.loginregister.ui.home.view.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import com.example.loginregister.ui.home.view.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import com.example.loginregister.ui.home.view.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import com.example.loginregister.ui.home.view.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TripDetail_FragementContainer extends AppCompatActivity {

    private PagerAdapter pagerAdapter;
    private MagicIndicator magicIndicator;
    private ViewPager viewPager;
    private CardView cardView;
    private ImageView ivBg;
    private View fullTransparent;
    private View fullWhite;
    private FirebaseDatabase mDatabase;
    TextView viewTitle;
    TextView viewBudget;
    public String tripID;
    public String budgetAll;
    long oldDuration;
    TextView viewDuration;
    String tempstart;
    String tempend;
    TextView cancel;
    private Dialog dialog;
    PopupWindow popup;
    TextView save;
    private Date startDate;
    private Date endDate;
    private BottomSheetDialog bottomSheetDialog;
    String start;
    RelativeLayout constraintLayout;
    String end;
    public TextView share;
    public String userid;
    public String ownerID;
    public ImageView changeOwner;
    public ArrayList <GroupUser> members = new ArrayList<GroupUser>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        mDatabase = FirebaseDatabase.getInstance();
        cardView = findViewById(R.id.card_view);
        ivBg = findViewById(R.id.iv_bg);
        fullTransparent = findViewById(R.id.full_transparent);
        fullWhite = findViewById(R.id.full_white);
        magicIndicator = findViewById(R.id.magic_indicator);
        viewPager = findViewById(R.id.view_pager);
        viewDuration = findViewById(R.id.tv_calendar);
        viewBudget = findViewById(R.id.textViewBudget);
        viewTitle = findViewById(R.id.triptitle);
        constraintLayout = findViewById(R.id.aaaaa);
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        changeOwner = findViewById(R.id.ib_more);
        share = findViewById(R.id.tv_share);

        Intent intent = getIntent();
        tripID = intent.getStringExtra(HomeFragment.USERID);
        setBasic();
        getMembers();
        changeOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("My app","Hello!!!!");
                if (userid.equals(ownerID)){
                    Dialog ownerDialog = new Dialog(TripDetail_FragementContainer.this);
                    View memberView = LayoutInflater.from(TripDetail_FragementContainer.this).inflate(R.layout.dialog_changeowner, null);
                    RecyclerView mems = memberView.findViewById(R.id.newOwners);
                    mems.setLayoutManager(new LinearLayoutManager(TripDetail_FragementContainer.this));
                    TextView save2 = memberView.findViewById(R.id.SaveOwner);
                    ownerDialog.setContentView(memberView);
                    Log.d("M","MEMBERS SIZE IS " + members.size() + "first" + members.get(0) );
                    MemberAdapter memberAdapter = new MemberAdapter(TripDetail_FragementContainer.this,members);
                    mems.setAdapter(memberAdapter);
                    ownerDialog.show();
                    save2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("m","I IN SIDE THIS SAve");
                            if (memberAdapter.getSelected()!= null){
                                GroupUser selectedMember = memberAdapter.getSelected();
                                mDatabase.getReference().child("trip").child(tripID).child("uid").setValue(selectedMember.getUid());
                                for (GroupUser groupUser:members){
                                    mDatabase.getReference().child("UID").child(groupUser.getUid()).child(tripID).child("ownerId").setValue(selectedMember.getUid());
                                }
                                mDatabase.getReference().child("UID").child(ownerID).child(tripID).child("ownerId").setValue(selectedMember.getUid());

                            }
                            ownerDialog.cancel();
                        }
                    });
            }else {
                    Toast.makeText(TripDetail_FragementContainer.this,"Only the owner can choose the new owner.😅",Toast.LENGTH_LONG).show();
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TripDetail_FragementContainer.this, ShareLinkActivity.class);
                intent.putExtra("tripID",tripID);
                startActivity(intent);
            }
        });

        //Calendar Click Events
//        viewDuration.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                bottomSheetDialog = new BottomSheetDialog(TripDetail_FragementContainer.this,R.style.BottomSheetTheme);
//                View sheetview = LayoutInflater.from(TripDetail_FragementContainer.this).inflate(R.layout.bottom_sheet_calander,(ViewGroup) findViewById(R.id.bottom_sheet));
//                bottomSheetDialog.setContentView(sheetview);
//                bottomSheetDialog.dismiss();
//            }
//        });
        viewBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userid.equals(ownerID)){
                    EditText input = new EditText(TripDetail_FragementContainer.this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    new AlertDialog.Builder(TripDetail_FragementContainer.this)
                            .setTitle("Please add your propose")
                            .setMessage("Please enter in Euros")
                            .setView(input)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String nb = input.getText().toString();
                                    DatabaseReference changeBudget = mDatabase.getReference().child("trip").child(tripID);
                                    HashMap<String,Object> newBudget = new HashMap<>();
                                    newBudget.put("budget",Double.parseDouble(nb));
                                    changeBudget.updateChildren(newBudget);
                                }
                            })
                            .setNegativeButton("Cancel",null).show();
                }else{
                    Toast.makeText(TripDetail_FragementContainer.this,"Only the owner can change the Budget (●'◡'●)",Toast.LENGTH_LONG).show();
                }
            }
        });
        viewDuration.setOnClickListener(new View.OnClickListener(){
            private ArrayList<String> totalC = new ArrayList<String>();//The List to store all the dates;
            private ArrayList<String> keptDays = new ArrayList<String>();

            @Override
            public void onClick(View view) {
                if (userid.equals(ownerID)){
                    dialog = new Dialog(TripDetail_FragementContainer.this);
                    View test = LayoutInflater.from(TripDetail_FragementContainer.this).inflate(R.layout.testalanderpickerview, null);
                    DateRangeCalendarView calanderpicker = test.findViewById(R.id.calendarnew);
                    TextView save = test.findViewById(R.id.save);
                    dialog.setContentView(test);
                    Window dialogWindow = dialog.getWindow();
                    dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialogWindow.setGravity( Gravity.BOTTOM);
                    dialog.show();
                    save.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onClick(View view) {
                            startDate = calanderpicker.getStartDate().getTime();
                            endDate = calanderpicker.getEndDate().getTime();
                            DatabaseReference changeDate = mDatabase.getReference().child("trip").child(tripID);
                            HashMap <String,Object> newDates = new HashMap<String,Object>();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            start = simpleDateFormat.format(startDate).toString();
                            end = simpleDateFormat.format(endDate).toString();
                            newDates.put("start",start);
                            newDates.put("end",end);
                            changeDate.updateChildren(newDates);

                            LocalDate dateBefore = LocalDate.parse(start);
                            LocalDate dateAfter = LocalDate.parse(end);

                            long duration = ChronoUnit.DAYS.between(dateBefore,dateAfter);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(startDate);
                            Log.d("my app",(String) simpleDateFormat.format(calendar.getTime()) + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            //totalC is the list of NEW DAYS.
                            totalC.add((String) simpleDateFormat.format(calendar.getTime()));
                            for(int i =0; i<duration;i++){
                                calendar.add(Calendar.DATE,1);
                                totalC.add((String) simpleDateFormat.format(calendar.getTime()));
                            }


                            //After above is finished,we get the new dates.
                            //Then we compare the new date with the old ones
                            DatabaseReference oldDates = mDatabase.getReference().child("trip").child(tripID).child("Day");
                            oldDates.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    for(DataSnapshot ds : task.getResult().getChildren()){
                                        String k = ds.getKey();
                                        if (!totalC.contains(k)){
                                            oldDates.child(k).setValue(null);
                                            keptDays.add(k);
//                                Day day = new Day();
//                                day.setDate(k);
//                                day.setActivities(ds.getValue());
//                                keptDays.put(k,day);
                                        }
                                        for (String date : totalC){
                                            if (!keptDays.contains(date)){
                                                oldDates.child(date).child("date").setValue(date);
                                            }
                                        }

                                    }
                                    //After above, we get a map contains the days taht can be reused.

                                }

                            });

                            dialog.cancel();
                        }
                    });
                }else{
                    Toast.makeText(TripDetail_FragementContainer.this,"Only the owner can change the Duration (●'◡'●)",Toast.LENGTH_LONG).show();
                }

            }

        });




        List<String> mTitleDataList = Arrays.asList(getResources().getStringArray(R.array.main_list_type));
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(OverviewFragment.newInstance(mTitleDataList.get(0), "0"));
        fragmentList.add(ItineraryFragment.newInstance(mTitleDataList.get(1), "1"));
        fragmentList.add(DiscussFragment.newInstance(mTitleDataList.get(2), "2"));
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), fragmentList);

        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mTitleDataList == null ? 0 : mTitleDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(getResources().getColor(R.color.color_content));
                colorTransitionPagerTitleView.setSelectedColor(getResources().getColor(R.color.color_red_required));
                colorTransitionPagerTitleView.setText(mTitleDataList.get(index));
                colorTransitionPagerTitleView.setOnClickListener(view1 -> viewPager.setCurrentItem(index));
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                indicator.setColors(getResources().getColor(R.color.color_red_required));
                return indicator;
            }
        });
        commonNavigator.setAdjustMode(true);
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    cardView.setVisibility(View.VISIBLE);
                    ivBg.setVisibility(View.VISIBLE);
                    fullTransparent.setVisibility(View.VISIBLE);
                    fullWhite.setVisibility(View.VISIBLE);
                } else {
                    cardView.setVisibility(View.GONE);
                    ivBg.setVisibility(View.GONE);
                    fullTransparent.setVisibility(View.GONE);
                    fullWhite.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setBasic(){
        DatabaseReference sDateDatabase = mDatabase.getReference().child("trip").child(tripID);
        ValueEventListener EventListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String start = "";
                String end = "";
                String budget = "";
                String title = "";
                for(DataSnapshot ds : snapshot.getChildren()){
                    String key = ds.getKey();
//                    c1.getDisplayName(c1.MONTH,c1.SHORT, Locale.US)
                    switch (key) {
                        case "end":
                            end = (String) ds.getValue();
                            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date date = simpleFormat.parse(end);
                                Calendar c1 = Calendar.getInstance();
                                c1.setTime(date);
                                tempend = c1.getDisplayName(c1.MONTH,c1.SHORT, Locale.US) +"/"+ c1.getTime().getDate();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "start":
                            start = (String) ds.getValue();
                            SimpleDateFormat simpleFormat2 = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date date = simpleFormat2.parse(start);
                                Calendar c2 = Calendar.getInstance();
                                c2.setTime(date);
                                tempstart = c2.getDisplayName(c2.MONTH,c2.SHORT, Locale.US) +"/"+ c2.getTime().getDate();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "budget":
                            budget = String.valueOf(ds.getValue());
                            budgetAll = budget;
                            break;
                        case "title":
                            title = (String) ds.getValue();
                            break;
                        case "uid":
                            ownerID = ds.getValue().toString();
                            break;
                    }

                }
                LocalDate dateBefore = LocalDate.parse(start);
                LocalDate dateAfter = LocalDate.parse(end);
                viewDuration.setText(tempstart + "-" +tempend);
                viewBudget.setText(budget);
                viewTitle.setText(title);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        };
        sDateDatabase.addValueEventListener(EventListener);
    }
    public void getMembers() {
        mDatabase.getReference().child("Group").child(tripID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                members = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()){
                    String memberid = ds.getKey();
                    if (!memberid.equals(ownerID)){
                        GroupUser groupUser = new GroupUser();
                        for (DataSnapshot ds2 : ds.getChildren()){

                            if (ds2.getKey().equals("username")){
                                groupUser.setUsername(ds2.getValue().toString());
                            }
                            if (ds2.getKey().equals("uid")){
                                groupUser.setUid(ds2.getValue().toString());
                            }
                        }
                        members.add(groupUser);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}
