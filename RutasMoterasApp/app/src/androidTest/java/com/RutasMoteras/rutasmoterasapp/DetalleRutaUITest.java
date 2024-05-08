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
public class DetalleRutaUITest {

    @Rule
    public ActivityTestRule<DetalleRuta> activityRule = new ActivityTestRule<>(DetalleRuta.class);

    @Test
    public void testUIElementsDisplayed() {
        // Verifica que los TextViews y ImageView estén en la pantalla
        onView(withId(R.id.TipoMoto)).check(matches(isDisplayed()));
        onView(withId(R.id.Titulo)).check(matches(isDisplayed()));
        onView(withId(R.id.Fecha)).check(matches(isDisplayed()));
        onView(withId(R.id.Comunidad)).check(matches(isDisplayed()));
        onView(withId(R.id.Decripcion)).check(matches(isDisplayed()));
        onView(withId(R.id.imgRuta)).check(matches(isDisplayed()));
        onView(withId(R.id.visitar)).check(matches(isDisplayed()));
    }

    @Test
    public void testVisitarButtonFunctionality() {
        onView(withId(R.id.visitar)).perform(click());
        // Verificar que se lanza la actividad MostrarUser
        // Esto requiere un intento declarado con el contexto apropiado y el elemento UI
    }
}
