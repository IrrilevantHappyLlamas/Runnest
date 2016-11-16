package ch.epfl.sweng.project;

import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import ch.epfl.sweng.project.Activities.FirebaseProxyActivity;
import ch.epfl.sweng.project.Firebase.FirebaseHelper;
import ch.epfl.sweng.project.Model.CheckPoint;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Test suite for demo activity
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FirebaseProxyActivityTest {

    private final int WAIT_DURATION = 2000;
    private final String LOCAL_USER = "AClocal";
    private final String REMOTE_OPPONENT = "ACremote";

    @Rule
    public ActivityTestRule<FirebaseProxyActivity> mActivityRule = new ActivityTestRule<>(
            FirebaseProxyActivity.class);

    @Test
    public void completeDemoTest() {

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.reset_btn)).perform(click());

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.local_user)).perform(typeText(LOCAL_USER));
        onView(withId(R.id.remote_opponent)).perform(typeText(REMOTE_OPPONENT));

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.set_challenge)).perform(click());

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.local_user_ready)).perform(click());
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.setUserReady(LOCAL_USER + "_vs_" + REMOTE_OPPONENT, REMOTE_OPPONENT);

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.checkpoint_latitude)).perform(typeText("100"));
        onView(withId(R.id.checkpoint_longitude)).perform(typeText("101"));

        SystemClock.sleep(WAIT_DURATION);

        onView(withId(R.id.put_checkpoint_btn)).perform(click());

        SystemClock.sleep(WAIT_DURATION);

    }
}
