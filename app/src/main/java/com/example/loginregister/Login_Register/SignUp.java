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

import com.example.loginregister.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

public class SignUp extends AppCompatActivity {

    TextInputEditText textInputEditTextFullname,textInputEditTextUsername,textInputEditTextPassword,textInputEditTextEmail;
    Button buttonSignUp;
    TextView textViewLogin;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    String userId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textInputEditTextUsername = findViewById(R.id.Username);
        textInputEditTextFullname = findViewById(R.id.fullname);
        textInputEditTextEmail = findViewById(R.id.emailss);
        textInputEditTextPassword = findViewById(R.id.password);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textViewLogin = findViewById(R.id.loginText);
        progressBar = findViewById(R.id.progress);
        firebaseAuth = FirebaseAuth.getInstance();


        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start ProgressBar first (Set visibility VISIBLE)
                progressBar.setVisibility(View.VISIBLE);
                String fullname,username,password,email;
                fullname = String.valueOf(textInputEditTextFullname.getText());
                username = String.valueOf(textInputEditTextUsername.getText());
                password = String.valueOf(textInputEditTextPassword.getText()); //a minimum of 6 characters required
                email = String.valueOf(textInputEditTextEmail.getText());
                mDatabase = FirebaseDatabase.getInstance().getReference();

                if(!fullname.equals("") && !username.equals("") && !password.equals("") && !email.equals("")) {
                    firebaseAuth.createUserWithEmailAndPassword(textInputEditTextEmail.getText().toString(),textInputEditTextPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //End ProgressBar (Set visibility to GONE)
                            progressBar.setVisibility(View.GONE);
                            //if the user is successfully created in the firebase Authentication
                            if(task.isSuccessful()){
                                firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //if the verification email is sent successfully(the user have not verified yet)
                                        if(task.isSuccessful()){
                                            userId = firebaseAuth.getCurrentUser().getUid();
                                            writeNewUser(userId,fullname,email,username, false);
                                            Toast.makeText(SignUp.this, "registered successful, Please verify your email", Toast.LENGTH_LONG).show();
                                        }else{
                                            Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });

                                //if signup success, this opens login activity
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"All fields are required", Toast.LENGTH_SHORT).show();
                };

            };
        });

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void writeNewUser(String userId, String fullname,String email, String username, boolean verified) {
        User user = new User(fullname,email,username,verified);
        mDatabase.child("users").child(userId).setValue(user);
    }

    @IgnoreExtraProperties
    public class User {

        public String username;
        public String email;
        public String fullName;
        public boolean verified;
        public String phone;



        public User(String fullname, String email,String username,boolean verified) {
            this.fullName = fullname;
            this.email = email;
            this.username = username;
            this.verified = verified;
        }

        public String getName() {
            return username;
        }
    }
}