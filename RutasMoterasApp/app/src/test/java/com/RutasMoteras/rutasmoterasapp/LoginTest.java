package com.RutasMoteras.rutasmoterasapp;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.content.SharedPreferences;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.Before;

@RunWith(MockitoJUnitRunner.class)
public class LoginTest {

    @Mock
    private SharedPreferences sharedPrefs;
    @Mock
    private SharedPreferences.Editor editor;
    @Mock
    private Context context;

    private Login loginActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        loginActivity = new Login();
        loginActivity.sharedURL = sharedPrefs;
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);
        when(sharedPrefs.edit()).thenReturn(editor);
        when(editor.putString(anyString(), anyString())).thenReturn(editor);
    }

    @Test
    public void loginUserTest() throws Exception {
        // Setup
        when(sharedPrefs.getString("URL", "")).thenReturn("http://api.example.com/");
        doNothing().when(editor).apply();

        // Action
        loginActivity.loginUser("password123", "user@example.com");

        // Verify
        verify(editor, atLeastOnce()).putString(eq("LoginResponse"), anyString());
        verify(editor).apply();
    }
}

