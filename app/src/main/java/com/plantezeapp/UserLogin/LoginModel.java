package com.plantezeapp.UserLogin;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class LoginModel {

    private LoginContract.onLoginListener mOnLoginListener;

    public LoginModel(LoginContract.onLoginListener onLoginListener){
        this.mOnLoginListener = onLoginListener;
    }

    public void firebaseAuthLogin (String email, String password){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                mOnLoginListener.onSuccess(user);
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











