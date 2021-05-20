package com.example.loginregister.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.loginregister.R;
import com.example.loginregister.TripDetail_FragementContainer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    public static final String USERID = "USERID";
    ListView mListView;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference mDatabase;
    ArrayList<Card> trips  = new ArrayList<Card>();
    String userid = "";
    ConstraintLayout layout;
    Context context;
    CardView mCardView;
//    Button button;
    ImageView backgroundimg;
//    ArrayList<Image> background = new ArrayList<Image>();
    TypedArray tArray;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);



        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mListView = root.findViewById(R.id.listView);
        mCardView = root.findViewById(R.id.card_view);
        //mCardView.setVisibility(View.GONE);

        //button = getActivity().findViewById(R.id.toOverview);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userid = firebaseUser.getUid();
        layout = root.findViewById(R.id.whole);
        backgroundimg = root.findViewById(R.id.backgroundimage);

        //button = root.findViewById(R.id.button1);
        context = getContext();
        tArray = context.getResources().obtainTypedArray(
                R.array.images_for_my_list);
//        System.out.println("--------------------------------------------------------------------------"+tArray);
//        int count = tArray.length();
//        int[] ids = new int[count];
//        for (int i = 0; i < ids.length; i++) {
//            ids[i] = tArray.getResourceId(i, 0);
//            backgroundimg.setImageResource(ids[i]);
//
//        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child("UID").child(userid);
        ValueEventListener tripListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String,Card> trips = new HashMap<>();
                int i = 0;
                for(DataSnapshot ds : snapshot.getChildren()){
                    Card card = new Card(ds.getKey());
                    card.setnum(i);
                    i++;
                    System.out.println("the current i is"+i);
//                    ids[i] = tArray.getResourceId(i, 0);
//                    backgroundimg.setImageResource(ids[i]);
//                    System.out.println(ds.getKey());
//                    backgroundimg.setImageResource();
                    trips.put(ds.getKey(),card);
                }

                FirebaseDatabase.getInstance().getReference().child("UID").child(userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        Iterator<DataSnapshot> iterator = task.getResult().getChildren().iterator();
                        while (iterator.hasNext()){
                            DataSnapshot temp = iterator.next();
                            Card cardTemp = trips.get(temp.getKey());
                            for (DataSnapshot ds : temp.getChildren()){
                                if (ds.getKey().equals("ownerId")){

                                    cardTemp.setOwnerID(ds.getValue().toString());
                                    //Log.d("my app","OWENER IDDDDDDDDDDDDDD IS" + ds.getValue().toString());
                                    trips.replace(temp.getKey(),cardTemp);
                                }
                            }
                        }
                        Myadapter myadapter = new Myadapter(context, R.layout.activity_main_page,new ArrayList<>(trips.values()),userid);
                        mListView.setAdapter(myadapter);
                    }
                });





            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        mDatabase.addValueEventListener(tripListener);
        return root;
    }




}

class Myadapter extends ArrayAdapter<Card> {


    private Context mContext;
    private  int mResource;
    private String userid;
    private Image backgroundimage;
    private ImageView backgroundimg;
    TypedArray tArray;

    private static class ViewHolder{
        TextView title;
        CardView mCardView;
        ImageView background;

    }

    public Myadapter(Context context, int resource, ArrayList<Card> trips, String userid){
        super(context,resource,trips);
        mContext = context;
        mResource = resource;
        this.userid = userid;
        tArray = context.getResources().obtainTypedArray(
                R.array.images_for_my_list);
    }
    @NonNull
    public View getView(int position, View convertView, ViewGroup parent){
        String ownerID = getItem(position).getOwnerID();
        Log.d("my app","Owner id is" + ownerID);
        String title = getItem(position).getTitle();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(mResource,parent,false);

            holder = new ViewHolder();
            holder.title = (TextView)convertView.findViewById(R.id.Cardtitle);
            holder.mCardView = (CardView)convertView.findViewById(R.id.card_view);
            holder.background = convertView.findViewById(R.id.backgroundimage);
            holder.background.setImageResource(tArray.getResourceId(getItem(position).getNumber(), 0));


            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, TripDetail_FragementContainer.class);
                    String message = (String) holder.title.getText();
                    intent.putExtra("USERID",message);
                    mContext.startActivity(intent);
                }
            });
            holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View view) {

                    String tripID = (String) holder.title.getText();

                    ArrayList<String> members = new ArrayList<String>();
                    AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                    if (userid.equals(ownerID)){
                        alert.setMessage("You are the owner of the trip.If you delete this trip,it will also be removed at other members' sides.");
                    }
                    else{
                        alert.setMessage("Do you want to delete this trip?");
                    }
                    alert
                            .setIcon(android.R.drawable.ic_delete)
                            .setTitle("Are you sure ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(userid.equals(ownerID)){
                                        mDatabase.getReference().child("Group").child(tripID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                if(task.getResult().exists()){
                                                    for (DataSnapshot ds : task.getResult().getChildren()){
                                                        members.add(ds.getKey());
                                                    }
                                                    for (String mem : members){
                                                        System.out.print("members are " + mem);
                                                        mDatabase.getReference().child("UID").child(mem).child(tripID).setValue(null);
                                                    }
                                                }else{
                                                    mDatabase.getReference().child("UID").child(userid).child(tripID).setValue(null);
                                                }
                                                mDatabase.getReference().child("Vote").child(tripID).setValue(null);
                                                mDatabase.getReference().child("Likes").child(tripID).setValue(null);
                                                mDatabase.getReference().child("trip").child(tripID).setValue(null);
                                                mDatabase.getReference().child("Group").child(tripID).setValue(null);
                                                mDatabase.getReference().child("Chats").child("GroupChat").child(tripID).setValue(null);
//                                            Intent intent = new Intent(TripDetail_FragementContainer.this, MainActivity2.class);
//                                            TripDetail_FragementContainer.this.startActivity(intent);
                                            }
                                        });
                                    } else{
                                        DatabaseReference group  =mDatabase.getReference().child("Group").child(tripID);
                                        group.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                if (task.getResult() != null){
                                                    for (DataSnapshot ds : task.getResult().getChildren()){
                                                        Log.d("My app", "the key is" + ds.getKey() + "and the value is" + ds.getValue().toString());
                                                        if (ds.getKey().toString().equals(userid)){
                                                            String key = ds.getKey();
                                                            group.child(key).setValue(null);
                                                        }
                                                    }
                                                }

                                            }
                                        });
                                        mDatabase.getReference().child("UID").child(userid).child(tripID).setValue(null);
                                    }

                                }
                            })
                            .setNegativeButton("No",null).show();
                    return true;
                }
            });
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(title);
        //holder.button.setBackgroundColor(Color.GREEN);


        return convertView;
    }

}