package com.RutasMoteras.rutasmoterasapp;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
@RunWith(AndroidJUnit4.class)
public class EditRutaUITest {

    @Rule
    public ActivityTestRule<EditRuta> activityRule = new ActivityTestRule<>(EditRuta.class);

    @Test
    public void testPhotoButtons() {
        // Simular el clic en el botón de seleccionar foto
        onView(withId(R.id.buttonSelectPhoto)).perform(click());
        // Asegurar que el diálogo de selección se muestra
        onView(withText(R.string.elegirOpcion)).check(matches(isDisplayed()));

        // Simular el clic en el botón de borrar foto
        onView(withId(R.id.borrarFoto)).perform(click());
    }
}