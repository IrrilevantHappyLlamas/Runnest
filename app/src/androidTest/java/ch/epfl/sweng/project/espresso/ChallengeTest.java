package ch.epfl.sweng.project.espresso;

import android.os.SystemClock;
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

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ChallengeTest extends EspressoTest {

    @Test
    public void challengeTime() {
        onView(withId(R.id.search)).perform(click());
        onView(isRoot()).perform(waitForMatch(withId(R.id.empty_layout), UI_TEST_TIMEOUT));
        onView(isAssignableFrom(EditText.class)).perform(typeText("R"), pressKey(KeyEvent.KEYCODE_ENTER));

        tryIsDisplayed(withText("Runnest IHL"), UI_TEST_TIMEOUT);
        onView(withText("Runnest IHL")).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), UI_TEST_TIMEOUT));
        onView(withText(R.string.challenge)).perform(click());

        //Choose a time challenge
        tryIsDisplayed(withId(R.id.define_challenge), UI_TEST_TIMEOUT);
        onView(withId(R.id.time_radio)).perform(click());
        onView(withId(R.id.customize_positive_btn)).perform(click());

        //Start Challenge
        onView(isRoot()).perform(waitForMatch(withId(R.id.readyBtn), UI_TEST_TIMEOUT));
        onView(withId(R.id.readyBtn)).perform(click());

        tryIsDisplayed(withId(R.id.button_history), TIME_CHALLENGE_DURATION + UI_TEST_TIMEOUT);
    }

    @Test
    public void challengeKeepRunningQuit() {
        onView(withId(R.id.search)).perform(click());
        onView(isRoot()).perform(waitForMatch(withId(R.id.empty_layout), UI_TEST_TIMEOUT));
        onView(isAssignableFrom(EditText.class)).perform(typeText("R"), pressKey(KeyEvent.KEYCODE_ENTER));

        tryIsDisplayed(withText("Runnest IHL"), UI_TEST_TIMEOUT);
        onView(withText("Runnest IHL")).perform(click());

        //Create challenge
        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), UI_TEST_TIMEOUT));
        onView(withText(R.string.challenge)).perform(click());
        tryIsDisplayed(withId(R.id.define_challenge), UI_TEST_TIMEOUT);
        onView(withId(R.id.time_radio)).perform(click());
        onView(withId(R.id.customize_positive_btn)).perform(click());

        //Start Challenge
        onView(isRoot()).perform(waitForMatch(withId(R.id.readyBtn), UI_TEST_TIMEOUT));
        onView(withId(R.id.readyBtn)).perform(click());

        SystemClock.sleep(MOCK_LOCATION_DURATION);

        //Keep running
        onView(withId(R.id.back_to_side_btn)).perform(click());
        tryIsDisplayed(withId(android.R.id.button1), UI_TEST_TIMEOUT);
        onView(withText(R.string.keep_running)).perform(click());

        //Quit challenge
        onView(withId(R.id.back_to_side_btn)).perform(click());
        tryIsDisplayed(withId(android.R.id.button1), UI_TEST_TIMEOUT);
        onView(withText(R.string.quit)).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.button_history), UI_TEST_TIMEOUT));
    }

    @Test
    public void challengeWaitStopWait() {
        onView(withId(R.id.search)).perform(click());
        onView(isRoot()).perform(waitForMatch(withId(R.id.empty_layout), UI_TEST_TIMEOUT));
        onView(isAssignableFrom(EditText.class)).perform(typeText("R"), pressKey(KeyEvent.KEYCODE_ENTER));

        tryIsDisplayed(withText("Runnest IHL"), UI_TEST_TIMEOUT);
        onView(withText("Runnest IHL")).perform(click());

        //Create challenge
        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), UI_TEST_TIMEOUT));
        onView(withText(R.string.challenge)).perform(click());
        tryIsDisplayed(withId(R.id.define_challenge), UI_TEST_TIMEOUT);
        onView(withId(R.id.customize_positive_btn)).perform(click());

        //Wait
        onView(withId(R.id.back_to_side_btn)).perform(click());
        tryIsDisplayed(withId(android.R.id.button1), UI_TEST_TIMEOUT);
        onView(withText(R.string.wait)).perform(click());

        //Quit
        onView(withId(R.id.back_to_side_btn)).perform(click());
        tryIsDisplayed(withId(android.R.id.button1), UI_TEST_TIMEOUT);
        onView(withText(R.string.quit)).perform(click());
    }

    @Test
    public void challengeDistanceCancelThenCreate() {
        onView(withId(R.id.search)).perform(click());
        onView(isRoot()).perform(waitForMatch(withId(R.id.empty_layout), UI_TEST_TIMEOUT));
        onView(isAssignableFrom(EditText.class)).perform(typeText("R"), pressKey(KeyEvent.KEYCODE_ENTER));

        tryIsDisplayed(withText("Runnest IHL"), UI_TEST_TIMEOUT);
        onView(withText("Runnest IHL")).perform(click());

        //Tap on cancel
        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), UI_TEST_TIMEOUT));
        onView(withText(R.string.challenge)).perform(click());

        tryIsDisplayed(withId(R.id.define_challenge), UI_TEST_TIMEOUT);
        onView(withId(R.id.customize_negative_btn)).perform(click());

        //Tap challenge and create one
        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), UI_TEST_TIMEOUT));
        onView(withText(R.string.challenge)).perform(click());

        tryIsDisplayed(withId(R.id.define_challenge), UI_TEST_TIMEOUT);
        onView(withId(R.id.customize_positive_btn)).perform(click());

        //Start Challenge
        onView(isRoot()).perform(waitForMatch(withId(R.id.readyBtn), UI_TEST_TIMEOUT));
        onView(withId(R.id.readyBtn)).perform(click());

        tryIsDisplayed(withId(R.id.button_history), MOCK_LOCATION_DURATION + UI_TEST_TIMEOUT);
    }

    @Test
    public void displayChallenge() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_history));

        onView(isRoot()).perform(waitForMatch(withId(R.id.tabs), UI_TEST_TIMEOUT));
        onView(allOf(withText("Challenges"), isDescendantOfA(withId(R.id.tabs)))).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
    }

    @Test
    public void scheduleRequestCancel() {
        onView(withId(R.id.search)).perform(click());
        onView(isRoot()).perform(waitForMatch(withId(R.id.empty_layout), UI_TEST_TIMEOUT));
        onView(isAssignableFrom(EditText.class)).perform(typeText("R"), pressKey(KeyEvent.KEYCODE_ENTER));

        tryIsDisplayed(withText("Runnest IHL"), UI_TEST_TIMEOUT);
        onView(withText("Runnest IHL")).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), UI_TEST_TIMEOUT));
        onView(withText(R.string.schedule)).perform(click());

        tryIsDisplayed(withId(R.id.schedule_negative_btn), UI_TEST_TIMEOUT);
        onView(withId(R.id.schedule_negative_btn)).perform(click());
    }

    @Test
    public void scheduleRequestDistance() {
        onView(withId(R.id.search)).perform(click());
        onView(isRoot()).perform(waitForMatch(withId(R.id.empty_layout), UI_TEST_TIMEOUT));
        onView(isAssignableFrom(EditText.class)).perform(typeText("R"), pressKey(KeyEvent.KEYCODE_ENTER));

        tryIsDisplayed(withText("Runnest IHL"), UI_TEST_TIMEOUT);
        onView(withText("Runnest IHL")).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), UI_TEST_TIMEOUT));
        onView(withText(R.string.schedule)).perform(click());

        //Choose a distance challenge
        tryIsDisplayed(withId(R.id.distance_radio), UI_TEST_TIMEOUT);
        onView(withId(R.id.distance_radio)).perform(click());
        onView(withId(R.id.schedule_positive_btn)).perform(click());
    }

    @Test
    public void scheduleRequestTime() {
        onView(withId(R.id.search)).perform(click());
        onView(isRoot()).perform(waitForMatch(withId(R.id.empty_layout), UI_TEST_TIMEOUT));
        onView(isAssignableFrom(EditText.class)).perform(typeText("R"), pressKey(KeyEvent.KEYCODE_ENTER));

        tryIsDisplayed(withText("Runnest IHL"), UI_TEST_TIMEOUT);
        onView(withText("Runnest IHL")).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), UI_TEST_TIMEOUT));
        onView(withText(R.string.schedule)).perform(click());

        //Choose a time challenge
        tryIsDisplayed(withId(R.id.time_radio), UI_TEST_TIMEOUT);
        onView(withId(R.id.time_radio)).perform(click());
        onView(withId(R.id.schedule_positive_btn)).perform(click());
    }
}
