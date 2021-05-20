package com.example.loginregister.Login_Register;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loginregister.R;
import com.example.loginregister.ui.home.bean.GroupUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

public class ShareLinkActivity extends AppCompatActivity {
    TextView hintText;
    Button userLogout;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference discussionRef;
    public String tripID;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharelink);

        hintText = findViewById(R.id.tvHintText);
        userLogout = findViewById(R.id.btnSignOut);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        assert firebaseUser != null;
        hintText.setText("Click the button below to share you trip to other participants");

        discussionRef = FirebaseDatabase.getInstance().getReference().child("Group");

        Intent intent = getIntent();
        tripID = intent.getStringExtra("tripID");


        /*
        * logout button
        * */
        userLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ShareLinkActivity.this, Login.class);
                //remove previous activity saved in the stack, this is used for user to really log out
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        /*
        * share button
        * */
        findViewById(R.id.buttonShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String TripId = tripID;


                //create Group Discussion once the share link inside the trip is called
                discussionRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.getValue().toString().contains(TripId)){
                            createReferlink(firebaseUser.getUid(),TripId);
                            createDiscussion(TripId);
                        }else{
                            createReferlink(firebaseUser.getUid(),TripId);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    public void createDiscussion(String TripId){
        String uid = firebaseUser.getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                GroupUser groupUser = new GroupUser(uid,task.getResult().getValue().toString());
                discussionRef.child(TripId).child(uid).setValue(groupUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ShareLinkActivity.this,"share link is ready,owner is the first participant",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    /*
    *this method is to creating a share link which contain information about uid and usage of this link
    * */
    public void createReferlink(String uid, String TripId){
        FirebaseDatabase.getInstance().getReference().child("trip").child(TripId).child("uid").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String ownerid = task.getResult().getValue().toString();
                // manuall link
                String sharelinktext  = "https://tourplannerexample.page.link/?"+
                        "link=https://tourplannerexample.page.link/test/myrefer.php?custid="+ownerid +"-"+TripId+
                        "&apn="+ getPackageName()+
                        "&st="+"My Refer Link"+
                        "&sd="+"Reward Coins 20" +
                        "&si="+"https://ibb.co/GPvs9tm";// expire on 25/03/2021, need to reupload
                //&st – Title of refer link
                //&sd – Short description about refer link
                //&si – Image URL for refer link

                Log.e("mainactivity", "sharelink - "+sharelinktext);
                // shorten the link
                Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                        //.setLongLink(dynamicLink.getUri())    // enable it if using firebase method dynamicLink
                        .setLongLink(Uri.parse(sharelinktext))  // manually
                        .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().
                                setFallbackUrl(Uri.parse("https://drive.google.com/drive/folders/1n55J9VOJUVDN02O2OucoCer-AVwSWjER")).build())//open google drive when user did not install the app
                        .buildShortDynamicLink()
                        .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                            @Override
                            public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                                if (task.isSuccessful()) {
                                    // Short link created
                                    Uri shortLink = task.getResult().getShortLink();
                                    Uri flowchartLink = task.getResult().getPreviewLink();
                                    Log.e("main ", "short link "+ shortLink.toString());
                                    // share app dialog
                                    Intent intent = new Intent();

                                    intent.setAction(Intent.ACTION_SEND);
                                    intent.putExtra(Intent.EXTRA_TEXT,  shortLink.toString());
                                    intent.setType("text/plain");
                                    startActivity(Intent.createChooser(intent, "Share Link"));
                                } else {
                                    Log.e("main", " error "+task.getException() );
                                }
                            }
                        });
            }
        });

        
    }
}