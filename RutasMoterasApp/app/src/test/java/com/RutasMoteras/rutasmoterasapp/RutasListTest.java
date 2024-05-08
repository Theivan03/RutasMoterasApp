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
public class RutasListTest {

    @Mock
    private SharedPreferences sharedPrefs;
    @Mock
    private SharedPreferences.Editor editor;
    @Mock
    private Context context;

    private RutasList rutasListActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        rutasListActivity = new RutasList();
        rutasListActivity.sharedURL = sharedPrefs;
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);
        when(sharedPrefs.edit()).thenReturn(editor);
        when(editor.putString(anyString(), anyString())).thenReturn(editor);
        when(editor.putLong(anyString(), anyLong())).thenReturn(editor);
    }

    @Test
    public void testRutasLoad() {
        rutasListActivity.LLamarApi("mock_url");

        verify(sharedPrefs).getString("LoginResponse", null);
        // Assure that the ListView adapter is set
        assertNotNull(rutasListActivity.miListaRutas.getAdapter());
    }
}

