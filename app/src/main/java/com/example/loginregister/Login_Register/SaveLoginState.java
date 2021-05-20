package com.example.loginregister.Login_Register;

import android.app.Application;
import android.content.Intent;

import com.example.loginregister.MainActivity2;
import com.example.loginregister.chatBox.chatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//this class is used for android phone to remember the login state of users.
//so user dont have to login again when reopen the application
public class SaveLoginState extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null && firebaseUser.isEmailVerified()){

            //Intent intent = new Intent(SaveLoginState.this,TripDetail_FragementContainer.class);

            Intent intent = new Intent(SaveLoginState.this, MainActivity2.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
