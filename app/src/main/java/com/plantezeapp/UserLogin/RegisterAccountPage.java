package com.plantezeapp.UserLogin;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterAccountPage extends AppCompatActivity {

    private EditText editTextFName, editTextLName, editTextEmail, editTextPassword;
    private Button registerButton;
    private TextView signinHereText;

    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            mAuth.signOut();
            startActivity(new Intent(getApplicationContext(), RegisterAccountPage.class)); //Checks the user
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_account_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextFName = findViewById(R.id.editTextFName);
        editTextLName = findViewById(R.id.editTextLName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        registerButton = findViewById(R.id.registerButton);
        signinHereText = findViewById(R.id.signinHereText);


        registerButton.setOnClickListener(new View.OnClickListener() {
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
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //Send verification link when login successful
                                    FirebaseUser muser = mAuth.getCurrentUser();
                                        muser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(RegisterAccountPage.this, "Please verify your email.", Toast.LENGTH_SHORT).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(RegisterAccountPage.this, "Verification Email has not been sent." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });

                                    //Registration is successful
                                        Toast.makeText(RegisterAccountPage.this, "User created.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), LoginView.class);
                                        startActivity(intent);
                                        finish();


                                } else {
                                    Toast.makeText(RegisterAccountPage.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        signinHereText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginView.class));
            }
        });

    }


}