package com.RutasMoteras.rutasmoterasapp;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.junit.Assert.assertNotNull;
@RunWith(AndroidJUnit4.class)
public class DetalleRuta2Test {

    @Rule
    public ActivityTestRule<DetalleRuta2> activityRule = new ActivityTestRule<>(DetalleRuta2.class);

    @Mock
    Context mockContext;
    @Mock
    SharedPreferences mockPrefs;
    @Mock
    SharedPreferences.Editor mockEditor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPrefs);
        when(mockPrefs.getString(anyString(), anyString())).thenReturn("fake_token");
        when(mockPrefs.edit()).thenReturn(mockEditor);
    }

    @Test
    public void testLeerRutaDesdeArchivo() {
        DetalleRuta2 activity = activityRule.getActivity();
        assertNotNull("Texto de ruta no debe ser null", activity.leerRutaDesdeArchivo());
    }

    @Test
    public void testBotonInteraction() {
        onView(withId(R.id.button2)).perform(click());
        // Verifica que se inicia la actividad correspondiente al clic del botón
        // Puedes usar intending y intended (necesitas añadir más configuraciones para intents)
    }

    @Test
    public void testTextDisplays() {
        onView(withId(R.id.TipoMoto)).check(matches(isDisplayed()));
        onView(withId(R.id.Titulo)).check(matches(isDisplayed()));
        onView(withId(R.id.Fecha)).check(matches(isDisplayed()));
        onView(withId(R.id.Comunidad)).check(matches(isDisplayed()));
        onView(withId(R.id.Decripcion)).check(matches(isDisplayed()));
        onView(withId(R.id.imgRuta)).check(matches(isDisplayed()));
    }
}
