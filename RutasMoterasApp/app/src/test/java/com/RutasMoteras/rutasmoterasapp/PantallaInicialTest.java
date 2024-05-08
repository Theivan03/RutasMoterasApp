package com.RutasMoteras.rutasmoterasapp;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.Before;

@RunWith(MockitoJUnitRunner.class)
public class PantallaInicialTest {

    @Mock
    private SharedPreferences sharedPrefs;
    @Mock
    private SharedPreferences.Editor editor;
    @Mock
    private Context context;

    private PantallaInicial pantallaInicialActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        pantallaInicialActivity = new PantallaInicial();
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);
        when(sharedPrefs.edit()).thenReturn(editor);
        when(editor.putString(anyString(), anyString())).thenReturn(editor);
        when(editor.putLong(anyString(), anyLong())).thenReturn(editor);
    }

    @Test
    public void testSinUsuarioButton() {
        pantallaInicialActivity.sinUsuario.performClick();

        verify(editor).putString("LoginResponse", "");
        verify(editor).putLong("TokenTimestamp", anyLong());
        verify(editor).apply();
        // Assert that the RutasList activity was started.
    }

    @Test
    public void testConUsuarioButton() {
        pantallaInicialActivity.conUsuario.performClick();

        verify(context).startActivity(any(Intent.class));
        // Assert that the Login activity was started.
    }
}
