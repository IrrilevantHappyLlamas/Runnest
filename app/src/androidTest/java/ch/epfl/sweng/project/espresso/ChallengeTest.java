package ch.epfl.sweng.project.espresso;

import android.os.SystemClock;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import android.widget.EditText;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.model.Statement;

import ch.epfl.sweng.project.Activities.SideBarActivity;
import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Model.TestUser;

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
import static ch.epfl.sweng.project.espresso.EspressoUtils.tryIsDisplayed;
import static ch.epfl.sweng.project.espresso.EspressoUtils.waitForMatch;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ChallengeTest {
    public class Retry implements TestRule {
        private int retryCount;

        private Retry(int retryCount) {
            this.retryCount = retryCount;
        }

        public Statement apply(Statement base, Description description) {
            return statement(base, description);
        }

        private Statement statement(final Statement base, final Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    Throwable caughtThrowable = null;
                    for (int i = 0; i < retryCount; i++) {
                        try {
                            base.evaluate();
                            return;
                        } catch (Throwable t) {
                            caughtThrowable = t;
                            System.err.println(description.getDisplayName() + ": run " + (i+1) + " failed");
                        }
                    }
                    System.err.println(description.getDisplayName() + ": giving up after " + retryCount + " failures");
                    throw caughtThrowable;
                }
            };
        }
    }

    @Rule
    public ActivityTestRule<SideBarActivity> mActivityRule = new ActivityTestRule<>(
            SideBarActivity.class);

    @Rule
    public Retry retry = new Retry(3);

    @Before
    public void setUpApp() {
        ((AppRunnest) mActivityRule.getActivity().getApplication()).setUser(new TestUser());
        ((AppRunnest) mActivityRule.getActivity().getApplication()).setTestSession(true);
        ((AppRunnest) mActivityRule.getActivity().getApplication()).setNetworkHandler();
        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), EspressoUtils.UI_TEST_TIMEOUT));
    }

    @Test
    public void challengeTime() {
        onView(withId(R.id.search)).perform(click());
        onView(isRoot()).perform(waitForMatch(withId(R.id.empty_layout), EspressoUtils.UI_TEST_TIMEOUT));
        onView(isAssignableFrom(EditText.class)).perform(typeText("R"), pressKey(KeyEvent.KEYCODE_ENTER));

        tryIsDisplayed(withText("Runnest IHL"), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withText("Runnest IHL")).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), EspressoUtils.UI_TEST_TIMEOUT));
        onView(withText(R.string.challenge)).perform(click());

        //Choose a time challenge
        tryIsDisplayed(withId(R.id.define_challenge), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withId(R.id.time_radio)).perform(click());
        onView(withId(R.id.customize_positive_btn)).perform(click());

        //Start Challenge
        onView(isRoot()).perform(waitForMatch(withId(R.id.readyBtn), EspressoUtils.UI_TEST_TIMEOUT));
        onView(withId(R.id.readyBtn)).perform(click());

        tryIsDisplayed(withId(R.id.button_history), EspressoUtils.TIME_CHALLENGE_DURATION + EspressoUtils.UI_TEST_TIMEOUT);
    }

    @Test
    public void challengeKeepRunningQuit() {
        onView(withId(R.id.search)).perform(click());
        onView(isRoot()).perform(waitForMatch(withId(R.id.empty_layout), EspressoUtils.UI_TEST_TIMEOUT));
        onView(isAssignableFrom(EditText.class)).perform(typeText("R"), pressKey(KeyEvent.KEYCODE_ENTER));

        tryIsDisplayed(withText("Runnest IHL"), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withText("Runnest IHL")).perform(click());

        //Create challenge
        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), EspressoUtils.UI_TEST_TIMEOUT));
        onView(withText(R.string.challenge)).perform(click());
        tryIsDisplayed(withId(R.id.define_challenge), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withId(R.id.time_radio)).perform(click());
        onView(withId(R.id.customize_positive_btn)).perform(click());

        //Start Challenge
        onView(isRoot()).perform(waitForMatch(withId(R.id.readyBtn), EspressoUtils.UI_TEST_TIMEOUT));
        onView(withId(R.id.readyBtn)).perform(click());

        SystemClock.sleep(EspressoUtils.MOCK_LOCATION_DURATION);

        //Keep running
        onView(withId(R.id.back_to_side_btn)).perform(click());
        tryIsDisplayed(withId(android.R.id.button1), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withText(R.string.keep_running)).perform(click());

        //Quit challenge
        onView(withId(R.id.back_to_side_btn)).perform(click());
        tryIsDisplayed(withId(android.R.id.button1), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withText(R.string.quit)).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.button_history), EspressoUtils.UI_TEST_TIMEOUT));
    }

    @Test
    public void challengeWaitStopWait() {
        onView(withId(R.id.search)).perform(click());
        onView(isRoot()).perform(waitForMatch(withId(R.id.empty_layout), EspressoUtils.UI_TEST_TIMEOUT));
        onView(isAssignableFrom(EditText.class)).perform(typeText("R"), pressKey(KeyEvent.KEYCODE_ENTER));

        tryIsDisplayed(withText("Runnest IHL"), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withText("Runnest IHL")).perform(click());

        //Create challenge
        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), EspressoUtils.UI_TEST_TIMEOUT));
        onView(withText(R.string.challenge)).perform(click());
        tryIsDisplayed(withId(R.id.define_challenge), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withId(R.id.customize_positive_btn)).perform(click());

        //Wait
        onView(withId(R.id.back_to_side_btn)).perform(click());
        tryIsDisplayed(withId(android.R.id.button1), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withText(R.string.wait)).perform(click());

        //Quit
        onView(withId(R.id.back_to_side_btn)).perform(click());
        tryIsDisplayed(withId(android.R.id.button1), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withText(R.string.quit)).perform(click());
    }

    @Test
    public void challengeDistanceCancelThenCreate() {
        onView(withId(R.id.search)).perform(click());
        onView(isRoot()).perform(waitForMatch(withId(R.id.empty_layout), EspressoUtils.UI_TEST_TIMEOUT));
        onView(isAssignableFrom(EditText.class)).perform(typeText("R"), pressKey(KeyEvent.KEYCODE_ENTER));

        tryIsDisplayed(withText("Runnest IHL"), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withText("Runnest IHL")).perform(click());

        //Tap on cancel
        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), EspressoUtils.UI_TEST_TIMEOUT));
        onView(withText(R.string.challenge)).perform(click());

        tryIsDisplayed(withId(R.id.define_challenge), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withId(R.id.customize_negative_btn)).perform(click());

        //Tap challenge and create one
        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), EspressoUtils.UI_TEST_TIMEOUT));
        onView(withText(R.string.challenge)).perform(click());

        tryIsDisplayed(withId(R.id.define_challenge), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withId(R.id.customize_positive_btn)).perform(click());

        //Start Challenge
        onView(isRoot()).perform(waitForMatch(withId(R.id.readyBtn), EspressoUtils.UI_TEST_TIMEOUT));
        onView(withId(R.id.readyBtn)).perform(click());

        tryIsDisplayed(withId(R.id.button_history), EspressoUtils.MOCK_LOCATION_DURATION + EspressoUtils.UI_TEST_TIMEOUT);
    }

    @Test
    public void displayChallenge() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_history));

        onView(isRoot()).perform(waitForMatch(withId(R.id.tabs), EspressoUtils.UI_TEST_TIMEOUT));
        onView(allOf(withText("Challenge History"), isDescendantOfA(withId(R.id.tabs)))).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.list), EspressoUtils.UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
    }

    @Test
    public void scheduleRequestCancel() {
        onView(withId(R.id.search)).perform(click());
        onView(isRoot()).perform(waitForMatch(withId(R.id.empty_layout), EspressoUtils.UI_TEST_TIMEOUT));
        onView(isAssignableFrom(EditText.class)).perform(typeText("R"), pressKey(KeyEvent.KEYCODE_ENTER));

        tryIsDisplayed(withText("Runnest IHL"), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withText("Runnest IHL")).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), EspressoUtils.UI_TEST_TIMEOUT));
        onView(withText(R.string.schedule)).perform(click());

        tryIsDisplayed(withId(R.id.schedule_negative_btn), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withId(R.id.schedule_negative_btn)).perform(click());
    }

    @Test
    public void scheduleRequestDistance() {
        onView(withId(R.id.search)).perform(click());
        onView(isRoot()).perform(waitForMatch(withId(R.id.empty_layout), EspressoUtils.UI_TEST_TIMEOUT));
        onView(isAssignableFrom(EditText.class)).perform(typeText("R"), pressKey(KeyEvent.KEYCODE_ENTER));

        tryIsDisplayed(withText("Runnest IHL"), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withText("Runnest IHL")).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), EspressoUtils.UI_TEST_TIMEOUT));
        onView(withText(R.string.schedule)).perform(click());

        //Choose a distance challenge
        tryIsDisplayed(withId(R.id.distance_radio), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withId(R.id.distance_radio)).perform(click());
        onView(withId(R.id.schedule_positive_btn)).perform(click());
    }

    @Test
    public void scheduleRequestTime() {
        onView(withId(R.id.search)).perform(click());
        onView(isRoot()).perform(waitForMatch(withId(R.id.empty_layout), EspressoUtils.UI_TEST_TIMEOUT));
        onView(isAssignableFrom(EditText.class)).perform(typeText("R"), pressKey(KeyEvent.KEYCODE_ENTER));

        tryIsDisplayed(withText("Runnest IHL"), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withText("Runnest IHL")).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), EspressoUtils.UI_TEST_TIMEOUT));
        onView(withText(R.string.schedule)).perform(click());

        //Choose a time challenge
        tryIsDisplayed(withId(R.id.time_radio), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withId(R.id.time_radio)).perform(click());
        onView(withId(R.id.schedule_positive_btn)).perform(click());
    }
}
