package com.plantezeapp.UserLogin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.plantezeapp.AnnualCarbonFootprint.CarbonFootprintBreakdown;
import com.plantezeapp.AnnualCarbonFootprint.IntroPage;


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
import com.plantezeapp.Database.CarbonFootprint;
import com.plantezeapp.Database.FirebaseHelper;
import com.plantezeapp.Database.User;
import com.plantezeapp.MainActivity;
import com.plantezeapp.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.plantezeapp.Track.Track;
import com.plantezeapp.Track.Tracker;

public class LoginAccountPage extends AppCompatActivity implements FirebaseHelper.UserFetchListener{
    private EditText editTextEmail, editTextPassword;
    private Button loginButton, resetPassButton;
    private TextView signupHereText;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_account_page);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.loginButton);
        signupHereText = findViewById(R.id.signupHereText);
        resetPassButton = findViewById(R.id.resetPassButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                //Check if email and password is empty or wrong
                if(TextUtils.isEmpty(email)){
                    editTextEmail.setError("Correct email is required.");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    editTextPassword.setError("Correct password is required.");
                    return;
                }

                if(password.length()< 6 ){
                    editTextPassword.setError("Password must be equal to 6 characters or less.");
                }


                //Firebase Authentication
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginAccountPage.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();

                                    checkUser();


                                    FirebaseUser user = mAuth.getCurrentUser();
                                    //Onboarding code








                                } else {
                                    Toast.makeText(LoginAccountPage.this, "Invalid email or password." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
        signupHereText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterAccountPage.class));
            }
        });

        //Reset password
        resetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginAccountPage.this, "You can reset your password now.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginAccountPage.this, ConfirmationSignupPage.class));
            }
        });


    }

    @Override
    public void onUserFetched(User user) {
        Log.d("THIS ONE", "YES??");
        Intent intent = new Intent (getApplicationContext(), Tracker.class); // change to eco tracker page
        Log.d("THIS ONE", "MADE IT");
        startActivity(intent);
    }

    @Override
    public void onFetchFailed(String errorMessage) {
        Log.d("THIS ONE", "YES??");
        Intent intent = new Intent (getApplicationContext(), IntroPage.class);
        Log.d("THIS ONE", "MADE IT");
        startActivity(intent);
    }

    void checkUser(){
        FirebaseHelper help = new FirebaseHelper();
        FirebaseUser userFire = FirebaseAuth.getInstance().getCurrentUser();
        help.fetchUser(userFire.getUid(), this);

    }

}
