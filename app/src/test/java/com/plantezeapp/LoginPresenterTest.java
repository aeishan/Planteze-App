package com.plantezeapp;

import static org.mockito.Mockito.*;
import static org.mockito.kotlin.VerificationKt.verify;

import com.google.firebase.auth.FirebaseUser;
import com.plantezeapp.UserLogin.LoginContract;
import com.plantezeapp.UserLogin.LoginModel;
import com.plantezeapp.UserLogin.LoginPresenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

public class LoginPresenterTest {

    @Mock
    private LoginContract.View mockLoginView;

    @Mock
    private LoginModel mockLoginModel;

    private LoginPresenter loginPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Create a new instance of LoginPresenter
        loginPresenter = new LoginPresenter(mockLoginView);

        // Use reflection to set the private field `mLoginModel` to the mocked LoginModel
        Field modelField = LoginPresenter.class.getDeclaredField("mLoginModel");
        modelField.setAccessible(true); // Make the private field accessible
        modelField.set(loginPresenter, mockLoginModel); // Set the mocked LoginModel
    }

    @Test
    public void login_WithValidInput_CallsFirebaseAuthLogin() {
        String email = "test@example.com";
        String password = "password123";

        loginPresenter.login(email, password);

        verify(mockLoginModel).firebaseAuthLogin(email, password);
    }

    @Test
    public void login_WithEmptyEmail_ShowsEmailError() {
        loginPresenter.login("", "password123");

        verify(mockLoginView).setEmailError(anyString());
        verify(mockLoginModel, never()).firebaseAuthLogin(anyString(), anyString());
    }

    @Test
    public void login_WithEmptyPassword_ShowsPasswordError() {
        loginPresenter.login("test@example.com", "");

        verify(mockLoginView).setPasswordError(anyString());
        verify(mockLoginModel, never()).firebaseAuthLogin(anyString(), anyString());
    }

    @Test
    public void login_WithShortPassword_ShowsPasswordError() {
        loginPresenter.login("test@example.com", "123");

        verify(mockLoginView).setPasswordError(anyString());
        verify(mockLoginModel, never()).firebaseAuthLogin(anyString(), anyString());
    }

    @Test
    public void onSuccess_NotifiesViewOfSuccess() {
        FirebaseUser mockUser = mock(FirebaseUser.class);

        loginPresenter.onSuccess(mockUser);

        verify(mockLoginView).onLoginSuccess(mockUser);
    }

    @Test
    public void onFailure_NotifiesViewOfFailure() {
        String errorMessage = "Login failed";

        loginPresenter.onFailure(errorMessage);

        verify(mockLoginView).onLoginFailure(errorMessage);
    }
}
