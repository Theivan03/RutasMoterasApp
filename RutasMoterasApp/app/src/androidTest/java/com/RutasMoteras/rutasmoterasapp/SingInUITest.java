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
public class SingInUITest {

    @Rule
    public ActivityTestRule<SingIn> activityRule = new ActivityTestRule<>(SingIn.class);

    @Test
    public void testSuccessfulFormSubmission() {
        onView(withId(R.id.nombre)).perform(typeText("John"));
        onView(withId(R.id.apellidos)).perform(typeText("Doe"));
        onView(withId(R.id.siguiente)).perform(click());

    }

    @Test
    public void testFormValidationError() {
        onView(withId(R.id.nombre)).perform(typeText(""));
        onView(withId(R.id.apellidos)).perform(typeText(""));
        onView(withId(R.id.siguiente)).perform(click());

    }

}

