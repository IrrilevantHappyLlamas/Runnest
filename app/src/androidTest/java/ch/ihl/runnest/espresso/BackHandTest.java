package ch.ihl.runnest.espresso;

import android.os.SystemClock;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.multidex.ch.ihl.runnest.AppRunnest.R;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Date;

import ch.ihl.runnest.Activities.SideBarActivity;
import ch.ihl.runnest.Model.Challenge;
import ch.ihl.runnest.Model.Message;
import ch.ihl.runnest.Model.Run;
import ch.ihl.runnest.TrackTest;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BackHandTest extends EspressoTest {

    @Test
    public void setRunningWorks() {
        SideBarActivity listenerTest = activityRule.getActivity();
        listenerTest.setRunning(true);
    }

    @Test
    public void runHistoryOnFragmentListenerWork() {
        SideBarActivity listenerTest = activityRule.getActivity();

        Run listenerRun= new Run();
        listenerRun.start();
        listenerRun.update(TrackTest.buildCheckPoint(1.0, 1.0));
        listenerRun.update(TrackTest.buildCheckPoint(1.0, 2.0));
        listenerRun.stop();

        listenerTest.onRunHistoryInteraction(listenerRun);
    }

    @Test
    public void uselessOnFragmentListenersWork() {
        SideBarActivity listenerTest = activityRule.getActivity();

        listenerTest.onProfileFragmentInteraction("test", "test");
    }

    @Test
    public void messageOnFragmentListenersWork() {
        SideBarActivity listenerTest = activityRule.getActivity();
        Message msg = new Message( "emailSender",
                "emailReceiver",
                "tester",
                "tested",
                Message.Type.CHALLENGE_REQUEST,
                "that's a test",
                new Date(),
                1,
                0,
                Challenge.Type.DISTANCE );
        listenerTest.onMessagesFragmentInteraction(msg);
    }

    @Test
    public void displayProfileFragmentListenersWork() {
        SideBarActivity listenerTest = activityRule.getActivity();
        listenerTest.onDisplayProfileFragmentInteraction("testName", "test@email.ch");
    }

    @Test
    public void runFragmentLifecycleTest() {

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run));

        // Jenkins sleep (1/3)
        //
        // Sleep necessary in order to successfully build on Jenkins, I wasn't able to
        // reproduce the failure in local. After a lot of attempts I decided to keep it.
        SystemClock.sleep(FIREBASE_DURATION);

        onView(isRoot()).perform(waitForMatch(withId(R.id.start_run), UI_TEST_TIMEOUT));

        activityRule.getActivity().finish();
        activityRule.getActivity();
    }

    @Test
    public void lifecycleTest() {
        activityRule.getActivity().onPause();
        activityRule.getActivity();
        activityRule.getActivity().onStop();
        activityRule.getActivity().finish();
    }
}
