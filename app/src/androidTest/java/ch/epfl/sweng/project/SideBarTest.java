package ch.epfl.sweng.project;

import android.os.SystemClock;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import ch.epfl.sweng.project.Activities.SideBarActivity;
import ch.epfl.sweng.project.Fragments.DisplayUserFragment;
import ch.epfl.sweng.project.Model.Run;
import ch.epfl.sweng.project.Model.TestUser;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SideBarTest {

    @Rule
    public ActivityTestRule<SideBarActivity> mActivityRule = new ActivityTestRule<>(
            SideBarActivity.class);

    @Before
    public void setUpApp() {
        ((AppRunnest) mActivityRule.getActivity().getApplication()).setUser(new TestUser());
        ((AppRunnest) mActivityRule.getActivity().getApplication()).setTestSession(true);
    }

    @Test
    public void aDrawerLayout() {

        SystemClock.sleep(500);

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(500);

        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
    }

    @Test
    public void navigateToRunningMap() {

        SystemClock.sleep(500);

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(500);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_new_run));
    }

    @Test
    public void navigateToRunHistory() {

        SystemClock.sleep(500);

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(500);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run_history));
    }

    @Test
    public void navigateToMessages() {

        SystemClock.sleep(500);

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(500);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));
    }

    @Test
    public void navigateToProfile() {

        SystemClock.sleep(500);

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(500);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_profile));
    }

    @Test
    public void navigateToLogout() {

        SystemClock.sleep(500);

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(500);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_logout));

        onView(withText("OK"))
                .perform(click());

        SystemClock.sleep(500);
    }


    @Test
    public void setRunningWorks() {

        SystemClock.sleep(500);

        SideBarActivity listenerTest = mActivityRule.getActivity();

        listenerTest.setRunning(true);
    }

    @Test
    public void nonEmptyOnFragmentListenerWork() {

        SystemClock.sleep(500);

        SideBarActivity listenerTest = mActivityRule.getActivity();

        Run listenerRun= new Run();
        listenerRun.start();
        SystemClock.sleep(500);
        listenerRun.update(TrackTest.buildCheckPoint(1, 1));
        listenerRun.update(TrackTest.buildCheckPoint(1, 2));
        listenerRun.stop();

        listenerTest.onRunHistoryInteraction(listenerRun);
        listenerTest.onRunningMapFragmentInteraction(listenerRun);
    }

    @Test
    public void uselessOnFragmentListenersWork() {

        SystemClock.sleep(500);

        SideBarActivity listenerTest = mActivityRule.getActivity();

        listenerTest.onMessagesFragmentInteraction();
        listenerTest.onDisplayUserFragmentInteraction();
        listenerTest.onProfileFragmentInteraction();
        listenerTest.onDBUploadFragmentInteraction();
    }

    @Test
    public void displayUserFragmentCanBeInstanced() {
        SystemClock.sleep(500);

        DisplayUserFragment.newInstance("testId", "testName");
    }

    @Test
    public void backButtonWorks() {

        SystemClock.sleep(500);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        SystemClock.sleep(500);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run_history));

        SystemClock.sleep(500);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        SystemClock.sleep(500);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_profile));

        SystemClock.sleep(500);

        Espresso.pressBack();

        SystemClock.sleep(500);

        onView(withId(R.id.list)).check(matches(isDisplayed()));
    }

    @Test
    public void backButtonDoesNothingIfStackEmpty() {

        SystemClock.sleep(500);

        Espresso.pressBack();
    }

    @Test
    public void startRun() {

        SystemClock.sleep(500);

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(500);

        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_new_run));

        SystemClock.sleep(500);

        onView(withId(R.id.start_run))
                .check(matches(isDisplayed()));

        SystemClock.sleep(500);

        onView(withId(R.id.start_run))
                .perform(click());

        SystemClock.sleep(500);

        onView(withId(R.id.stop_run))
                .check(matches(isDisplayed()));

        SystemClock.sleep(500);


        onView(withId(R.id.stop_run))
                .perform(click());

        SystemClock.sleep(500);

        onView(withId(R.id.go_to_run_history))
                .perform(click());

        SystemClock.sleep(500);

        onView(withId(R.id.list)).check(matches(isDisplayed()));
    }

    @Test
    public void lifecycleTest() {

        SystemClock.sleep(500);

        mActivityRule.getActivity().finish();
        mActivityRule.getActivity();
    }
}
