
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

import com.plantezeapp.Database.User;
import com.plantezeapp.MainActivity;
import com.plantezeapp.AnnualCarbonFootprint.IntroPage;
import com.plantezeapp.EcoGauge.LineGraph;
import com.plantezeapp.R;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.plantezeapp.Database.FirebaseHelper;

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

    @Override
    public void onUserFetched(User user) {
        Log.d("THIS ONE", "YES??");
        // You already defined this method to navigate to MainActivity
        Intent intent = new Intent(getApplicationContext(), LineGraph.class);
        Log.d("THIS ONE", "MADE IT");
        startActivity(intent);
    }

    @Override
    public void onFetchError(String error) {
        Log.d("THIS ONE", "YES??");
        // You already defined this method to navigate to IntroPage
        Intent intent = new Intent(getApplicationContext(), IntroPage.class);
        Log.d("THIS ONE", "MADE IT");
        startActivity(intent);

    }




}




