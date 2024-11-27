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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class UserDatabase{
    


     FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference  = database.getInstance("https://planetze-app-tracker-default-rtdb.firebaseio.com/").getReference();







}

//Connection to the realtime data base

//Read all database

//Fetch specific user database for carbon footprint and eco tracker

//Get all carbonfoot print answers so, can calculate carbon emissions and eco-tracker answers

//Onbaording setting true once the data has been fetched


//Read and Write database (eco-tracker needs to be able to edit answers)




