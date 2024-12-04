package com.plantezeapp.UserLogin;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.plantezeapp.Database.FirebaseHelper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.plantezeapp.Database.User;

import android.util.Log;


public class LoginModel {

    private LoginContract.onLoginListener mOnLoginListener;

    public LoginModel(LoginContract.onLoginListener onLoginListener) {

        this.mOnLoginListener = onLoginListener;
    }

    public void firebaseAuthLogin(String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                //helper method to call below
                                //Call helper.fetchUser(user.getuid(), this);
                                mOnLoginListener.onSuccess(user);
                                FirebaseHelper help = new FirebaseHelper();
                                FirebaseUser userFire = FirebaseAuth.getInstance().getCurrentUser();

                                help.fetchUser(userFire.getUid(), new FirebaseHelper.UserFetchListener(){
                                    @Override
                                    public void onUserFetched(User user) {
                                        mOnLoginListener.onUserFetched(user);
                                    }
                                    @Override
                                    public void onFetchFailed (String error) {
                                        mOnLoginListener.onFetchError(error);
                                    }
                                });

                            } else {
                                mAuth.signOut();  // Sign out the user if email is not verified
                                mOnLoginListener.onFailure("Please verify your email first.");
                            }
                        } else {
                            mOnLoginListener.onFailure("Authentication failed:" + task.getException().getMessage());
                        }
                    }
                });
    }
}











