package ch.epfl.sweng.project.espresso;

import android.os.SystemClock;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.model.Statement;

import java.util.Date;

import ch.epfl.sweng.project.Activities.SideBarActivity;
import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Firebase.FirebaseHelper;
import ch.epfl.sweng.project.Model.Challenge;
import ch.epfl.sweng.project.Model.Message;
import ch.epfl.sweng.project.Model.TestUser;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.sweng.project.espresso.EspressoUtils.tryIsDisplayed;
import static ch.epfl.sweng.project.espresso.EspressoUtils.waitForMatch;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessageTest {


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

    @After
    public void deleteTestUserMessages() {
        new FirebaseHelper().deleteUserMessages("Test User");
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
        firebaseHelper.sendMessage(msg1);
        // Instantiate challenges so that messages are clickable
        firebaseHelper.addChallengeNode("Test User","Runnest IHL" ,"Runnest IHL vs Test User test1");

        //TODO: send messages
        SystemClock.sleep(EspressoUtils.FIREBASE_DURATION);

        //Tap on the request
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));

        //Tap on Cancel
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), EspressoUtils.UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());

        SystemClock.sleep(EspressoUtils.FIREBASE_DURATION);

        tryIsDisplayed(withId(R.id.request_layout), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withId(R.id.cancel_btn)).perform(click());

        //Tap on Decline
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), EspressoUtils.UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        tryIsDisplayed(withId(R.id.request_layout), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withId(R.id.decline_btn)).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.list), EspressoUtils.UI_TEST_TIMEOUT));

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
        firebaseHelper.sendMessage(msg1);
        // Instantiate challenges so that messages are clickable
        firebaseHelper.addChallengeNode("Test User","Runnest IHL" ,"Runnest IHL vs Test User test1");

        //TODO: send messages
        SystemClock.sleep(EspressoUtils.FIREBASE_DURATION);

        //Tap on the request
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), EspressoUtils.UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());

        //Tap on Accept
        tryIsDisplayed(withId(R.id.request_layout), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withId(R.id.accept_btn)).perform(click());
        onView(isRoot()).perform(waitForMatch(withId(R.id.readyBtn), EspressoUtils.UI_TEST_TIMEOUT));

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
        firebaseHelper.sendMessage(msg1);

        //TODO: send message
        SystemClock.sleep(EspressoUtils.FIREBASE_DURATION);

        //Tap on the request
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));

        //Tap on Cancel
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), EspressoUtils.UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        tryIsDisplayed(withId(R.id.cancel_btn), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withId(R.id.cancel_btn)).perform(click());

        //Tap on Accept
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), EspressoUtils.UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        tryIsDisplayed(withId(R.id.accept_btn), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withId(R.id.accept_btn)).perform(click());

        //TODO: (for Hakim) how check if there is a MEMO
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
        firebaseHelper.sendMessage(msg1);

        //TODO: send message
        SystemClock.sleep(EspressoUtils.FIREBASE_DURATION);

        //Tap on the request
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));

        //Tap on Decline
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), EspressoUtils.UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        tryIsDisplayed(withId(R.id.decline_btn), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withId(R.id.decline_btn)).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.list), EspressoUtils.UI_TEST_TIMEOUT));
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
        firebaseHelper.sendMessage(msg1);

        //TODO: send message
        SystemClock.sleep(EspressoUtils.FIREBASE_DURATION);

        //Tap on the request
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), EspressoUtils.UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());

        //Tap on Close
        tryIsDisplayed(withId(R.id.cancel_btn), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withId(R.id.cancel_btn)).perform(click());

        //Tap on Delete
        onView(isRoot()).perform(waitForMatch(withId(R.id.list), EspressoUtils.UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());
        tryIsDisplayed(withId(R.id.decline_btn), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withId(R.id.decline_btn)).perform(click());

        onView(isRoot()).perform(waitForMatch(withId(R.id.list), EspressoUtils.UI_TEST_TIMEOUT));
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
        firebaseHelper.sendMessage(msg1);

        //TODO: send message
        SystemClock.sleep(EspressoUtils.FIREBASE_DURATION);

        //Tap on the request
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));

        onView(isRoot()).perform(waitForMatch(withId(R.id.list), EspressoUtils.UI_TEST_TIMEOUT));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).perform(click());

        //Tap on Challenge
        tryIsDisplayed(withId(R.id.accept_btn), EspressoUtils.UI_TEST_TIMEOUT);
        onView(withId(R.id.accept_btn)).perform(click());

        tryIsDisplayed(withId(R.id.time_radio), EspressoUtils.UI_TEST_TIMEOUT);
    }

}