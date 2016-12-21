package ch.epfl.sweng.project.espresso;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import android.widget.EditText;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AppNavigationTest extends EspressoTest {

    @Test
    public void aDrawerLayout() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
    }

    @Test
    public void navigateToRunningMap() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run));
    }

    @Test
    public void navigateToRunHistory() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_history));
    }

    @Test
    public void navigateToMessages() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));
    }

    @Test
    public void navigateToProfile() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_profile));
    }

    @Test
    public void navigateToChallengeHistory() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_history));

        onView(isRoot()).perform(waitForMatch(withId(R.id.tabs), UI_TEST_TIMEOUT));
        onView(allOf(withText("Challenges"), isDescendantOfA(withId(R.id.tabs)))).perform(click());
    }

    @Test
    public void navigateToSingleRunHistory() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_history));

        onView(isRoot()).perform(waitForMatch(withId(R.id.tabs), UI_TEST_TIMEOUT));
        onView(allOf(withText("Runs"), isDescendantOfA(withId(R.id.tabs)))).perform(click());
    }

    @Test
    public void navigateToLogout() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_logout));

        tryIsDisplayed(withId(android.R.id.button2), UI_TEST_TIMEOUT);
        onView(withText("OK")).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.sign_in_button), UI_TEST_TIMEOUT));
    }

    @Test
    public void backButtonWorks() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run));
        onView(isRoot()).perform(waitForMatch(withId(R.id.start_run),  UI_TEST_TIMEOUT));

        Espresso.pressBack();
        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout),  UI_TEST_TIMEOUT));
    }

    @Test
    public void backButtonDoesNothingIfStackEmpty() {
        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout),  UI_TEST_TIMEOUT));
        Espresso.pressBack();
        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout),  UI_TEST_TIMEOUT));
    }

    @Test
    public void searchNonExistentUser() {
        onView(withId(R.id.search)).perform(click());
        onView(isRoot()).perform(waitForMatch(withId(R.id.empty_layout),  UI_TEST_TIMEOUT));
        onView(isRoot()).perform(waitForMatch(withId(R.id.empty_layout),  UI_TEST_TIMEOUT));

        onView(isAssignableFrom(EditText.class)).perform(typeText("NonExistent"), pressKey(KeyEvent.KEYCODE_ENTER));
        tryIsDisplayed(withText("No user found."),  UI_TEST_TIMEOUT);
        onView(withText("No user found.")).check(matches(isDisplayed()));
    }
}