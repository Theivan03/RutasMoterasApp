package com.RutasMoteras.rutasmoterasapp;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SingIn2UITest {

    @Rule
    public ActivityTestRule<SingIn2> activityRule = new ActivityTestRule<>(SingIn2.class);

    @Test
    public void testFormSubmission() {
        onView(withId(R.id.email)).perform(typeText("user@example.com"));
        onView(withId(R.id.contraseña)).perform(typeText("Password1"));
        onView(withId(R.id.crear)).perform(click());

    }

    @Test
    public void testFormInputErrors() {
        onView(withId(R.id.email)).perform(typeText("user"));
        onView(withId(R.id.crear)).perform(click());

        onView(withId(R.id.contraseña)).perform(typeText("short"));
        onView(withId(R.id.crear)).perform(click());
    }
}

