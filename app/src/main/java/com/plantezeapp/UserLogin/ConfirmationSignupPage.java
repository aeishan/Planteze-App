package com.plantezeapp.UserLogin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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
import com.plantezeapp.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ConfirmationSignupPage extends AppCompatActivity {

    private EditText edit_email;
    private Button forgotPassButton, backButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirmation_signup_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edit_email  = findViewById(R.id.edit_email);
        forgotPassButton = findViewById(R.id.forgotPassButton);
        backButton = findViewById(R.id.backButton);

        forgotPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edit_email.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(ConfirmationSignupPage.this,"Please enter your registered email.", Toast.LENGTH_SHORT).show();
                    edit_email.setError("Email is required.");
                    edit_email.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(ConfirmationSignupPage.this,"Please enter a valid email.", Toast.LENGTH_SHORT).show();
                    edit_email.setError("Valid email is required.");
                    edit_email.requestFocus();
                }else{
                    resetPassword(email);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginView.class));
            }
        });

    }

    private void resetPassword(String email){
        mAuth = FirebaseAuth.getInstance();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ConfirmationSignupPage.this, "Please check your inbox for reset password link.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ConfirmationSignupPage.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}