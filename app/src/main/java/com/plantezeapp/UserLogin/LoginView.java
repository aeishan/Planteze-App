
package com.plantezeapp.UserLogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.plantezeapp.MainActivity;
import com.plantezeapp.R;

import com.google.firebase.auth.FirebaseUser;



public class LoginView extends AppCompatActivity implements LoginContract.View {
    private EditText editTextEmail, editTextPassword;
    private Button loginButton, resetPassButton;
    private TextView signupHereText;

    private LoginPresenter mLoginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_account_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //UI elements
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.loginButton);
        signupHereText = findViewById(R.id.signupHereText);
        resetPassButton = findViewById(R.id.resetPassButton);


        mLoginPresenter = new LoginPresenter(this);

        loginButton.setOnClickListener(v ->  {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            mLoginPresenter.login(email, password);

        });

        signupHereText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterAccountPage.class));
            }
        });

        resetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginView.this, "You can reset your password now.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginView.this, ConfirmationSignupPage.class));
            }
        });


    }

    @Override
    public void onLoginSuccess(FirebaseUser user) {
        // Navigate to next screen or show success message
        Toast.makeText(LoginView.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class); //This class needs to be changed to the class with the maindashboard
        startActivity(intent);
        finish();

    }
    @Override
    public void onLoginFailure(String message) {
        Toast.makeText(LoginView.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override public void setEmailError(String error) {
        editTextEmail.setError(error);
    }
    @Override
    public void setPasswordError(String error) {
        editTextPassword.setError(error);
    }



}




