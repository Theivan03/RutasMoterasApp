package com.RutasMoteras.rutasmoterasapp;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.content.Context;
import android.content.SharedPreferences;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.Before;

@RunWith(MockitoJUnitRunner.class)
public class UserTest {

    @Mock
    private SharedPreferences sharedPrefs;
    @Mock
    private SharedPreferences.Editor editor;
    @Mock
    private Context context;

    private User userActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userActivity = new User();
        userActivity.sharedURL = sharedPrefs;
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);
        when(sharedPrefs.edit()).thenReturn(editor);
        when(editor.putString(anyString(), anyString())).thenReturn(editor);
        when(editor.putLong(anyString(), anyLong())).thenReturn(editor);
        when(sharedPrefs.getString("URL", "")).thenReturn("http://fakeapi.com");
    }

    @Test
    public void testUserLoadData() {
        userActivity.onCreate(null);
        verify(sharedPrefs, times(2)).getString(anyString(), any());
        assertNotNull(userActivity.nombre);
        assertNotNull(userActivity.apellidos);
    }

    @Test
    public void testApiCall() {
        userActivity.LLamarApi("http://fakeapi.com/api/rutas");
        verify(sharedPrefs).getString("LoginResponse", null);
        // Verificar que se llama a la API con el token adecuado.
    }
}

