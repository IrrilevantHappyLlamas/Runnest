package ch.epfl.sweng.project.espresso;

import android.os.SystemClock;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

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
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
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
public class RunTest {

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
    public void startAndStopRun() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run));

        //TODO
        SystemClock.sleep(EspressoUtils.FIREBASE_DURATION);

        onView(isRoot()).perform(waitForMatch(withId(R.id.start_run), EspressoUtils.UI_TEST_TIMEOUT));
        onView(withId(R.id.start_run)).perform(click());

        SystemClock.sleep(EspressoUtils.MOCK_LOCATION_DURATION);

        onView(isRoot()).perform(waitForMatch(withId(R.id.stop_run), EspressoUtils.UI_TEST_TIMEOUT));
        onView(withId(R.id.stop_run)).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.button_history), EspressoUtils.UI_TEST_TIMEOUT));
        onView(withId(R.id.button_history)).perform(click());
    }

    @Test
    public void startAndAbortRun() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run));

        //TODO
        SystemClock.sleep(EspressoUtils.FIREBASE_DURATION);

        onView(isRoot()).perform(waitForMatch(withId(R.id.start_run), EspressoUtils.UI_TEST_TIMEOUT));
        onView(withId(R.id.start_run)).perform(click());

        SystemClock.sleep(EspressoUtils.MOCK_LOCATION_DURATION);

        //Press on CANCEL
        pressBack();
        tryIsDisplayed(withId(android.R.id.button2), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withId(android.R.id.button2)).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.stop_run), EspressoUtils.UI_TEST_TIMEOUT));

        pressBack();
        tryIsDisplayed(withId(android.R.id.button1), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withId(android.R.id.button1)).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), EspressoUtils.UI_TEST_TIMEOUT));
    }

    @Test
    public void deleteRun() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run));

        //TODO
        SystemClock.sleep(EspressoUtils.FIREBASE_DURATION);

        onView(isRoot()).perform(waitForMatch(withId(R.id.start_run), EspressoUtils.UI_TEST_TIMEOUT));
        onView(withId(R.id.start_run)).perform(click());

        SystemClock.sleep(EspressoUtils.MOCK_LOCATION_DURATION);

        onView(isRoot()).perform(waitForMatch(withId(R.id.stop_run), EspressoUtils.UI_TEST_TIMEOUT));
        onView(withId(R.id.stop_run)).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.button_delete), EspressoUtils.UI_TEST_TIMEOUT));
        onView(withId(R.id.button_delete)).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.tabs), EspressoUtils.UI_TEST_TIMEOUT));
    }

    @Test
    public void displayRun() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_history));

        onView(isRoot()).perform(waitForMatch(withId(R.id.tabs), EspressoUtils.UI_TEST_TIMEOUT));
        onView(allOf(withText("Run History"), isDescendantOfA(withId(R.id.tabs)))).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.list), EspressoUtils.UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
    }
}
