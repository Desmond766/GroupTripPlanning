package com.example.loginregister.ui.me;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.example.loginregister.Login_Register.Login;
import com.example.loginregister.Login_Register.ResetPasswordActivity;
import com.example.loginregister.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SettingsFragment extends PreferenceFragment{
    private DatabaseReference mDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        // below line is used to add preference
        // fragment from our xml folder.
        addPreferencesFromResource(R.xml.preferences);


//        In preferences there are different types of preferences which are listed below :
//
//        EditTextPreference: this is used to get the text from the user.
//        ListPreference: this option is used to display a dialog with the list of options to choose from.
//        CheckBoxPreference: this option is used to display a checkbox to toggle a setting.
//        SwitchPreference: this option is used to turn the switch on and off.
//        RingtonePreference: this option is used to open the ringtone page of your device.
//        Preference with an Intent action android.intent.action.VIEW – to open an external browser navigating to an URL.
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        //Preference reset Email
        Preference reset_email = (Preference) findPreference("Reset Email");
        reset_email.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //AlertDialog is quite useful by poping out without input inadvance
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Enter New Email");

                final EditText NewEmail = new EditText(getActivity());
                NewEmail.setHint("e.g  123456@gmail.com");
                builder.setView(NewEmail);
                builder.setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String newEmail = NewEmail.getText().toString();
                                firebaseUser.verifyBeforeUpdateEmail(newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(),"check your new email",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return false;
            }
        });
        Preference changeNumber = findPreference("Wanna edit your phone number?");
        changeNumber.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onPreferenceClick(Preference preference) {
               AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
               builder.setTitle("Enter your phone number");
               final EditText newPhone = new EditText(getActivity());
               builder.setView(newPhone);
               builder.setCancelable(false)
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               String phoneNumber = newPhone.getText().toString();
                               HashMap<String,Object> newNumber = new HashMap<String,Object>();
                               newNumber.put("phone",phoneNumber);
                               mDatabase.child("users").child(firebaseUser.getUid()).updateChildren(newNumber);
                               Toast.makeText(getContext(),"Your number has been changed 😊",Toast.LENGTH_LONG).show();
                           }
                       })
               .setNegativeButton("No",null)
               .show();
                return false;
            }
        });

        //Preference forget Password
        Preference forgetPassword = (Preference) findPreference("Forget Password? Click here");
        forgetPassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getContext())
                        .setMessage("Are you sure you want to reset your Password?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getContext(), ResetPasswordActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return false;
            }
        });

        //Preference LogOut
        Preference logOut = (Preference) findPreference("btnlogOut");
        logOut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getContext())
                        .setMessage("Are you sure you want to LogOut?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getContext(), Login.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return false;
            }
        });


        //EditTextPreference:feedback(done)
        EditTextPreference editTextPreference = (EditTextPreference) findPreference("feedback");
        editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //store feedback into database
                String feedback = o.toString();
                mDatabase.child("Feedback").child(firebaseAuth.getCurrentUser().getUid()).push().setValue(feedback);
                //set Summary on our EditText
                //editTextPreference.setSummary("Entered Name is  " + editTextPreference.getText());
                Toast.makeText(getContext(),"your feedback is received",Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }
}
