package ch.epfl.sweng.project.espresso;

import android.support.test.runner.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.sweng.project.Activities.SideBarActivity;
import ch.epfl.sweng.project.Fragments.DisplayUserFragment;
import ch.epfl.sweng.project.Model.Challenge;
import ch.epfl.sweng.project.Model.Message;
import ch.epfl.sweng.project.Model.Run;
import ch.epfl.sweng.project.TrackTest;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BackHandTest extends EspressoTest {

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
    public void lifecycleTest() {
        mActivityRule.getActivity().finish();
        mActivityRule.getActivity();
    }
}
