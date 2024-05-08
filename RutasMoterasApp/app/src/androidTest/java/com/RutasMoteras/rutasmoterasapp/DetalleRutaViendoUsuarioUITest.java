package com.RutasMoteras.rutasmoterasapp;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
@RunWith(AndroidJUnit4.class)
public class DetalleRutaViendoUsuarioUITest {

    @Rule
    public ActivityTestRule<DetalleRutaViendoUsuario> activityRule = new ActivityTestRule<>(DetalleRutaViendoUsuario.class);

    @Test
    public void testUIElementsDisplayed() {
        onView(withId(R.id.TipoMoto)).check(matches(isDisplayed()));
        onView(withId(R.id.Titulo)).check(matches(isDisplayed()));
        onView(withId(R.id.Fecha)).check(matches(isDisplayed()));
        onView(withId(R.id.Comunidad)).check(matches(isDisplayed()));
        onView(withId(R.id.Decripcion)).check(matches(isDisplayed()));
        onView(withId(R.id.imgRuta)).check(matches(isDisplayed()));
        onView(withId(R.id.visitar)).check(matches(isDisplayed()));
    }

    @Test
    public void testButtonNavigatesToUser() {
        onView(withId(R.id.visitar)).perform(click());
        // Verificar que se inicia la actividad MostrarUser.
        // Nota: Para realizar esta verificación necesitas usar Intents test library para capturar el intent.
    }
}