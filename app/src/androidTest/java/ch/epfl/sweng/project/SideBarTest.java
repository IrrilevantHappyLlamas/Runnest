package ch.epfl.sweng.project;

import android.os.SystemClock;
import android.support.test.espresso.action.EspressoKey;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.sweng.project.Activities.SideBarActivity;
import ch.epfl.sweng.project.Fragments.DisplayUserFragment;
import ch.epfl.sweng.project.Model.Message;
import ch.epfl.sweng.project.Model.Run;
import ch.epfl.sweng.project.Model.TestUser;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
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

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(500);

        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
    }

    @Test
    public void navigateToRunningMap() {

        SystemClock.sleep(500);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(500);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run));
    }

    @Test
    public void navigateToRunHistory() {

        SystemClock.sleep(500);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(500);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run_history));
    }

    @Test
    public void navigateToMessages() {

        SystemClock.sleep(500);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(500);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));
    }

    @Test
    public void navigateToProfile() {

        SystemClock.sleep(500);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(500);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_profile));
    }

    @Test
    public void navigateToLogout() {

        SystemClock.sleep(500);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(500);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_logout));
        SystemClock.sleep(500);


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
    public void searchAndClickOnChallenge() {
        onView(withId(R.id.search)).perform(click());
        SystemClock.sleep(500);

        onView(isAssignableFrom(EditText.class)).perform(typeText("pablo"), pressKey(KeyEvent.KEYCODE_ENTER));
        SystemClock.sleep(500);

        onView(withId(R.id.table)).check(matches(isDisplayed()));
        SystemClock.sleep(500);

        onView(isAssignableFrom(Button.class)).perform(click());
        SystemClock.sleep(500);

        onView(withId(R.id.waitChallengedUserTextView)).check(matches(isDisplayed()));
    }

    @Test
    public void searchInexistentUser() {
        onView(withId(R.id.search)).perform(click());
        SystemClock.sleep(500);

        onView(isAssignableFrom(EditText.class)).perform(typeText("kadfjisadsa"), pressKey(KeyEvent.KEYCODE_ENTER));
        SystemClock.sleep(500);

        onView(withId(R.id.table)).check(matches(isDisplayed()));
        onView(withText("No user found.")).check(matches(isDisplayed()));
    }

    @Test
    public void acceptChallengeRequest() {

        SystemClock.sleep(500);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(500);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));
        SystemClock.sleep(500);

        onView(withText("From: Pablo\nType: CHALLENGE_REQUEST")).perform(click());
        SystemClock.sleep(500);

        onView(withText("ACCEPT")).perform(click());
        SystemClock.sleep(500);
    }

    @Test
    public void declineChallengeRequest() {

        SystemClock.sleep(500);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(500);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));
        SystemClock.sleep(500);

        onView(withText("From: Pablo\nType: CHALLENGE_REQUEST")).perform(click());
        SystemClock.sleep(500);

        onView(withText("DECLINE")).perform(click());
        SystemClock.sleep(500);
    }

    @Test
    public void uselessOnFragmentListenersWork() {

        SystemClock.sleep(500);

        SideBarActivity listenerTest = mActivityRule.getActivity();

        listenerTest.onProfileFragmentInteraction();
        listenerTest.onDBUploadFragmentInteraction();
    }

    @Test
    public void messageOnFragmentListenersWork() {
        SystemClock.sleep(500);

        SideBarActivity listenerTest = mActivityRule.getActivity();
        Message msg = new Message("to", "from", Message.MessageType.CHALLENGE_REQUEST, "test");
        listenerTest.onMessagesFragmentInteraction(msg);
    }



    @Test
    public void displayUserOnFragmentListenersWork() {
        SystemClock.sleep(500);

        SideBarActivity listenerTest = mActivityRule.getActivity();
        listenerTest.onDisplayUserFragmentInteraction("testName", "test@email.ch");
    }

    @Test
    public void displayUserFragmentCanBeInstanced() {
        SystemClock.sleep(500);

        Map<String, String> map = new HashMap<>();
        map.put("testId", "testName");
        DisplayUserFragment.newInstance(map);
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

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(500);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run));
        SystemClock.sleep(500);

        onView(withId(R.id.start_run)).check(matches(isDisplayed()));
        onView(withId(R.id.start_run)).perform(click());
        SystemClock.sleep(500);

        onView(withId(R.id.stop_run)).check(matches(isDisplayed()));
        onView(withId(R.id.stop_run)).perform(click());
        SystemClock.sleep(500);

        onView(withId(R.id.go_to_run_history)).perform(click());
        SystemClock.sleep(500);

        onView(withId(R.id.list)).check(matches(isDisplayed()));
    }

    @Test
    public void stopRun() {

        SystemClock.sleep(500);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(500);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run));
        SystemClock.sleep(500);

        onView(withId(R.id.start_run)).perform(click());
        SystemClock.sleep(500);

        pressBack();
        SystemClock.sleep(500);

        //Press on CANCEL
        onView(withId(android.R.id.button2)).perform(click());

        SystemClock.sleep(500);

        onView(withId(R.id.stop_run)).check(matches(isDisplayed()));
        SystemClock.sleep(500);

        pressBack();
        SystemClock.sleep(500);

        //Press on OK
        onView(withId(android.R.id.button1)).perform(click());

        SystemClock.sleep(500);

        onView(withId(R.id.photoImg)).check(matches(isDisplayed()));
    }

    @Test
    public void lifecycleTest() {

        SystemClock.sleep(500);

        mActivityRule.getActivity().finish();
        mActivityRule.getActivity();
    }

}
