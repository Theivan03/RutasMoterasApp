package com.RutasMoteras.rutasmoterasapp;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.widget.EditText;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.MockitoAnnotations;

import org.junit.Before;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
@RunWith(MockitoJUnitRunner.class)
public class EditInfoUserTest {

    @Mock
    private SharedPreferences sharedPrefs;
    @Mock
    private SharedPreferences.Editor editor;
    @Mock
    private Context context;
    @Mock
    private InputStream inputStream;

    private EditInfoUser activity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);
        when(sharedPrefs.edit()).thenReturn(editor);
        when(editor.putString(anyString(), anyString())).thenReturn(editor);
        activity = new EditInfoUser();
        activity.sharedURL = sharedPrefs;
    }

    @Test
    public void testGuardarUser() throws Exception {
        activity.nombre = new EditText(context);
        activity.apellidos = new EditText(context);
        activity.email = new EditText(context);
        activity.ciudad = new EditText(context);
        activity.codigoPostal = new EditText(context);

        activity.nombre.setText("John");
        activity.apellidos.setText("Doe");
        activity.email.setText("john@example.com");
        activity.ciudad.setText("City");
        activity.codigoPostal.setText("12345");

        activity.GuardarUser();

        verify(editor).putString("Name", "John");
        verify(editor).putString("Surname", "Doe");
        verify(editor).putString("Email", "john@example.com");
        verify(editor).putString("City", "City");
        verify(editor).putString("postalCode", "12345");
        verify(editor).apply();
    }

    @Test
    public void testConvertirImagenABase64() throws Exception {
        when(context.getContentResolver().openInputStream(any(Uri.class))).thenReturn(inputStream);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.userwhothoutphoto);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        assertEquals(Base64.encodeToString(imageBytes, Base64.DEFAULT), activity.convertirImagenABase64(Uri.parse("file://dummy/path")));
    }
}
