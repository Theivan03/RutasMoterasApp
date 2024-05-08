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
public class SingIn2Test {

    @Mock
    private SharedPreferences sharedPrefs;
    @Mock
    private SharedPreferences.Editor editor;
    @Mock
    private Context context;

    private SingIn2 singIn2Activity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        singIn2Activity = new SingIn2();
        singIn2Activity.sharedURL = sharedPrefs;
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);
        when(sharedPrefs.edit()).thenReturn(editor);
        when(editor.putString(anyString(), anyString())).thenReturn(editor);
        when(editor.putLong(anyString(), anyLong())).thenReturn(editor);
    }

    @Test
    public void testEmailValidation() {
        assertTrue(singIn2Activity.validarEmail("test@example.com"));
        assertFalse(singIn2Activity.validarEmail("testexample.com"));
    }

    @Test
    public void testPasswordValidation() {
        assertNull(singIn2Activity.validarContraseña("Valid1Password!"));
        assertNotNull(singIn2Activity.validarContraseña("short"));
    }

    @Test
    public void testUserRegistration() {
        singIn2Activity.registrarUsuario("Password1", "John", "Doe", "john@example.com", new int[]{1});

        verify(sharedPrefs).getString("URL", "");
        // Verificar que la API se llama correctamente
    }
}
