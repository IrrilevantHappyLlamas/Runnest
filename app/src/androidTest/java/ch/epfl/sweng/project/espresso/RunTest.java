package ch.epfl.sweng.project.espresso;

import android.os.SystemClock;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RunTest extends EspressoTest {

    @Test
    public void startAndStopRun() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run));

        //TODO
        SystemClock.sleep(FIREBASE_DURATION);

        onView(isRoot()).perform(waitForMatch(withId(R.id.start_run), UI_TEST_TIMEOUT));
        onView(withId(R.id.start_run)).perform(click());

        SystemClock.sleep(MOCK_LOCATION_DURATION);

        onView(isRoot()).perform(waitForMatch(withId(R.id.stop_run), UI_TEST_TIMEOUT));
        onView(withId(R.id.stop_run)).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.button_history), UI_TEST_TIMEOUT));
        onView(withId(R.id.button_history)).perform(click());
    }

    @Test
    public void startAndAbortRun() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run));

        //TODO
        SystemClock.sleep(FIREBASE_DURATION);

        onView(isRoot()).perform(waitForMatch(withId(R.id.start_run), UI_TEST_TIMEOUT));
        onView(withId(R.id.start_run)).perform(click());

        SystemClock.sleep(MOCK_LOCATION_DURATION);

        //Press on CANCEL
        pressBack();
        tryIsDisplayed(withId(android.R.id.button2), UI_TEST_TIMEOUT);
        onView(withId(android.R.id.button2)).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.stop_run), UI_TEST_TIMEOUT));

        pressBack();
        tryIsDisplayed(withId(android.R.id.button1), UI_TEST_TIMEOUT);
        onView(withId(android.R.id.button1)).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), UI_TEST_TIMEOUT));
    }

    @Test
    public void deleteRun() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run));

        //TODO
        SystemClock.sleep(FIREBASE_DURATION);

        onView(isRoot()).perform(waitForMatch(withId(R.id.start_run), UI_TEST_TIMEOUT));
        onView(withId(R.id.start_run)).perform(click());

        SystemClock.sleep(MOCK_LOCATION_DURATION);

        onView(isRoot()).perform(waitForMatch(withId(R.id.stop_run), UI_TEST_TIMEOUT));
        onView(withId(R.id.stop_run)).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.button_delete), UI_TEST_TIMEOUT));
        onView(withId(R.id.button_delete)).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.tabs), UI_TEST_TIMEOUT));
    }

    @Test
    public void displayRun() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_history));

        onView(isRoot()).perform(waitForMatch(withId(R.id.tabs), UI_TEST_TIMEOUT));
        onView(allOf(withText("Run History"), isDescendantOfA(withId(R.id.tabs)))).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
    }
}
