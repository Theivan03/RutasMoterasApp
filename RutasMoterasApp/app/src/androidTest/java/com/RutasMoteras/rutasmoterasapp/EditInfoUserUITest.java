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
public class EditInfoUserUITest {

    @Rule
    public ActivityTestRule<EditInfoUser> activityRule = new ActivityTestRule<>(EditInfoUser.class);

    @Test
    public void testButtonClicks() {
        onView(withId(R.id.boton)).perform(click());
        onView(withId(R.id.botonGuardar)).perform(click());
        onView(withId(R.id.borrarFoto)).perform(click());
        // Check for expected results or new activity
    }
}
