package com.RutasMoteras.rutasmoterasapp;
import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

@RunWith(MockitoJUnitRunner.class)
public class DetalleRutaTest {

    private DetalleRuta detalleRuta;

    @Mock
    Context mockContext;
    @Mock
    FileInputStream mockFileInputStream;
    @Mock
    InputStreamReader mockInputStreamReader;
    @Mock
    BufferedReader mockBufferedReader;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        detalleRuta = new DetalleRuta();
        when(mockContext.openFileInput(anyString())).thenReturn(mockFileInputStream);
        when(mockFileInputStream.getChannel()).thenReturn(null);
        when(new InputStreamReader(any(FileInputStream.class))).thenReturn(mockInputStreamReader);
        when(new BufferedReader(any(InputStreamReader.class))).thenReturn(mockBufferedReader);
    }

    @Test
    public void testLeerRutaDesdeArchivo() throws Exception {
        String expectedOutput = "ruta de prueba";
        when(mockBufferedReader.readLine()).thenReturn("ruta de prueba", (String) null);
        assertEquals(expectedOutput, detalleRuta.leerRutaDesdeArchivo());
    }
}
