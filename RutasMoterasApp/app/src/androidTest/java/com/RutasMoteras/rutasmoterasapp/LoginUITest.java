package com.RutasMoteras.rutasmoterasapp;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginUITest {

    @Rule
    public ActivityTestRule<Login> activityRule = new ActivityTestRule<>(Login.class);

    @Test
    public void testLoginButtonPress() {
        onView(withId(R.id.iniciar)).perform(click());
        onView(withId(R.id.nombre)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToRegister() {
        onView(withId(R.id.registrar)).perform(click());
        onView(withId(R.id.nombre)).check(matches(isDisplayed())); // assuming 'nombre' is also part of the registration activity
    }
}
