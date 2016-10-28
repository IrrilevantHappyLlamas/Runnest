package ch.epfl.sweng.project;


/**
 * Created by riccardoconti on 25.10.16.
 */

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import ch.epfl.sweng.project.Activities.LoginActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginTest {


    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);


   @Test
    public void loginAndLogout() {
       /*
        onView(withId(R.id.sign_in_button))
                .perform(click());

        onView(withId(R.id.fragment_container))
                .check(matches(isDisplayed()));

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_logout));

        onView(withText("OK"))
                .perform(click());

        onView(withId(R.id.sign_in_button))
                .check(matches(isDisplayed()));
                */
    }

    /*@Test
    public void a() {
        onView(withId(R.id.sign_in_button))
                .perform(click());
        onView(withId(R.id.fragment_container))
                .check(matches(isDisplayed()));
    }

    @Test
    public void b() {
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
    }*/







}