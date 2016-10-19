package ch.epfl.sweng.project;

import android.support.test.espresso.Espresso;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.project.Activities.SideBarActivity;

/**
 * Test suite for SideBarActivity
 */
@LargeTest
public class SideBarActivityTest {

    @Rule
    public ActivityTestRule<SideBarActivity> mActivityRule = new ActivityTestRule<SideBarActivity>(SideBarActivity.class);

    @Test
    public void onProfileTabPressedChangeFragment() {

    }

}
