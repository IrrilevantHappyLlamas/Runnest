package ch.ihl.runnest.espresso;

import android.os.SystemClock;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.multidex.ch.ihl.runnest.AppRunnest.R;

import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Date;

import ch.ihl.runnest.Firebase.FirebaseHelper;
import ch.ihl.runnest.Model.Challenge;
import ch.ihl.runnest.Model.Message;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessageTest extends EspressoTest {


    @After
    public void deleteTestUserMessages() {
        new FirebaseHelper().deleteUserMessages("Test User");
    }

    private void sendTestMessage(Message.Type messageType, Challenge.Type challengeType, boolean createChallenge) {
        // Send message
        Message msg1 = new Message("runnest_dot_ihl_at_gmail_dot_com",
                "Test User",
                "Runnest IHL",
                "Test User",
                messageType,
                "test1",
                new Date(),
                1,
                0,
                challengeType);

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.sendMessage(msg1);

        if(createChallenge) {
            // Instantiate challenges so that messages are clickable
            firebaseHelper.addChallengeNode("Test User", "Runnest IHL", "Runnest IHL vs Test User test1");
        }

        // Needed to be sure that the sent message appears on firebase
        SystemClock.sleep(FIREBASE_DURATION);
    }

    @Test
    public void challengeRequestCancelAndDecline() {
        // Send message
        sendTestMessage(Message.Type.CHALLENGE_REQUEST, Challenge.Type.TIME, true);

        //Tap on the request
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));

        //Tap on Cancel
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());

        SystemClock.sleep(FIREBASE_DURATION);

        tryIsDisplayed(withId(R.id.request_layout), UI_TEST_TIMEOUT);
        onView(withId(R.id.cancel_btn)).perform(click());

        //Tap on Decline
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        tryIsDisplayed(withId(R.id.request_layout), UI_TEST_TIMEOUT);
        onView(withId(R.id.decline_btn)).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));

        //Delete challenges from firebase
        new FirebaseHelper().deleteChallengeNode("Runnest IHL vs Test User test1");
    }

    @Test
    public void challengeRequestAccept() {
        // Send message
        sendTestMessage(Message.Type.CHALLENGE_REQUEST, Challenge.Type.DISTANCE, true);

        //Tap on the request
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());

        //Tap on Accept
        tryIsDisplayed(withId(R.id.request_layout), UI_TEST_TIMEOUT);
        onView(withId(R.id.accept_btn)).perform(click());
        onView(isRoot()).perform(waitForMatch(withId(R.id.readyBtn), UI_TEST_TIMEOUT));

        //Delete challenges from firebase
        new FirebaseHelper().deleteChallengeNode("Runnest IHL vs Test User test1");
    }

    @Test
    public void scheduleMessageCancelAndAccept() {
        // Send message
        sendTestMessage(Message.Type.SCHEDULE_REQUEST, Challenge.Type.DISTANCE, false);

        //Tap on the request
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));

        //Tap on Cancel
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        tryIsDisplayed(withId(R.id.cancel_btn), UI_TEST_TIMEOUT);
        onView(withId(R.id.cancel_btn)).perform(click());

        //Tap on Accept
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        tryIsDisplayed(withId(R.id.accept_btn), UI_TEST_TIMEOUT);
        onView(withId(R.id.accept_btn)).perform(click());
    }

    @Test
    public void scheduleMessageDecline() {
        // Send message
        sendTestMessage(Message.Type.SCHEDULE_REQUEST, Challenge.Type.TIME, false);

        //Tap on the request
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));

        //Tap on Decline
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        tryIsDisplayed(withId(R.id.decline_btn), UI_TEST_TIMEOUT);
        onView(withId(R.id.decline_btn)).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
    }

    @Test
    public void memoMessageCloseDelete() {
        // Send message
        sendTestMessage(Message.Type.MEMO, Challenge.Type.TIME, false);

        //Tap on the request
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());

        //Tap on Close
        tryIsDisplayed(withId(R.id.cancel_btn), UI_TEST_TIMEOUT);
        onView(withId(R.id.cancel_btn)).perform(click());

        //Tap on Delete
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        tryIsDisplayed(withId(R.id.decline_btn), UI_TEST_TIMEOUT);
        onView(withId(R.id.decline_btn)).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
    }

    @Test
    public void memoMessageChallenge() {
        // Send message
        sendTestMessage(Message.Type.MEMO, Challenge.Type.DISTANCE, false);

        //Tap on the request
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));

        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());

        //Tap on Challenge
        tryIsDisplayed(withId(R.id.accept_btn), UI_TEST_TIMEOUT);
        onView(withId(R.id.accept_btn)).perform(click());

        tryIsDisplayed(withId(R.id.time_radio), UI_TEST_TIMEOUT);
    }

    @Test
    public void memoMessageBusyChallenge() {
        // Send message
        Message msg1 = new Message("busyEmail",
                "Test User",
                "busyUser",
                "Test User",
                Message.Type.MEMO,
                "test1",
                new Date(),
                1,
                0,
                Challenge.Type.DISTANCE);

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.sendMessage(msg1);

        // Needed to be sure that the sent message appears on firebase
        SystemClock.sleep(FIREBASE_DURATION);

        //Tap on the request
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));

        onView(isRoot()).perform(waitForMatch(withId(R.id.list), UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());

        //Tap on Challenge
        tryIsDisplayed(withId(R.id.accept_btn), UI_TEST_TIMEOUT);
        onView(withId(R.id.accept_btn)).perform(click());
    }

}