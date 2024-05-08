package com.RutasMoteras.rutasmoterasapp;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class CrearRutaTest {

    @Mock
    private Context mockContext;
    @Mock
    private ContentResolver mockContentResolver;

    private CrearRuta crearRuta;

    @Before
    public void setUp() {
        crearRuta = Mockito.spy(new CrearRuta());
        Mockito.doReturn(mockContext).when(crearRuta).getApplicationContext();
        Mockito.doReturn(mockContentResolver).when(mockContext).getContentResolver();
    }

    @Test
    public void testConvertirImagenABase64() throws Exception {
        // Preparar datos de entrada
        Bitmap bitmap = BitmapFactory.decodeResource(mockContext.getResources(), R.drawable.ic_launcher_background);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        InputStream imageInputStream = new ByteArrayInputStream(imageBytes);

        // Configurar el entorno de mocks
        Uri fakeUri = Uri.parse("content://com.RutasMoteras.rutasmoterasapp/drawable/example_image");
        when(mockContentResolver.openInputStream(any(Uri.class))).thenReturn(imageInputStream);

        // Ejecutar la función bajo prueba
        String encodedImage = crearRuta.convertirImagenABase64(fakeUri);

        // Afirmar los resultados
        assertNotNull(encodedImage);
        assertEquals(Base64.encodeToString(imageBytes, Base64.DEFAULT), encodedImage);
    }
}