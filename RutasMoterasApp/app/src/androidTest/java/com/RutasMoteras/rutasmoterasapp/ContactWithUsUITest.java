package com.RutasMoteras.rutasmoterasapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.RutasMoteras.rutasmoterasapp.R;
@RunWith(AndroidJUnit4.class)
public class ContactWithUsUITest {
    @Rule
    public ActivityScenarioRule<ContactWithUs> activityRule = new ActivityScenarioRule<>(ContactWithUs.class);

    @Test
    public void testSendButtonTriggersEmailSend() {
        // Verificar que los campos de texto y el botón están presentes
        onView(withId(R.id.nameUser)).perform(typeText("Prueba"));
        onView(withId(R.id.messageUser)).perform(typeText("Mensaje de prueba"), closeSoftKeyboard());

        // Simular el click en el botón de enviar
        onView(withId(R.id.sendUser)).perform(click());

        // Verificar que se muestra el ProgressDialog
        onView(withText(R.string.enviandoCorreo)).check(matches(isDisplayed()));
    }
}
