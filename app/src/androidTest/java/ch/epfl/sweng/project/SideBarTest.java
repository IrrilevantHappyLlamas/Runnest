package ch.epfl.sweng.project;

import android.app.LauncherActivity;
import android.os.SystemClock;
import android.support.test.espresso.action.EspressoKey;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.AppCompatTextView;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ListView;
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

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SideBarTest {

    private final int WAIT_DURATION = 2000;

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

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
    }

    @Test
    public void navigateToRunningMap() {

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run));
    }

    @Test
    public void navigateToRunHistory() {

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run_history));
    }

    @Test
    public void navigateToMessages() {

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));
    }

    @Test
    public void navigateToProfile() {

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_profile));
    }

    @Test
    public void navigateToLogout() {

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_logout));
        SystemClock.sleep(WAIT_DURATION);

        onView(withText("OK"))
                .perform(click());

        SystemClock.sleep(WAIT_DURATION);
    }


    @Test
    public void setRunningWorks() {

        SystemClock.sleep(WAIT_DURATION);

        SideBarActivity listenerTest = mActivityRule.getActivity();

        listenerTest.setRunning(true);
    }

    @Test
    public void runHistoryOnFragmentListenerWork() {

        SystemClock.sleep(WAIT_DURATION);

        SideBarActivity listenerTest = mActivityRule.getActivity();

        Run listenerRun= new Run();
        listenerRun.start();
        SystemClock.sleep(WAIT_DURATION);
        listenerRun.update(TrackTest.buildCheckPoint(1, 1));
        listenerRun.update(TrackTest.buildCheckPoint(1, 2));
        listenerRun.stop();

        listenerTest.onRunHistoryInteraction(listenerRun);
    }

    @Test
    public void searchAndClickOnChallenge() {
        onView(withId(R.id.search)).perform(click());

        SystemClock.sleep(WAIT_DURATION);

        onView(isAssignableFrom(EditText.class)).perform(typeText("pablo"), pressKey(KeyEvent.KEYCODE_ENTER));
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.table)).check(matches(isDisplayed()));
        SystemClock.sleep(WAIT_DURATION);

        onView(isAssignableFrom(Button.class)).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.waitChallengedUserTextView)).check(matches(isDisplayed()));
    }

    @Test
    public void searchInexistentUser() {
        onView(withId(R.id.search)).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        onView(isAssignableFrom(EditText.class)).perform(typeText("kadfjisadsa"), pressKey(KeyEvent.KEYCODE_ENTER));
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.table)).check(matches(isDisplayed()));
        onView(withText("No user found.")).check(matches(isDisplayed()));
    }

    @Test
    public void cantSearchMyself() {
        onView(withId(R.id.search)).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        onView(isAssignableFrom(EditText.class)).perform(typeText("test user"), pressKey(KeyEvent.KEYCODE_ENTER));
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.table)).check(matches(isDisplayed()));
        onView(withText("No user found.")).check(matches(isDisplayed()));
    }

    @Test
    public void acceptChallengeRequest() {

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));
        SystemClock.sleep(WAIT_DURATION);

        onView(withText("From: Pablo\nType: CHALLENGE_REQUEST")).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        onView(withText("ACCEPT")).perform(click());
        SystemClock.sleep(WAIT_DURATION);
    }

    @Test
    public void declineChallengeRequest() {

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));
        SystemClock.sleep(WAIT_DURATION);

        onView(withText("From: Pablo\nType: CHALLENGE_REQUEST")).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        onView(withText("DECLINE")).perform(click());
        SystemClock.sleep(WAIT_DURATION);
    }

    @Test
    public void uselessOnFragmentListenersWork() {

        SystemClock.sleep(WAIT_DURATION);

        SideBarActivity listenerTest = mActivityRule.getActivity();

        listenerTest.onProfileFragmentInteraction();
        listenerTest.onDBUploadFragmentInteraction();
    }

    @Test
    public void messageOnFragmentListenersWork() {
        SystemClock.sleep(WAIT_DURATION);

        SideBarActivity listenerTest = mActivityRule.getActivity();
        Message msg = new Message("to", "from", "to", "from", Message.MessageType.CHALLENGE_REQUEST, "test");
        listenerTest.onMessagesFragmentInteraction(msg);
    }



    @Test
    public void displayUserOnFragmentListenersWork() {
        SystemClock.sleep(WAIT_DURATION);

        SideBarActivity listenerTest = mActivityRule.getActivity();
        listenerTest.onDisplayUserFragmentInteraction("testName", "test@email.ch");
    }

    @Test
    public void displayUserFragmentCanBeInstanced() {
        SystemClock.sleep(WAIT_DURATION);

        Map<String, String> map = new HashMap<>();
        map.put("testId", "testName");
        DisplayUserFragment.newInstance(map);
    }

    @Test
    public void backButtonWorks() {

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run_history));
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_profile));
        SystemClock.sleep(WAIT_DURATION);

        Espresso.pressBack();
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.list)).check(matches(isDisplayed()));
    }

    @Test
    public void backButtonDoesNothingIfStackEmpty() {

        SystemClock.sleep(WAIT_DURATION);

        Espresso.pressBack();
    }

    @Test
    public void startRun() {

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run));
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.start_run)).check(matches(isDisplayed()));
        onView(withId(R.id.start_run)).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.stop_run)).check(matches(isDisplayed()));
        onView(withId(R.id.stop_run)).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.go_to_run_history)).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.list)).check(matches(isDisplayed()));
    }

    @Test
    public void stopRun() {

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run));
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.start_run)).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        pressBack();
        SystemClock.sleep(WAIT_DURATION);

        //Press on CANCEL
        onView(withId(android.R.id.button2)).perform(click());

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.stop_run)).check(matches(isDisplayed()));
        SystemClock.sleep(WAIT_DURATION);

        pressBack();
        SystemClock.sleep(WAIT_DURATION);

        //Press on OK
        onView(withId(android.R.id.button1)).perform(click());

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.photoImg)).check(matches(isDisplayed()));
    }

    @Test
    public void lifecycleTest() {

        SystemClock.sleep(WAIT_DURATION);

        mActivityRule.getActivity().finish();
        mActivityRule.getActivity();
    }

    @Test
    public void deleteRun() {
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run));
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.start_run)).check(matches(isDisplayed()));
        onView(withId(R.id.start_run)).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.stop_run)).check(matches(isDisplayed()));
        onView(withId(R.id.stop_run)).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.deleteRunButton)).perform(click());

    }
}
