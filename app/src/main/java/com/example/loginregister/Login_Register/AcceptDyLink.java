package com.example.loginregister.Login_Register;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loginregister.MainActivity2;
import com.example.loginregister.R;
import com.example.loginregister.ui.home.bean.GroupUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

public class AcceptDyLink extends AppCompatActivity {
    private DatabaseReference mDatabase;
    String TAG ="splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_dy_link);
        FirebaseAuth firebaseAuth;
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            String referlink = deepLink.toString();
                            try {
                                referlink = referlink.substring(referlink.lastIndexOf("=")+1);
                                String ownerUid = referlink.substring(0, referlink.indexOf("-"));// not useful for now
                                String tripId = referlink.substring(referlink.indexOf("-")+1);// use this tripId as GroupId to generate group discussion

                                String memberId = firebaseAuth.getCurrentUser().getUid();

                                //store this participant inside child: UID
                                UserTrip userTrip = new UserTrip(tripId,false,false,ownerUid);
                                mDatabase.child("UID").child(firebaseAuth.getCurrentUser().getUid()).child(tripId).setValue(userTrip);
                                //this opens TripDetail_FragementContainer activity
                                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                                startActivity(intent);
                                finish();

                                mDatabase.child("Group").child(tripId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        System.out.println("data is -----"+ dataSnapshot.getValue().toString());
                                        if(dataSnapshot.getValue().toString().contains(memberId)){
                                            Toast.makeText(AcceptDyLink.this, "this participant already in the Group", Toast.LENGTH_SHORT).show();
                                        }else{
                                            mDatabase.child("users").child(memberId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                    String username;
                                                    for (DataSnapshot ds : task.getResult().getChildren()){
                                                        if (ds.getKey().equals("username")){
                                                            username = ds.getValue().toString();
                                                            GroupUser newMember = new GroupUser(memberId,username);
                                                            mDatabase.child("Group").child(tripId).child(memberId).setValue(newMember);
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println("fail to get Discussion:  "+ e.getMessage());
                                    }
                                });
                            }catch (Exception e){
                                Log.e(TAG, " error "+e.toString());
                            }
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "getDynamicLink:onFailure", e);
                    }
                });
    }

    class UserTrip{
        public String tripid;
        public boolean TimeVoted;
        public boolean BudgetVoted;
        public String ownerId;
        public UserTrip(){

        }
        public UserTrip(String tripid,boolean timeVoted,boolean budgetVoted,String ownerId){
            this.tripid = tripid;
            this.BudgetVoted = budgetVoted;
            this.TimeVoted = timeVoted;
            this.ownerId = ownerId;
        }

    }

}
