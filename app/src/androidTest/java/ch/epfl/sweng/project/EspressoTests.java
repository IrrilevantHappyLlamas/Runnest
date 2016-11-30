package ch.epfl.sweng.project;

import android.os.SystemClock;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.sweng.project.Activities.ChallengeActivity;
import ch.epfl.sweng.project.Activities.SideBarActivity;
import ch.epfl.sweng.project.Firebase.FirebaseHelper;
import ch.epfl.sweng.project.Fragments.DisplayUserFragment;
import ch.epfl.sweng.project.Model.Challenge;
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
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EspressoTests {

    private final int WAIT_DURATION = 1000;
    //TODO: find a way to bound this to setupLocationChangeSimulation value
    public final static int RUN_DURATION = 11000;

    @Rule
    public ActivityTestRule<SideBarActivity> mActivityRule = new ActivityTestRule<>(
            SideBarActivity.class);

    @Before
    public void setUpApp() {
        ((AppRunnest) mActivityRule.getActivity().getApplication()).setUser(new TestUser());
        ((AppRunnest) mActivityRule.getActivity().getApplication()).setTestSession(true);
        ((AppRunnest) mActivityRule.getActivity().getApplication()).setNetworkHandler();
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
        listenerRun.update(TrackTest.buildCheckPoint(1.0, 1.0));
        listenerRun.update(TrackTest.buildCheckPoint(1.0, 2.0));
        listenerRun.stop();

        listenerTest.onRunHistoryInteraction(listenerRun);
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
        Message msg = new Message( "emailSender",
                "emailReceiver",
                "tester",
                "tested",
                Message.MessageType.CHALLENGE_REQUEST,
                "that's a test",
                new Date(),
                1,
                0,
                Challenge.Type.DISTANCE );
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
        SystemClock.sleep(RUN_DURATION);

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

    @Test
    public void challengeDistance() {
        SystemClock.sleep(WAIT_DURATION);
        onView(withId(R.id.search)).perform(click());
        SystemClock.sleep(WAIT_DURATION);
        onView(isAssignableFrom(EditText.class)).perform(typeText("Runnest"), pressKey(KeyEvent.KEYCODE_ENTER));
        SystemClock.sleep(WAIT_DURATION);

        //TODO: non riesco ad adattarlo al nuovo codice, chiedere a rikka.
        //onView(withId(R.id.table)).check(matches(isDisplayed()));
        //SystemClock.sleep(WAIT_DURATION);

        onView(withText(R.string.challenge)).perform(click());

        //Tap con cancel
        SystemClock.sleep(WAIT_DURATION);
        onView(withId(R.id.first_picker)).check(matches(isDisplayed()));
        onView(withText("Cancel")).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        //Tap on Challenge! and start a challenge of 1km
        onView(withText(R.string.challenge)).perform(click());
        SystemClock.sleep(WAIT_DURATION);
        onView(withText("Challenge!")).perform(click());

        onView(withId(R.id.readyBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.readyBtn)).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.challenge_chronometer)).check(matches(isDisplayed()));
        SystemClock.sleep(RUN_DURATION);
    }

    @Test
    public void challengeTime() {
        SystemClock.sleep(WAIT_DURATION);
        onView(withId(R.id.search)).perform(click());
        SystemClock.sleep(WAIT_DURATION);
        onView(isAssignableFrom(EditText.class)).perform(typeText("Runnest"), pressKey(KeyEvent.KEYCODE_ENTER));
        SystemClock.sleep(WAIT_DURATION);

        //TODO: non riesco ad adattarlo al nuovo codice, chiedere a rikka.
        // onView(withId(R.id.table)).check(matches(isDisplayed()));
        // SystemClock.sleep(WAIT_DURATION);

        onView(withText(R.string.challenge)).perform(click());

        //Choose a time challenge
        SystemClock.sleep(WAIT_DURATION);
        onView(withId(R.id.btn_time)).perform(click());
        SystemClock.sleep(WAIT_DURATION);
        onView(withText("Challenge!")).perform(click());

        onView(withId(R.id.readyBtn)).check(matches(isDisplayed()));
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.readyBtn)).perform(click());
        SystemClock.sleep(RUN_DURATION);

        onView(withId(R.id.challenge_chronometer)).check(matches(isDisplayed()));
    }

    @Test
    public void scheduleRequestCancel() {
        SystemClock.sleep(WAIT_DURATION);
        onView(withId(R.id.search)).perform(click());
        SystemClock.sleep(WAIT_DURATION);
        onView(isAssignableFrom(EditText.class)).perform(typeText("Runnest"), pressKey(KeyEvent.KEYCODE_ENTER));
        SystemClock.sleep(WAIT_DURATION);

        //TODO: non riesco ad adattarlo al nuovo codice, chiedere a rikka.
        // onView(withId(R.id.table)).check(matches(isDisplayed()));
        // SystemClock.sleep(WAIT_DURATION);

        onView(withText(R.string.schedule)).perform(click());

        SystemClock.sleep(WAIT_DURATION);
        SystemClock.sleep(WAIT_DURATION);
        onView(withText("Cancel")).perform(click());

        //nView(withId(R.id.readyBtn)).check(matches(isDisplayed()));
        //SystemClock.sleep(WAIT_DURATION);
    }

    @Test
    public void scheduleRequestDistance() {
        SystemClock.sleep(WAIT_DURATION);
        onView(withId(R.id.search)).perform(click());
        SystemClock.sleep(WAIT_DURATION);
        onView(isAssignableFrom(EditText.class)).perform(typeText("Runnest"), pressKey(KeyEvent.KEYCODE_ENTER));
        SystemClock.sleep(WAIT_DURATION);

        //TODO: non riesco ad adattarlo al nuovo codice, chiedere a rikka.
        // onView(withId(R.id.table)).check(matches(isDisplayed()));
        // SystemClock.sleep(WAIT_DURATION);

        onView(withText(R.string.schedule)).perform(click());

        //Choose a distance challenge
        SystemClock.sleep(WAIT_DURATION);
        onView(withId(R.id.button_distance)).perform(click());
        SystemClock.sleep(WAIT_DURATION);
        onView(withText("Schedule!")).perform(click());

        //nView(withId(R.id.readyBtn)).check(matches(isDisplayed()));
        //SystemClock.sleep(WAIT_DURATION);
    }

    @Test
    public void scheduleRequestTime() {
        SystemClock.sleep(WAIT_DURATION);
        //Tap on the challenge button per user
        onView(withId(R.id.search)).perform(click());
        SystemClock.sleep(WAIT_DURATION);
        onView(isAssignableFrom(EditText.class)).perform(typeText("Runnest"), pressKey(KeyEvent.KEYCODE_ENTER));
        SystemClock.sleep(WAIT_DURATION);

        //TODO: non riesco ad adattarlo al nuovo codice, chiedere a rikka.
        // onView(withId(R.id.table)).check(matches(isDisplayed()));
        // SystemClock.sleep(WAIT_DURATION);

        onView(withText(R.string.schedule)).perform(click());

        //Choose a time challenge
        SystemClock.sleep(WAIT_DURATION);
        onView(withId(R.id.button_time)).perform(click());
        SystemClock.sleep(WAIT_DURATION);
        onView(withText("Schedule!")).perform(click());

        //nView(withId(R.id.readyBtn)).check(matches(isDisplayed()));
        //SystemClock.sleep(WAIT_DURATION);
    }

    @Test
    public void challengeRequest() {

        // Send message
        Message msg1 = new Message("runnest_dot_ihl_at_gmail_dot_com",
                "Test User",
                "Runnest IHL",
                "Test User",
                Message.MessageType.CHALLENGE_REQUEST,
                "test 1",
                new Date(),
                1,
                0,
                Challenge.Type.DISTANCE);
        SystemClock.sleep(WAIT_DURATION);
        Message msg2 = new Message("runnest_dot_ihl_at_gmail_dot_com",
                "Test User",
                "Runnest IHL",
                "Test User",
                Message.MessageType.CHALLENGE_REQUEST,
                "test 2",
                new Date(),
                1,
                0,
                Challenge.Type.DISTANCE);
        SystemClock.sleep(WAIT_DURATION);
        Message msg3 = new Message("runnest_dot_ihl_at_gmail_dot_com",
                "Test User",
                "Runnest IHL",
                "Test User",
                Message.MessageType.CHALLENGE_REQUEST,
                "test 3",
                new Date(),
                1,
                0,
                Challenge.Type.DISTANCE);
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.send(msg1);
        firebaseHelper.send(msg2);
        firebaseHelper.send(msg3);
        SystemClock.sleep(WAIT_DURATION);

        //Tap on the request
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(WAIT_DURATION);
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));
        SystemClock.sleep(WAIT_DURATION);

        //Tap on Cancel
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        //onView(withText("From: Runnest IHL\nType: CHALLENGE_REQUEST")).perform(click());
        SystemClock.sleep(WAIT_DURATION);
        onView(withText("Cancel")).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        //Tap on Decline
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        SystemClock.sleep(WAIT_DURATION);
        onView(withText("Decline")).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        //Tap on Accept
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        SystemClock.sleep(WAIT_DURATION);
        onView(withText("Accept")).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.readyBtn)).check(matches(isDisplayed()));
    }

    @Test
    public void scheduleMessageCancelAndAccept() {

        // Send message
        Message msg1 = new Message("runnest_dot_ihl_at_gmail_dot_com",
                "Test User",
                "Runnest IHL",
                "Test User",
                Message.MessageType.SCHEDULE_REQUEST,
                "that's a test",
                new Date(),
                Challenge.Type.DISTANCE);

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.send(msg1);


        //Tap on the request
        SystemClock.sleep(WAIT_DURATION);
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(WAIT_DURATION);
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));
        SystemClock.sleep(WAIT_DURATION);

        //Tap on Cancel
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        SystemClock.sleep(WAIT_DURATION);
        onView(withText("Cancel")).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        //Tap on Accept
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        SystemClock.sleep(WAIT_DURATION);
        onView(withText("Accept")).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        //onView(withId(R.id.readyBtn)).check(matches(isDisplayed()));
    }

    @Test
    public void scheduleMessageDecline() {

        // Send message
        Message msg1 = new Message("runnest_dot_ihl_at_gmail_dot_com",
                "Test User",
                "Runnest IHL",
                "Test User",
                Message.MessageType.SCHEDULE_REQUEST,
                "that's a test",
                new Date(),
                Challenge.Type.DISTANCE);

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.send(msg1);


        //Tap on the request
        SystemClock.sleep(WAIT_DURATION);
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(WAIT_DURATION);
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));
        SystemClock.sleep(WAIT_DURATION);

        //Tap on Decline
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        SystemClock.sleep(WAIT_DURATION);
        onView(withText("Decline")).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        //onView(withId(R.id.readyBtn)).check(matches(isDisplayed()));
    }

    @Test
    public void memoMessage() {

        // Send message
        Message msg1 = new Message("runnest_dot_ihl_at_gmail_dot_com",
                "Test User",
                "Runnest IHL",
                "Test User",
                Message.MessageType.MEMO,
                "that's a test",
                new Date(),
                Challenge.Type.DISTANCE);

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.send(msg1);

        //Tap on the request
        SystemClock.sleep(WAIT_DURATION);
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(WAIT_DURATION);
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));
        SystemClock.sleep(WAIT_DURATION);

        //Tap on Close
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        SystemClock.sleep(WAIT_DURATION);
        onView(withText("Close")).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        //Tap on Delete
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        SystemClock.sleep(WAIT_DURATION);
        onView(withText("Delete")).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        //onView(withId(R.id.readyBtn)).check(matches(isDisplayed()));
    }

    public void navigateToChallengeHistory() {

        SystemClock.sleep(WAIT_DURATION);
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        SystemClock.sleep(WAIT_DURATION);
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run_history));

        SystemClock.sleep(WAIT_DURATION);
        onView(withId(R.id.spinner)).perform(click());

        SystemClock.sleep(WAIT_DURATION);
        onData(allOf(is(instanceOf(String.class)), is("Challenges"))).perform(click());
    }

    @Test
    public void navigateToSingleRunHistoryWithSpinner() {

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run_history));

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.spinner)).perform(click());

        onData(allOf(is(instanceOf(String.class)), is("Challenges"))).perform(click());

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.spinner)).perform(click());

        onData(allOf(is(instanceOf(String.class)), is("Single Runs"))).perform(click());
    }

    @Test
    public void displayChallenge() {

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run_history));
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.spinner)).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        onData(allOf(is(instanceOf(String.class)), is("Challenges"))).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        SystemClock.sleep(WAIT_DURATION);
    }

    @Test
    public void challengeQuit() {
        SystemClock.sleep(WAIT_DURATION);
        onView(withId(R.id.search)).perform(click());
        SystemClock.sleep(WAIT_DURATION);
        onView(isAssignableFrom(EditText.class)).perform(typeText("Runnest"), pressKey(KeyEvent.KEYCODE_ENTER));
        SystemClock.sleep(WAIT_DURATION);
        onView(withText(R.string.challenge)).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        //Tap on Challenge! and start a challenge of 1km
        onView(withId(R.id.btn_time)).perform(click());
        SystemClock.sleep(WAIT_DURATION);
        onView(withText("Challenge!")).perform(click());

        onView(withId(R.id.readyBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.readyBtn)).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.challenge_chronometer)).check(matches(isDisplayed()));
        SystemClock.sleep(RUN_DURATION);

        onView(withId(R.id.back_to_side_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.back_to_side_btn)).perform(click());

        SystemClock.sleep(WAIT_DURATION);

        onView(withText(R.string.quit)).perform(click());
        SystemClock.sleep(WAIT_DURATION);
    }

    @Test
    public void challengeStopWait() {
        SystemClock.sleep(WAIT_DURATION);
        onView(withId(R.id.search)).perform(click());
        SystemClock.sleep(WAIT_DURATION);
        onView(isAssignableFrom(EditText.class)).perform(typeText("Runnest"), pressKey(KeyEvent.KEYCODE_ENTER));
        SystemClock.sleep(WAIT_DURATION);
        onView(withText(R.string.challenge)).perform(click());
        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.btn_time)).perform(click());
        SystemClock.sleep(WAIT_DURATION);
        onView(withText("Challenge!")).perform(click());

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.back_to_side_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.back_to_side_btn)).perform(click());

        SystemClock.sleep(WAIT_DURATION);

        onView(withText(R.string.quit)).perform(click());
    }
}