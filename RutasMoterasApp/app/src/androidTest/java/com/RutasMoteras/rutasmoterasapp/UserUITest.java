package com.RutasMoteras.rutasmoterasapp;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserUITest {

    @Rule
    public ActivityTestRule<User> activityRule = new ActivityTestRule<>(User.class);

    @Test
    public void testListViewInteraction() {
        onView(withId(R.id.miListaRutas)).perform(click());
    }

    @Test
    public void testSwipeRefresh() {
        onView(withId(R.id.swipe_refresh_layout)).perform(swipeDown());
    }

    @Test
    public void testMenuInteraction() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Editar")).perform(click());
    }
}

