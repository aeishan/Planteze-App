package com.plantezeapp;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.google.firebase.auth.FirebaseUser;
import com.plantezeapp.UserLogin.LoginContract;
import com.plantezeapp.UserLogin.LoginModel;
import com.plantezeapp.UserLogin.LoginPresenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class LoginPresenterTest {

    @Mock
    private LoginContract.View mockLoginView;

    @Mock
    private LoginModel mockLoginModel;

    private LoginPresenter loginPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        loginPresenter = new LoginPresenter(mockLoginView);
        loginPresenter.mLoginModel = mockLoginModel;
    }

    @Test
    public void login_WithValidInput_CallsFirebaseAuthLogin() {

        String email = "test@example.com";
        String password = "password123";

        loginPresenter.login(email, password);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);

        verify(mockLoginModel).firebaseAuthLogin(emailCaptor.capture(), passwordCaptor.capture());

        assertEquals("test@example.com", emailCaptor.getValue());
        assertEquals("password123", passwordCaptor.getValue());
    }

    @Test
    public void login_WithEmptyEmail_ShowsEmailError() {

        String email = "";
        String password = "password123";

        loginPresenter.login(email, password);

        verify(mockLoginView).setEmailError("Correct email is required.");
        verify(mockLoginModel, never()).firebaseAuthLogin(anyString(), anyString());
    }

    @Test
    public void login_WithEmptyPassword_ShowsPasswordError() {
        String email = "test@example.com";
        String password = "";

        loginPresenter.login(email, password);

        verify(mockLoginView).setPasswordError("Correct password is required.");
        verify(mockLoginModel, never()).firebaseAuthLogin(anyString(), anyString());
    }

    @Test
    public void login_WithShortPassword_ShowsPasswordError() {
        String email = "test@example.com";
        String password = "123";

        loginPresenter.login(email, password);

        verify(mockLoginView).setPasswordError("Password must be at least 6 characters.");
        verify(mockLoginModel, never()).firebaseAuthLogin(anyString(), anyString());
    }

    @Test
    public void onSuccess_NotifiesViewOfSuccess() {
        FirebaseUser mockUser = mock(FirebaseUser.class);

        loginPresenter.onSuccess(mockUser);

        ArgumentCaptor<FirebaseUser> userCaptor = ArgumentCaptor.forClass(FirebaseUser.class);
        verify(mockLoginView).onLoginSuccess(userCaptor.capture());

        assertEquals(mockUser, userCaptor.getValue());
    }

    @Test
    public void onFailure_NotifiesViewOfFailure() {
        String errorMessage = "Login failed";

        loginPresenter.onFailure(errorMessage);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockLoginView).onLoginFailure(messageCaptor.capture());

        assertEquals("Login failed", messageCaptor.getValue());
    }
}
