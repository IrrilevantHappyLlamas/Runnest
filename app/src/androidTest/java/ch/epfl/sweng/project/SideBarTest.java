package ch.epfl.sweng.project;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
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
import ch.epfl.sweng.project.Model.Run;

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
        ((AppRunnest) mActivityRule.getActivity().getApplication()).setGoogleUser(null);
    }

    @Test
    public void backButtonDoesNothingIfStackEmpty() {
        SystemClock.sleep(1000);

        Espresso.pressBack();
    }

    @Test
    public void aDrawerLayout() {
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(1000);

        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
    }

    @Test
    public void navigateToRunningMap() {
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(1000);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_new_run));
    }

    @Test
    public void navigateToRunHistory() {
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(1000);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run_history));
    }

    @Test
    public void navigateToMessages() {
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(1000);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));
    }

    @Test
    public void navigateToProfile() {
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(1000);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_profile));
    }

    @Test
    public void navigateToLogout() {
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(1000);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_logout));

        onView(withText("OK"))
                .perform(click());

        SystemClock.sleep(1000);
    }

    @Test
    public void startRun(){
        SystemClock.sleep(1000);

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(1000);

        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_new_run));

        onView(withId(R.id.start_run))
                .check(matches(isDisplayed()));

        onView(withId(R.id.start_run))
                .perform(click());

        SystemClock.sleep(1000);

        onView(withId(R.id.stop_run))
                .check(matches(isDisplayed()));

        onView(withId(R.id.stop_run))
                .perform(click());

        SystemClock.sleep(1000);

        onView(withId(R.id.go_to_run_history))
                .perform(click());

        SystemClock.sleep(1000);

        onView(withId(R.id.list)).check(matches(isDisplayed()));

    }

    @Test
    public void backButtonWorks() {

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(1000);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run_history));

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(1000);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_new_run));

        SystemClock.sleep(1000);

        Espresso.pressBack();

        SystemClock.sleep(1000);

        onView(withId(R.id.list)).check(matches(isDisplayed()));
    }


    @Test
    public void uselessOnFragmentListenersWork() {

        SideBarActivity listenerTest = mActivityRule.getActivity();

        listenerTest.onMessagesFragmentInteraction();
        listenerTest.onDisplayUserFragmentInteraction();
        listenerTest.onProfileFragmentInteraction();
        listenerTest.onDBUploadFragmentInteraction();
    }

    @Test
    public void emptyOnFragmentListenersWork() {

        //TODO: capire esattamente a cosa serve
        Looper.prepare();

        SideBarActivity listenerTest = mActivityRule.getActivity();

        listenerTest.onDisplayRunInteraction();
        listenerTest.onDBDownloadFragmentInteraction();
    }

    @Test
    public void nonEmptyOnFragmentListenerWork() {

        SideBarActivity listenerTest = mActivityRule.getActivity();

        Run listenerRun= new Run();
        listenerRun.start();
        SystemClock.sleep(1000);
        listenerRun.update(TrackTest.buildCheckPoint(1, 1));
        listenerRun.update(TrackTest.buildCheckPoint(1, 2));
        listenerRun.stop();

        listenerTest.onRunHistoryInteraction(listenerRun);
        listenerTest.onRunningMapFragmentInteraction(listenerRun);
    }

    @Test
    public void setRunningWorks() {

        SideBarActivity listenerTest = mActivityRule.getActivity();

        listenerTest.setRunning(true);
    }

}
