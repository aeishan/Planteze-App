package com.plantezeapp.UserLogin;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import com.google.firebase.auth.FirebaseUser;

public class LoginPresenter implements LoginContract.Presenter, LoginContract.onLoginListener{
    private LoginContract.View mLoginView;
    private LoginModel mLoginModel;

    public LoginPresenter(LoginContract.View mLoginView){
        this.mLoginView = mLoginView;
        this.mLoginModel = new LoginModel(this);
    }

    @Override
    public void login(String email, String password) {
        if (checkUserLogin(email, password)) {
            mLoginModel.firebaseAuthLogin(email, password);
        }
    }

    public boolean checkUserLogin (String email, String password){
        boolean isValid = true;
        if(isEmpty(email)) {
            mLoginView.setEmailError("Correct email is required.");
            isValid = false;
        }

        if(isEmpty(password)){
            mLoginView.setPasswordError("Correct password is required.");
            isValid = false;

        }else if(password.length()< 6 ){
            mLoginView.setPasswordError("Password must be at least 6 characters.");
            isValid = false;
        }
        return isValid;

    }

    private boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    @Override
    public void onSuccess(FirebaseUser user) {
        mLoginView.onLoginSuccess(user);

    }

    @Override
    public void onFailure(String message) {
        mLoginView.onLoginFailure(message);

    }


}
