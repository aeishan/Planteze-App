package com.plantezeapp.UserLogin;

import android.app.Activity;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseUser;

import com.plantezeapp.R;

public interface LoginContract {

    interface View{
        void onLoginSuccess(FirebaseUser user);
        void onLoginFailure(String message);
        void setEmailError(String error);
        void setPasswordError(String error);

    }
    interface Presenter{
        void login(String email, String password);
    }
    interface onLoginListener{
        void onSuccess(FirebaseUser user);
        void onFailure(String message);
    }

}
