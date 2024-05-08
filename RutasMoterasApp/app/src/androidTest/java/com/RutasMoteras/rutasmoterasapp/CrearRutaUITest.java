package com.RutasMoteras.rutasmoterasapp;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class CrearRutaUITest {

    @Rule
    public ActivityScenarioRule<CrearRuta> activityScenarioRule = new ActivityScenarioRule<>(CrearRuta.class);

    @Test
    public void testBotonCrearRuta() {
        // Simulando entradas de texto
        onView(withId(R.id.tit)).perform(typeText("Ruta de Prueba"));
        onView(withId(R.id.editTextTitle2)).perform(typeText("Descripción de la ruta"));

        // Simulando el clic en el botón crear
        onView(withId(R.id.button)).perform(click());
    }
}
