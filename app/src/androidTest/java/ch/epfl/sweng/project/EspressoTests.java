package ch.epfl.sweng.project;

import android.os.SystemClock;
import android.support.test.espresso.Espresso;
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
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.project.EspressoCustomActions.tryIsDisplayed;
import static ch.epfl.sweng.project.EspressoCustomActions.waitForMatch;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EspressoTests {

    private static final int UI_TEST_TIMEOUT = 5000;
    private static final int FIREBASE_DURATION = 2000;
    private static final int TIME_CHALLENGE_DURATION = 15000;

    //TODO: find a way to bound this to setupLocationChangeSimulation value
    private final int RUN_DURATION = 5000;

    @Rule
    public ActivityTestRule<SideBarActivity> mActivityRule = new ActivityTestRule<>(
            SideBarActivity.class);

    @Before
    public void setUpApp() {
        ((AppRunnest) mActivityRule.getActivity().getApplication()).setUser(new TestUser());
        ((AppRunnest) mActivityRule.getActivity().getApplication()).setTestSession(true);
        ((AppRunnest) mActivityRule.getActivity().getApplication()).setNetworkHandler();
        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), UI_TEST_TIMEOUT));
    }


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
    public void navigateToLogout() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_logout));

        tryIsDisplayed(withId(android.R.id.button2), UI_TEST_TIMEOUT);
        onView(withText("OK")).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.sign_in_button), UI_TEST_TIMEOUT));
    }

    @Test
    public void setRunningWorks() {
        SideBarActivity listenerTest = mActivityRule.getActivity();
        listenerTest.setRunning(true);
    }

    @Test
    public void runHistoryOnFragmentListenerWork() {
        SideBarActivity listenerTest = mActivityRule.getActivity();

        Run listenerRun= new Run();
        listenerRun.start();
        listenerRun.update(TrackTest.buildCheckPoint(1.0, 1.0));
        listenerRun.update(TrackTest.buildCheckPoint(1.0, 2.0));
        listenerRun.stop();

        listenerTest.onRunHistoryInteraction(listenerRun);
    }

    @Test
    public void searchNonExistentUser() {
        onView(withId(R.id.search)).perform(click());
        onView(isRoot()).perform(waitForMatch(withId(R.id.empty_layout), UI_TEST_TIMEOUT));
        onView(isRoot()).perform(waitForMatch(withId(R.id.empty_layout), UI_TEST_TIMEOUT));

        onView(isAssignableFrom(EditText.class)).perform(typeText("NonExistent"), pressKey(KeyEvent.KEYCODE_ENTER));
        tryIsDisplayed(withText("No user found."), UI_TEST_TIMEOUT);
        onView(withText("No user found.")).check(matches(isDisplayed()));
    }

    @Test
    public void uselessOnFragmentListenersWork() {
        SideBarActivity listenerTest = mActivityRule.getActivity();

        listenerTest.onProfileFragmentInteraction("test", "test");
    }

    @Test
    public void messageOnFragmentListenersWork() {
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
    public void displayProfileFragmentListenersWork() {
        SideBarActivity listenerTest = mActivityRule.getActivity();
        listenerTest.onDisplayProfileFragmentInteraction("testName", "test@email.ch");
    }

    @Test
    public void displayUserFragmentCanBeInstanced() {
        Map<String, String> map = new HashMap<>();
        map.put("testId", "testName");
        DisplayUserFragment.newInstance(map);
    }

    @Test
    public void backButtonWorks() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        //onView(isRoot()).perform(waitForMatch(withId(R.id.nav_history), UI_TEST_TIMEOUT));
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_history));
        onView(isRoot()).perform(waitForMatch(withId(R.id.tabs), UI_TEST_TIMEOUT));

        Espresso.pressBack();
        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), UI_TEST_TIMEOUT));
    }

    @Test
    public void backButtonDoesNothingIfStackEmpty() {
        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), UI_TEST_TIMEOUT));
        Espresso.pressBack();
        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), UI_TEST_TIMEOUT));
    }

    @Test
    public void startAndStopRun() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run));

        onView(isRoot()).perform(waitForMatch(withId(R.id.start_run), UI_TEST_TIMEOUT));
        onView(withId(R.id.start_run)).perform(click());

        //TODO: mocked location
        SystemClock.sleep(RUN_DURATION);

        onView(isRoot()).perform(waitForMatch(withId(R.id.stop_run), UI_TEST_TIMEOUT));
        onView(withId(R.id.stop_run)).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.button_history), UI_TEST_TIMEOUT));
        onView(withId(R.id.button_history)).perform(click());
    }

    @Test
    public void startAndAbortRun() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run));

        onView(isRoot()).perform(waitForMatch(withId(R.id.start_run), UI_TEST_TIMEOUT));
        onView(withId(R.id.start_run)).perform(click());

        //TODO: mocked location
        SystemClock.sleep(RUN_DURATION);

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
    public void lifecycleTest() {
        mActivityRule.getActivity().finish();
        mActivityRule.getActivity();
    }

    @Test
    public void deleteRun() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run));

        onView(isRoot()).perform(waitForMatch(withId(R.id.start_run), UI_TEST_TIMEOUT));
        onView(withId(R.id.start_run)).perform(click());

        //TODO: mocked location
        SystemClock.sleep(RUN_DURATION);

        onView(isRoot()).perform(waitForMatch(withId(R.id.stop_run), UI_TEST_TIMEOUT));
        onView(withId(R.id.stop_run)).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.button_delete), UI_TEST_TIMEOUT));
        onView(withId(R.id.button_delete)).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.tabs), UI_TEST_TIMEOUT));
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
        onView(withText("Cancel")).perform(click());

        //Tap challenge and create one
        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), UI_TEST_TIMEOUT));
        onView(withText(R.string.challenge)).perform(click());

        tryIsDisplayed(withId(R.id.define_challenge), UI_TEST_TIMEOUT);
        onView(withText("Challenge!")).perform(click());

        //Start Challenge
        onView(isRoot()).perform(waitForMatch(withId(R.id.readyBtn), UI_TEST_TIMEOUT));
        onView(withId(R.id.readyBtn)).perform(click());

        //TODO: mocked location
        tryIsDisplayed(withId(R.id.button_history), RUN_DURATION + UI_TEST_TIMEOUT);
    }

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
        onView(withId(R.id.btn_time)).perform(click());
        onView(withText("Challenge!")).perform(click());

        //Start Challenge
        onView(isRoot()).perform(waitForMatch(withId(R.id.readyBtn), UI_TEST_TIMEOUT));
        onView(withId(R.id.readyBtn)).perform(click());

        //TODO: time challenge
        tryIsDisplayed(withId(R.id.button_history), TIME_CHALLENGE_DURATION + UI_TEST_TIMEOUT);
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

        tryIsDisplayed(withId(android.R.id.button1), UI_TEST_TIMEOUT);
        onView(withText("Cancel")).perform(click());
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
        tryIsDisplayed(withId(R.id.button_distance), UI_TEST_TIMEOUT);
        onView(withId(R.id.button_distance)).perform(click());
        onView(withText("Schedule!")).perform(click());
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
        tryIsDisplayed(withId(R.id.button_time), UI_TEST_TIMEOUT);
        onView(withId(R.id.button_time)).perform(click());
        onView(withText("Schedule!")).perform(click());
    }

    @Test
    public void challengeRequestCancelAndDecline() {
        // Send message
        Message msg1 = new Message("runnest_dot_ihl_at_gmail_dot_com",
                "Test User",
                "Runnest IHL",
                "Test User",
                Message.MessageType.CHALLENGE_REQUEST,
                "test1",
                new Date(),
                1,
                0,
                Challenge.Type.DISTANCE);

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.send(msg1);
        // Instantiate challenges so that messages are clickable
        firebaseHelper.addChallengeNode("Test User","Runnest IHL" ,"Runnest IHL vs Test User test1");

        //TODO: send messages
        SystemClock.sleep(FIREBASE_DURATION);

        //Tap on the request
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));

        //Tap on Cancel
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());

        SystemClock.sleep(FIREBASE_DURATION);

        tryIsDisplayed(withId(R.id.request_layout), UI_TEST_TIMEOUT);
        onView(withText("Cancel")).perform(click());

        //Tap on Decline
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        tryIsDisplayed(withId(R.id.request_layout), UI_TEST_TIMEOUT);
        onView(withText("Decline")).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));

        //Delete challenges from firebase
        firebaseHelper.deleteChallengeNode("Runnest IHL vs Test User test1");
    }

    @Test
    public void challengeRequestAccept() {
        // Send message
        Message msg1 = new Message("runnest_dot_ihl_at_gmail_dot_com",
                "Test User",
                "Runnest IHL",
                "Test User",
                Message.MessageType.CHALLENGE_REQUEST,
                "test1",
                new Date(),
                1,
                0,
                Challenge.Type.DISTANCE);

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.send(msg1);
        // Instantiate challenges so that messages are clickable
        firebaseHelper.addChallengeNode("Test User","Runnest IHL" ,"Runnest IHL vs Test User test1");

        //TODO: send messages
        SystemClock.sleep(FIREBASE_DURATION);

        //Tap on the request
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());

        //Tap on Accept
        tryIsDisplayed(withId(R.id.request_layout), UI_TEST_TIMEOUT);
        onView(withText("Accept")).perform(click());
        onView(isRoot()).perform(waitForMatch(withId(R.id.readyBtn), UI_TEST_TIMEOUT));

        //Delete challenges from firebase
        firebaseHelper.deleteChallengeNode("Runnest IHL vs Test User test1");
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

        //TODO: send message
        SystemClock.sleep(FIREBASE_DURATION);

        //Tap on the request
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));

        //Tap on Cancel
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        tryIsDisplayed(withId(android.R.id.button1), UI_TEST_TIMEOUT);
        onView(withText("Cancel")).perform(click());

        //Tap on Accept
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        tryIsDisplayed(withId(android.R.id.button1), UI_TEST_TIMEOUT);
        onView(withText("Accept")).perform(click());

        //TODO: check there is a MEMO?!?
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

        //TODO: send message
        SystemClock.sleep(FIREBASE_DURATION);

        //Tap on the request
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));

        //Tap on Decline
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        tryIsDisplayed(withId(android.R.id.button1), UI_TEST_TIMEOUT);
        onView(withText("Decline")).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
    }

    @Test
    public void memoMessageCloseDelete() {
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

        //TODO: send message
        SystemClock.sleep(FIREBASE_DURATION);

        //Tap on the request
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());

        //Tap on Close
        tryIsDisplayed(withId(android.R.id.button1), UI_TEST_TIMEOUT);
        onView(withText("Close")).perform(click());

        //Tap on Delete
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        tryIsDisplayed(withId(android.R.id.button1), UI_TEST_TIMEOUT);
        onView(withText("Delete")).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
    }

    @Test
    public void memoMessageChallenge() {
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

        //TODO: send message
        SystemClock.sleep(FIREBASE_DURATION);

        //Tap on the request
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));

        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());

        //Tap on Challenge
        tryIsDisplayed(withId(android.R.id.button1), UI_TEST_TIMEOUT);
        onView(withText(R.string.challenge)).perform(click());

        tryIsDisplayed(withId(R.id.btn_time), UI_TEST_TIMEOUT);
    }

    @Test
    public void navigateToChallengeHistory() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_history));

        onView(isRoot()).perform(waitForMatch(withId(R.id.tabs), UI_TEST_TIMEOUT));
        onView(allOf(withText("Challenge History"), isDescendantOfA(withId(R.id.tabs)))).perform(click());
    }

    @Test
    public void navigateToSingleRunHistory() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_history));

        onView(isRoot()).perform(waitForMatch(withId(R.id.tabs), UI_TEST_TIMEOUT));
        onView(allOf(withText("Run History"), isDescendantOfA(withId(R.id.tabs)))).perform(click());
    }

    @Test
    public void displayChallenge() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_history));

        onView(isRoot()).perform(waitForMatch(withId(R.id.tabs), UI_TEST_TIMEOUT));
        onView(allOf(withText("Challenge History"), isDescendantOfA(withId(R.id.tabs)))).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
    }

    @Test
    public void challengeQuit() {
        onView(withId(R.id.search)).perform(click());
        onView(isRoot()).perform(waitForMatch(withId(R.id.empty_layout), UI_TEST_TIMEOUT));
        onView(isAssignableFrom(EditText.class)).perform(typeText("R"), pressKey(KeyEvent.KEYCODE_ENTER));

        tryIsDisplayed(withText("Runnest IHL"), UI_TEST_TIMEOUT);
        onView(withText("Runnest IHL")).perform(click());

        //Create challenge
        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), UI_TEST_TIMEOUT));
        onView(withText(R.string.challenge)).perform(click());
        tryIsDisplayed(withId(R.id.define_challenge), UI_TEST_TIMEOUT);
        onView(withId(R.id.btn_time)).perform(click());
        onView(withText("Challenge!")).perform(click());

        //Start Challenge
        onView(isRoot()).perform(waitForMatch(withId(R.id.readyBtn), UI_TEST_TIMEOUT));
        onView(withId(R.id.readyBtn)).perform(click());

        //TODO: mocked location
        SystemClock.sleep(RUN_DURATION);

        //Quit challenge
        onView(withId(R.id.back_to_side_btn)).perform(click());
        tryIsDisplayed(withId(android.R.id.button1), UI_TEST_TIMEOUT);
        onView(withText(R.string.quit)).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.button_history), UI_TEST_TIMEOUT));
    }

    @Test
    public void challengeStopWait() {
        onView(withId(R.id.search)).perform(click());
        onView(isRoot()).perform(waitForMatch(withId(R.id.empty_layout), UI_TEST_TIMEOUT));
        onView(isAssignableFrom(EditText.class)).perform(typeText("R"), pressKey(KeyEvent.KEYCODE_ENTER));

        tryIsDisplayed(withText("Runnest IHL"), UI_TEST_TIMEOUT);
        onView(withText("Runnest IHL")).perform(click());

        //Create challenge
        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), UI_TEST_TIMEOUT));
        onView(withText(R.string.challenge)).perform(click());
        tryIsDisplayed(withId(R.id.define_challenge), UI_TEST_TIMEOUT);
        onView(withText("Challenge!")).perform(click());

        //Quit challenge
        onView(withId(R.id.back_to_side_btn)).perform(click());
        tryIsDisplayed(withId(android.R.id.button1), UI_TEST_TIMEOUT);
        onView(withText(R.string.quit)).perform(click());
    }
}