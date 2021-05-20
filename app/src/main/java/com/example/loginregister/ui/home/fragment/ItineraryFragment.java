  package com.example.loginregister.ui.home.fragment;

  import android.app.TimePickerDialog;
  import android.content.Context;
  import android.content.Intent;
  import android.os.Build;
  import android.os.Bundle;
  import android.text.format.DateFormat;
  import android.util.DisplayMetrics;
  import android.util.Log;
  import android.view.LayoutInflater;
  import android.view.View;
  import android.view.ViewGroup;
  import android.widget.EditText;
  import android.widget.GridView;
  import android.widget.HorizontalScrollView;
  import android.widget.LinearLayout;
  import android.widget.ListView;
  import android.widget.TextView;
  import android.widget.TimePicker;
  import android.widget.Toast;

  import androidx.annotation.NonNull;
  import androidx.annotation.RequiresApi;
  import androidx.appcompat.app.AlertDialog;
  import androidx.fragment.app.Fragment;

  import com.example.loginregister.R;
  import com.example.loginregister.ui.home.HomeFragment;
  import com.example.loginregister.ui.home.adapter.DateInfoAdapter;
  import com.example.loginregister.ui.home.adapter.TimeAdapter;
  import com.example.loginregister.ui.home.bean.DateInfo;
  import com.example.loginregister.ui.home.bean.NoteInfo;
  import com.example.loginregister.ui.home.utils.Utils;
  import com.example.loginregister.ui.home.view.GridViewInScrollView;
  import com.google.firebase.auth.FirebaseAuth;
  import com.google.firebase.database.DataSnapshot;
  import com.google.firebase.database.DatabaseError;
  import com.google.firebase.database.DatabaseReference;
  import com.google.firebase.database.FirebaseDatabase;
  import com.google.firebase.database.ValueEventListener;

  import java.text.ParseException;
  import java.text.SimpleDateFormat;
  import java.util.ArrayList;
  import java.util.Calendar;
  import java.util.Date;
  import java.util.HashMap;
  import java.util.Iterator;
  import java.util.List;



  /**
   * A simple {@link Fragment} subclass.
   * Use the {@link com.example.loginregister.ui.home.fragment.ItineraryFragment#newInstance} factory method to
   * create an instance of this fragment.
   */
  public class ItineraryFragment extends Fragment {
      private static final String ARG_PARAM1 = "param1";
      private static final String ARG_PARAM2 = "param2";
      private String mParam1;
      private String mParam2;
      private String activityID;
      private HorizontalScrollView mScrollView;
      private GridViewInScrollView mGridView;
      private ListView mListView;
      private FirebaseDatabase mDatabase;
      private List<DateInfo> lists;
      private TimeAdapter adapter;
      private DateInfoAdapter dateInfoAdapter;

      public double budget ;
      public double totalCost;
      public double remain;

      private int hour;
      private int min;
      private String startTime;
      private String endTime;

      public Context context;
      private float mDensity;
      private String tripID;
      View view;

      public ItineraryFragment() {
          // Required empty public constructor
      }

      /**
       * Use this factory method to create a new instance of
       * this fragment using the provided parameters.
       *
       * @param param1 Parameter 1.
       * @param param2 Parameter 2.
       * @return A new instance of fragment ItineraryFragment.
       */
      public static com.example.loginregister.ui.home.fragment.ItineraryFragment newInstance(String param1, String param2) {
          com.example.loginregister.ui.home.fragment.ItineraryFragment fragment = new com.example.loginregister.ui.home.fragment.ItineraryFragment();
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
          view = inflater.inflate(R.layout.fragment_itinerary, container, false);

          Intent intent = getActivity().getIntent();
          tripID = intent.getStringExtra(HomeFragment.USERID);
          mDatabase = FirebaseDatabase.getInstance();

          // GridView
          mGridView = view.findViewById(R.id.grid_view);
          mScrollView = view.findViewById(R.id.scroll_view);
          //getNotes();
          context = getContext();
          onAttach(context);
          getBudget();
          getDuration();
          getCost();

          return view;
      }

      public void getCost(){
          DatabaseReference allCosts = mDatabase.getReference().child("trip").child(tripID).child("cost");
          allCosts.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  if(snapshot.getValue().toString() != null){
                      totalCost = Double.parseDouble(snapshot.getValue().toString());
                      Log.d("app","my budget is " + budget);
                      remain = budget - totalCost;
                      Log.d("my app" , "the cost is " + totalCost);
                      if(budget != 0 ){
                          if (remain < 0 ){
                              Toast.makeText(context, "Just to let you Know (*^_^*),You have spent " + (-remain) + "  over the budget.", Toast.LENGTH_SHORT).show();
                          }else{
                              if (remain > budget){
                                  remain = budget;
                              }
                              Toast.makeText(context, "Just to let you Know (*^_^*),You still have" + remain + "  left.", Toast.LENGTH_SHORT).show();
                          }
                      }


                  }

              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });
      }
      public void getBudget(){
          DatabaseReference budgetData = mDatabase.getReference().child("trip").child(tripID);
          budgetData.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  for (DataSnapshot ds : snapshot.getChildren()){
                      if (ds.getKey().equals("budget")){
                          budget = Double.parseDouble(ds.getValue().toString());
                      }
                  }

              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });
      }


      public String getActivityID(String location) {

          return (tripID + location);
      }

      //Used for Adding activity to the DataBase(For Backend!!!)
      public void addActivity(String date, String location, String description, String startTime, String endTime, Integer likes, Integer dislikes, String estimatedCost) {
          HashMap<String, Object> activity = new HashMap<>();
          activity.put("date", date);
          activity.put("Location", location);
          activity.put("description", description);
          activity.put("startTime", startTime);
          activity.put("endTime", endTime);
          activity.put("estimatedCost", estimatedCost);
          activity.put("likes", likes);
          activity.put("dislikes", dislikes);
          activity.put("tripID", tripID);
          mDatabase.getReference().child("trip").child(tripID).child("Day").child(date).child(getActivityID(location)).setValue(activity);
      }

      public void getDuration() {
          DatabaseReference dDatabase = mDatabase.getReference().child("trip").child(tripID).child("Day");
          ValueEventListener mListener = new ValueEventListener() {
              @RequiresApi(api = Build.VERSION_CODES.O)
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  HashMap<String,ArrayList<NoteInfo>> mapActivities = new HashMap<String,ArrayList<NoteInfo>>();
                  ArrayList<String> allDays = new ArrayList<String>();
                  //Loop for every day
                  for (DataSnapshot ds : snapshot.getChildren()) {
                      ArrayList<NoteInfo> notes = new ArrayList<NoteInfo>();
                      allDays.add(ds.getKey());
                      Iterator<DataSnapshot> iterator = ds.getChildren().iterator();
                      //Loop for every Activity
                      while(iterator.hasNext()){
                          NoteInfo note = new NoteInfo();
                          DataSnapshot a =iterator.next();
                          if(!a.getKey().equals("date")){
                              for (DataSnapshot ds2 : a.getChildren()){
                                  String key = ds2.getKey();
                                  switch (key) {
                                      case "endTime":
                                          note.setEndTime(ds2.getValue().toString());
                                          break;
                                      case "estimatedCost":
                                          note.setCost(ds2.getValue().toString());
                                          break;
                                      case "startTime":
                                          note.setStartTime(ds2.getValue().toString());
                                          break;
                                      case "Location":
                                          note.setLocation(ds2.getValue().toString());
                                          break;
                                      case "date":
                                          note.setDate(ds2.getValue().toString());
                                          break;
                                      case "tripID":
                                          note.setTripID(ds2.getValue().toString());
                                          break;
                                      case "likes":
                                          note.setLikes(Integer.parseInt(ds2.getValue().toString()));
                                          break;
                                      case "dislikes":
                                          note.setDislikes(Integer.parseInt(ds2.getValue().toString()));
                                          break;
                                  }
                              }
                          };

                          if(!note.isEmpty()){
                              notes.add(note);
                          }

                      }
                      if (notes != null){
                          mapActivities.put(ds.getKey(),notes);
                      }

                  }

                  SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
                  lists = new ArrayList<DateInfo>();
                  for (String date : allDays) {
                      try {
                          Date d = dateformat.parse(date);
                          String dayOfTheWeek = (String) DateFormat.format("EEE", d);
                          String Month = (String) DateFormat.format("MM", d);
                          String day = (String) DateFormat.format("dd", d);
                          DateInfo dateInfo = new DateInfo();
                          dateInfo.setDate(date);
                          dateInfo.setWeek(dayOfTheWeek);
                          dateInfo.setMonth(Month);
                          dateInfo.setDay(day);
                          dateInfo.setNodes(mapActivities.get(date));

                          lists.add(dateInfo);
                      } catch (ParseException e) {
                          e.printStackTrace();
                      }
                  }
                  horizontalLayout(lists.size());
                  adapter = new TimeAdapter(getActivity(), lists);
                  adapter.setDate(Utils.dateFormat.format(new Date()));
                  mGridView.setAdapter(adapter);

                  // GridView Click on the event
                  mGridView.setOnItemClickListener((parent, view1, position, id) -> {
                      // Refresh the horizontal list position
                      DateInfo dateInfo = lists.get(position);
                      if (dateInfo == null) return;
                      adapter.setDate(dateInfo.getDate());
                      adapter.notifyDataSetChanged();
                      // ScrollToPosition
                      mListView.post(() -> mListView.smoothScrollToPosition(position));
                  });

                  // Sets where you want the horizontal list to slide
                  mScrollView.post(() -> mScrollView.smoothScrollTo((int) (80 * 0 * mDensity), 0));

                  // Init ListView
                  mListView = view.findViewById(R.id.list_view);
                  dateInfoAdapter = new DateInfoAdapter(getActivity(), lists, tripID, FirebaseAuth.getInstance().getCurrentUser().getUid());
                  mListView.setAdapter(dateInfoAdapter);
                  dateInfoAdapter.setOnChildClickListener(dateInfo -> {
                      showAddDialog(dateInfo);
                  });
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {
              }
          };
          dDatabase.addValueEventListener(mListener);
      }

      /**
       * Calculate the number of horizontal positions, the required width, and call when adding or subtracting data
       *
       * @param size
       */
      private void horizontalLayout(int size) {
          DisplayMetrics dm = new DisplayMetrics();
          getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
          mDensity = dm.density;
          int allWidth = (int) (90 * size * mDensity);
          int itemWidth = (int) (80 * mDensity);
          LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(allWidth, LinearLayout.LayoutParams.MATCH_PARENT);
          mGridView.setLayoutParams(params);
          mGridView.setColumnWidth(itemWidth);
          mGridView.setHorizontalSpacing(Utils.dip2px(getActivity(), 1));
          mGridView.setStretchMode(GridView.NO_STRETCH);
          mGridView.setNumColumns(size);
      }


      private void showAddDialog(DateInfo dateInfo) {
          final View textEntryView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog, null);
          final EditText etLocation = (EditText) textEntryView.findViewById(R.id.et_location);
          final TextView etStartTime = (TextView) textEntryView.findViewById(R.id.et_start_time);
          final TextView etEndTime = (TextView) textEntryView.findViewById(R.id.et_end_time);
          final EditText etCost = (EditText) textEntryView.findViewById(R.id.et_cost);
          String date = dateInfo.getDate();

          AlertDialog.Builder ad1 = new AlertDialog.Builder(getActivity());
          ad1.setTitle("Add Notes");
          ad1.setIcon(android.R.drawable.ic_dialog_info);
          ad1.setView(textEntryView);
          etStartTime.setOnClickListener(new View.OnClickListener(){

              @Override
              public void onClick(View view) {
                  //initialize the dialog

                  TimePickerDialog timePickerDialog = new TimePickerDialog(textEntryView.getContext(),

                          new TimePickerDialog.OnTimeSetListener() {
                              @Override
                              public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                  hour = i;
                                  min = i1;
                                  Calendar c1 = Calendar.getInstance();
                                  c1.set(0,0,0,hour,min);
                                  etStartTime.setText(DateFormat.format("hh:mm aa",c1));
                                  String startTime = etStartTime.getText().toString();

                              }
                          },12,0,false
                  );
                  timePickerDialog.updateTime(hour,min);
                  timePickerDialog.show();
                  startTime = etStartTime.getText().toString();
              }
          });
          etEndTime.setOnClickListener(new View.OnClickListener(){

              @Override
              public void onClick(View view) {
                  //initialize the dialog
                  TimePickerDialog timePickerDialog = new TimePickerDialog(textEntryView.getContext(),

                          new TimePickerDialog.OnTimeSetListener() {
                              @Override
                              public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                  hour = i;
                                  min = i1;
                                  Calendar c1 = Calendar.getInstance();
                                  c1.set(0,0,0,hour,min);
                                  etEndTime.setText(DateFormat.format("hh:mm aa",c1));
                                  String endTime = etEndTime.getText().toString();

                              }
                          },12,0,false
                  );
                  timePickerDialog.updateTime(hour,min);
                  timePickerDialog.show();
                  endTime = etEndTime.getText().toString();
              }
          });

          ad1.setView(textEntryView);
          ad1.setPositiveButton("Yes", (dialog, i) -> {
              String location = etLocation.getText().toString();
              Log.d("app","added location is" + location);
              String startTime = etStartTime.getText().toString();
              String endTime = etEndTime.getText().toString();
              String cost = etCost.getText().toString();
              String description = "";
              int costForCalc = Integer.parseInt(cost);
              addActivity(date, location, description, startTime, endTime, 0, 0, cost);
              NoteInfo noteInfo = new NoteInfo();
              noteInfo.setLocation(location);
              noteInfo.setStartTime(startTime);
              noteInfo.setEndTime(endTime);
              noteInfo.setCost(cost);
              noteInfo.setTripID(tripID);
              noteInfo.setDate(dateInfo.getDate());
              dateInfo.getNodes().add(noteInfo);
              dateInfoAdapter.notifyDataSetChanged();

              DatabaseReference costDatabase = mDatabase.getReference().child("trip").child(tripID);
              HashMap<String,Object> newCost = new HashMap<>();
              totalCost = totalCost + costForCalc;
              newCost.put("cost",(totalCost));
              costDatabase.updateChildren(newCost);

          });
          ad1.setNegativeButton("No", (dialog, i) -> {
              dialog.dismiss();
          });
          ad1.show();
      }




  }
