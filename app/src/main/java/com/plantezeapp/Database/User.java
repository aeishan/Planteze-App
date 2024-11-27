package com.plantezeapp.Database;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.plantezeapp.MainActivity;
import com.plantezeapp.R;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;


public class User{
    private String uID;
    private String email;
    private CarbonFootprint carbonFootprint;
    private EcoTracker ecoTracker;

    private Boolean done;

    // Firebase reference
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase;

    // Constructor
    public User(String uID, String email) {
        this.uID = uID;
        this.email = email;
        this.done = false;
        this.carbonFootprint = new CarbonFootprint();
        this.ecoTracker = new EcoTracker();
        this.mDatabase = database.getReference("users").child(uID);
    }

    public void saveToFirebase() {
        mDatabase.child("email").setValue(email);
        mDatabase.child("carbonFootprint").setValue(carbonFootprint.toMap());  // Assuming toMap() method exists
        mDatabase.child("ecoTracker").setValue(ecoTracker.toMap());  // Assuming toMap() method exists
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CarbonFootprint getCarbonFootprint() {
        return carbonFootprint;
    }

    public EcoTracker getEcoTracker() {
        return ecoTracker;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

}







