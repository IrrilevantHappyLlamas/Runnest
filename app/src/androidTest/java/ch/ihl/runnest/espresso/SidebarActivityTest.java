package ch.ihl.runnest.espresso;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.multidex.ch.ihl.runnest.AppRunnest.R;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import ch.ihl.runnest.Activities.SideBarActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static ch.ihl.runnest.espresso.EspressoTest.waitForMatch;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SidebarActivityTest {

    @Rule
    public final ActivityTestRule<SideBarActivity> activityRule = new ActivityTestRule<>(
            SideBarActivity.class);

    private SideBarActivity activity;

    @Before
    public void setUpApp() {
        activity = activityRule.getActivity();
        onView(isRoot()).perform(waitForMatch(withId(R.id.main_layout), EspressoTest.UI_TEST_TIMEOUT));
    }


    @Test(expected = IllegalArgumentException.class)
    public void onMemoDialogChallengeClick() {
        activity.onMemoDialogChallengeClick(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onMemoDialogCloseClick() {
        activity.onMemoDialogCloseClick(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onMemoDialogDeleteClick() {
        activity.onMemoDialogDeleteClick(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onMessagesFragmentScheduleRequestInteraction() {
        activity.onMessagesFragmentScheduleRequestInteraction(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onMessagesFragmentMemoInteraction() {
        activity.onMessagesFragmentMemoInteraction(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onMessagesFragmentInteraction() {
        activity.onMessagesFragmentInteraction(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onAcceptScheduleDialogAcceptClick() {
        activity.onReceiveScheduleDialogAcceptClick(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onAcceptScheduleDialogCancelClick() {
        activity.onReceiveScheduleDialogCancelClick(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onAcceptScheduleDialogDeclineClick() {
        activity.onReceiveScheduleDialogDeclineClick(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onActivityResult1() {
        activity.onActivityResult(0, 0, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onActivityResult2() {
        activity.onActivityResult(-1, 0, new Intent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void onChallengeDialogNegativeClick() {
        activity.onSendChallengeDialogNegativeClick(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onChallengeDialogPositiveClick() {
        activity.onSendChallengeDialogPositiveClick(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onChallengeHistoryInteraction() {
        activity.onChallengeHistoryInteraction(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onRunHistoryInteraction() {
        activity.onRunHistoryInteraction(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onChallengeDialogAcceptClick() {
        activity.onReceiveChallengeDialogAcceptClick(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onChallengeDialogDeclineClick() {
        activity.onReceiveChallengeDialogDeclineClick(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onChallengeDialogCancelClick() {
        activity.onReceiveChallengeDialogCancelClick(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onRequestScheduleDialogPositiveClick() {
        activity.onSendScheduleDialogPositiveClick(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onRequestScheduleDialogNegativeClick() {
        activity.onSendScheduleDialogNegativeClick(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onDisplayProfileFragmentInteraction() {
        activity.onDisplayProfileFragmentInteraction(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onProfileFragmentInteractionSchedule() {
        activity.onProfileFragmentInteractionSchedule(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onProfileFragmentInteraction() {
        activity.onProfileFragmentInteraction(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void onRunningMapFragmentInteraction() {
        activity.onRunningMapFragmentInteraction(null);
    }
}

