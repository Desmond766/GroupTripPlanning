package com.example.loginregister.Login_Register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.loginregister.MainActivity2;
import com.example.loginregister.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    TextInputEditText textInputEditTextemail,textInputEditTextPassword;
    Button buttonLogin;
    TextView textViewSignUp;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    TextView textViewResetPassword;
    private DatabaseReference mDatabase;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textInputEditTextemail = findViewById(R.id.Username);
        textInputEditTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewSignUp = findViewById(R.id.signUpText);
        progressBar = findViewById(R.id.progress);
        textViewResetPassword = findViewById(R.id.ResetPassText);

        firebaseAuth = FirebaseAuth.getInstance();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String password,email;
                email = textInputEditTextemail.getText().toString();
                password = textInputEditTextPassword.getText().toString();
                if(!email.equals("") && !password.equals("") ) {
                    //Start ProgressBar first (Set visibility VISIBLE)
                    firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            //if the account exists
                            if(task.isSuccessful()){
                                //if the account is verified
                                if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                    mDatabase = FirebaseDatabase.getInstance().getReference();
                                    String uid = firebaseAuth.getCurrentUser().getUid();
                                    updateUser(uid,true);
                                    Toast.makeText(Login.this,"Login successful",Toast.LENGTH_LONG).show();

                                    startActivity(new Intent(Login.this, MainActivity2.class));



                                }else{
                                    Toast.makeText(Login.this,"Please verify your email address",Toast.LENGTH_LONG).show();
                                }

                            }else{
                                Toast.makeText(Login.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"All fields are required", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
                finish();
            }
        });

        textViewResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void updateUser(String uid, boolean verified){
        HashMap hashMap = new HashMap();
        hashMap.put("verified",verified);
        mDatabase.child("users").child(uid).updateChildren(hashMap);
    }
}