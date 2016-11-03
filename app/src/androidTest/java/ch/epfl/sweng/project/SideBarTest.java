package ch.epfl.sweng.project;

import android.os.SystemClock;
import android.support.test.espresso.action.EspressoKey;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;

import com.example.android.multidex.ch.epfl.sweng.project.AppRunnest.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import ch.epfl.sweng.project.Activities.SideBarActivity;

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

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SideBarTest {

    @Rule
    public ActivityTestRule<SideBarActivity> mActivityRule = new ActivityTestRule<>(
            SideBarActivity.class);

    @Before
    public void setUpApp() {
        ((AppRunnest) mActivityRule.getActivity().getApplication()).setGoogleUser(null);
    }

    @Test
    public void ADrawerLayout() {
        SystemClock.sleep(3000);

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(3000);

        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
    }

    @Test
    public void navigateToRunningMap() {
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(3000);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_new_run));
    }

    @Test
    public void navigateToRunHistory() {
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(3000);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_run_history));
    }

    @Test
    public void navigateToMessages() {

        SystemClock.sleep(3000);

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(3000);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));
    }

    @Test
    public void navigateToProfile() {
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(3000);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_profile));
    }

    @Test
    public void navigateToLogout() {
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(3000);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_logout));

        onView(withText("OK"))
                .perform(click());

        SystemClock.sleep(3000);
    }

    @Test
    public void searchAndClickOnChallenge() {
        onView(withId(R.id.search)).perform(click());
        SystemClock.sleep(3000);

        onView(isAssignableFrom(EditText.class)).perform(typeText("pablo"), pressKey(KeyEvent.KEYCODE_ENTER));
        SystemClock.sleep(3000);
        onView(withId(R.id.table)).check(matches(isDisplayed()));
        SystemClock.sleep(2000);
        onView(isAssignableFrom(Button.class)).perform(click());
        SystemClock.sleep(2000);
        onView(withId(R.id.waitChallengedUserTextView)).check(matches(isDisplayed()));
    }

    @Test
    public void searchInexistentUser() {
        onView(withId(R.id.search)).perform(click());
        SystemClock.sleep(3000);

        onView(isAssignableFrom(EditText.class)).perform(typeText("kadfjisadsa"), pressKey(KeyEvent.KEYCODE_ENTER));
        SystemClock.sleep(3000);
        onView(withId(R.id.table)).check(matches(isDisplayed()));
        onView(withText("No user found.")).check(matches(isDisplayed()));
    }

    @Test
    public void acceptChallengeRequest() {

        SystemClock.sleep(3000);

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(3000);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));

        SystemClock.sleep(3000);

        onView(withText("From: Pablo\nType: CHALLENGE_REQUEST")).perform(click());

        onView(withText("ACCEPT")).perform(click());
        SystemClock.sleep(3000);
    }

    @Test
    public void declineChallengeRequest() {

        SystemClock.sleep(3000);

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        SystemClock.sleep(3000);

        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_messages));

        SystemClock.sleep(3000);

        onView(withText("From: Pablo\nType: CHALLENGE_REQUEST")).perform(click());

        onView(withText("DECLINE")).perform(click());
        SystemClock.sleep(3000);
    }

}
