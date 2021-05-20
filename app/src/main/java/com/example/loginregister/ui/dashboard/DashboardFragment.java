package com.example.loginregister.ui.dashboard;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.example.loginregister.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;



public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private Button datepicker;
    private Button submit;
    private Date startDate;
    private Date endDate;
    private DatabaseReference mDatabase;
    private String userId;
    FirebaseAuth firebaseAuth;
    private String Tripid;
    private Calendar c;
    private ArrayList<String> totalC;
    private Dialog dialog;
    private CalendarView calendarView;
    EditText Budget;
    TextView Start_date;
    TextView End_date;
    TextView duration;
    String start;
    String end;
    int BudgetFinal;
    Context mContext;

    EditText location;
    String location_final;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
//        cardview = getActivity().findViewById(R.id.card_view);
//        cardview.setVisibility(View.GONE);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        location = root.findViewById(R.id.locationinput);
        Start_date = root.findViewById(R.id.StartDate);
        End_date = root.findViewById(R.id.Enddate);
        submit = root.findViewById(R.id.set);
        Budget = root.findViewById(R.id.Budgetinput);
        mContext = getActivity();



//        RangeDateSelector ri = root.findViewById(R.id.date_picker);
        totalC = new ArrayList<String>();
//        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
//
//        builder.setTitleText("Plase select your date");

//        final MaterialDatePicker materialDatePicker = builder.build();

        Start_date.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
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
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(View view) {
                        if(calanderpicker.getStartDate()!= null && calanderpicker.getEndDate()!= null){
                            startDate = calanderpicker.getStartDate().getTime();
                            endDate = calanderpicker.getEndDate().getTime();
                            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
                            start = simpleFormat.format(startDate).toString();
                            end = simpleFormat.format(endDate).toString();
                            Start_date.setText(start);
                            End_date.setText(end);
                            LocalDate dateBefore = LocalDate.parse(start);
                            LocalDate dateAfter = LocalDate.parse(end);
                            long duriations = ChronoUnit.DAYS.between(dateBefore, dateAfter);
                            int tempdate = (int)duriations;
                            c = Calendar.getInstance();
                            c.setTime(startDate);
                            totalC.add((String)simpleFormat.format(c.getTime()));
                            for(int i =0; i<duriations;i++){
                                c.add(Calendar.DATE,1);
                                totalC.add((String) simpleFormat.format(c.getTime()));

                                System.out.println(simpleFormat.format(c.getTime()));
                            }


                            dialog.cancel();
                        }else{
                            Toast.makeText(mContext,"Please also choose the end date please🙂",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

        });
        submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                location_final = location.getText().toString();
                Tripid = location_final+start;
                String budgetString = Budget.getText().toString();
                if (!location_final.equals("") && !Tripid.equals("") &&!budgetString.matches("")){
                    BudgetFinal = Integer.parseInt(budgetString);
                    int cost = 0;
                    String ownerId = userId;
                    writeNewTrip(location_final,start,end,userId,BudgetFinal,cost);
                    writeNewuidTrip(Tripid,ownerId);
                    writtenDays(totalC);
                    Toast.makeText(mContext,"Your trip has been created succesfully!😁",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(mContext,"Dear,you have to fill all the fields.😘",Toast.LENGTH_LONG).show();
                }


            }
        });


        return root;


    }
    public String getlocation(){
        return location_final;
    }
    public String getStartDate(){
        String Sdate = startDate.toString();
        return Sdate;
    }
    public String getEndDate(){
        return endDate.toString();
    }
    public void writeNewTrip(String location_send, String start, String end,String uid,int budget,int cost) {
        Trip trip = new Trip(location_send,start,end,uid,budget,cost);

        mDatabase.child("trip").child(Tripid).setValue(trip);
    }
    public void writeNewuidTrip(String tripid,String ownerId){

        UserTrip userTrip = new UserTrip(tripid,false,false,ownerId);
        mDatabase.child("UID").child(userId).child(tripid).setValue(userTrip);
    }
    public void writtenDays(ArrayList<String> a){
        for(int i = 0; i< a.size();i++){
            Day day = new Day(a.get(i));
            mDatabase.child("trip").child(Tripid).child("Day").child(day.getDate()).setValue(day);
        }
    }


}
class Trip{
    public String title;
    public String start;
    public String end;
    public String uid;
    public int budget;
    public int cost;

    public Trip(){

    }
    public Trip(String location_send, String start, String end,String uid,int budget,int cost){
        this.title = location_send;
        this.start = start;
        this.end = end;
        this.uid = uid;
        this.budget = budget;
        this.cost = cost;
    }

}
class UserTrip{
    public String tripid;
    public boolean BudgetVoted;
    public boolean TimeVoted;
    public String ownerId;
    public UserTrip(){

    }
    public UserTrip(String tripid,boolean BudgetVoted,boolean TimeVoted,String ownerId){
        this.tripid = tripid;
        this.BudgetVoted = BudgetVoted;
        this.TimeVoted = TimeVoted;
        this.ownerId = ownerId;
    }

}
class Day{
    public String date;
    public Day(String date){
        this.date = date;
    }
    public String getDate(){
        return this.date;
    }
}


