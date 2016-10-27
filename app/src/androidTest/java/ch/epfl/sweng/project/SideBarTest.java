package ch.epfl.sweng.project;

import android.os.SystemClock;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.project.Activities.SideBarActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Tobia Albergoni on 27.10.2016.
 */
@RunWith(AndroidJUnit4.class)
public class SideBarTest {

    @Rule
    public ActivityTestRule<SideBarActivity> mActivityRule = new ActivityTestRule<>(
            SideBarActivity.class);

    @Before
    public void setUpApp() {
        ((AppRunnest) mActivityRule.getActivity().getApplication()).setGoogleUser(null);
    }

    @Test
    public void testDrawerLayout() {
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(3000);

        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
    }
}
