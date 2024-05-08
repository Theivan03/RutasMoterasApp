package com.RutasMoteras.rutasmoterasapp;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.Before;

import java.io.InputStream;
@RunWith(MockitoJUnitRunner.class)
public class EditRutaTest {

    @Mock
    private SharedPreferences sharedPrefs;
    @Mock
    private SharedPreferences.Editor editor;
    @Mock
    private Context context;
    @Mock
    private InputStream inputStream;

    private EditRuta activity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);
        when(sharedPrefs.edit()).thenReturn(editor);
        when(editor.putString(anyString(), anyString())).thenReturn(editor);
        activity = new EditRuta();
        activity.sharedURL = sharedPrefs;
    }

    @Test
    public void testGuardarRuta() throws Exception {
        // Configuración inicial para el test
        activity.tit = new EditText(context);
        activity.des = new EditText(context);
        activity.tit.setText("Titulo de prueba");
        activity.des.setText("Descripción de prueba");

        // Simular la acción de guardar
        activity.GuardarRuta("Titulo de prueba", "Descripción de prueba", "Comunidad", "TipoMoto", "FotoBase64");

        // Verificar que se guarda la información correctamente
        verify(sharedPrefs.edit()).putString("titulo", "Titulo de prueba");
        verify(sharedPrefs.edit()).apply();
    }
}

