package ch.epfl.sweng.project.espresso;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

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
import java.util.HashMap;
import java.util.Map;

import ch.epfl.sweng.project.Activities.SideBarActivity;
import ch.epfl.sweng.project.AppRunnest;
import ch.epfl.sweng.project.Fragments.DisplayUserFragment;
import ch.epfl.sweng.project.Model.Challenge;
import ch.epfl.sweng.project.Model.Message;
import ch.epfl.sweng.project.Model.Run;
import ch.epfl.sweng.project.Model.TestUser;
import ch.epfl.sweng.project.TrackTest;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.sweng.project.espresso.EspressoUtils.waitForMatch;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BackHandTest {
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


    //TODO: do we really need this test?
    @Test
    public void displayUserFragmentCanBeInstanced() {
        Map<String, String> map = new HashMap<>();
        map.put("testId", "testName");
        DisplayUserFragment.newInstance(map);
    }

    @Test
    public void lifecycleTest() {
        mActivityRule.getActivity().finish();
        mActivityRule.getActivity();
    }
}
