package com.RutasMoteras.rutasmoterasapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PantallaInicialUITest {

    @Rule
    public ActivityTestRule<PantallaInicial> activityRule = new ActivityTestRule<>(PantallaInicial.class);

    @Test
    public void testButtonNavigations() {
        onView(withId(R.id.SinUsuario)).perform(click());
        // assert that RutasList activity is opened.
        onView(withId(R.id.ConUsuario)).perform(click());
        // assert that Login activity is opened.
    }
}
