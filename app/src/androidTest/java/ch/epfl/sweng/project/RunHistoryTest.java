package ch.epfl.sweng.project;

import android.os.SystemClock;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import ch.epfl.sweng.project.Activities.SideBarActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by riccardoconti on 28.10.16.
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RunHistoryTest {

    @Rule
    public ActivityTestRule<SideBarActivity> mActivityRule = new ActivityTestRule<>(
            SideBarActivity.class);

    @Before
    public void setUpApp() {
        ((AppRunnest) mActivityRule.getActivity().getApplication()).setGoogleUser(null);
    }

    @Test
    public void clickOnRunDetails() {
        SystemClock.sleep(1500);

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(1500);

        onView(withId(R.id.nav_view)).perform((NavigationViewActions.navigateTo(R.id.nav_run_history)));

        onView(withId(R.id.list)).perform(click());

        //TODO: check something
        SystemClock.sleep(1500);
    }

    @Test
    public void startRun(){
        SystemClock.sleep(1500);

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(1500);

        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_new_run));

        onView(withId(R.id.start_run))
                .check(matches(isDisplayed()));

        onView(withId(R.id.start_run))
                .perform(click());

        SystemClock.sleep(1500);

        onView(withId(R.id.stop_run))
                .check(matches(isDisplayed()));

        onView(withId(R.id.stop_run))
                .perform(click());

        SystemClock.sleep(1500);

        onView(withId(R.id.go_to_run_history))
                .perform(click());

        SystemClock.sleep(1500);


        onView(withId(R.id.list)).check(matches(isDisplayed()));

        SystemClock.sleep(1500);

    }


}
