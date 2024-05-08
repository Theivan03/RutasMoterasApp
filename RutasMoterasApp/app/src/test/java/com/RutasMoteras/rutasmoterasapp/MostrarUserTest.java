package com.RutasMoteras.rutasmoterasapp;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.content.Context;
import android.content.SharedPreferences;

import com.RutasMoteras.rutasmoterasapi.RutasModel;
import com.RutasMoteras.rutasmoterasapi.UserModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class MostrarUserTest {

    @Mock
    private SharedPreferences sharedPrefs;
    @Mock
    private SharedPreferences.Editor editor;
    @Mock
    private Context context;

    private MostrarUser activity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        activity = new MostrarUser();
        activity.sharedURL = sharedPrefs;
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);
        when(sharedPrefs.edit()).thenReturn(editor);
        when(sharedPrefs.getString(anyString(), any())).thenReturn("mock_token");
    }

    @Test
    public void testUserLoad() {
        // Simulate API response
        UserModel user = new UserModel();
        user.setName("Test User");
        user.setEmail("test@example.com");
        activity.ObtenerUsuario("mock_url");

        // Asserts
        assertNotNull(activity.nombre);
        assertEquals("Test User", activity.nombre.getText().toString());
    }

    @Test
    public void testRoutesLoad() {
        // Simulate API response
        List<RutasModel> rutas = new ArrayList<>();
        rutas.add(new RutasModel());
        activity.LLamarApi("mock_url");

        // Asserts
        assertNotNull(activity.miListaRutas.getAdapter());
        assertEquals(1, activity.miListaRutas.getAdapter().getCount());
    }
}

