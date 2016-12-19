package ch.epfl.sweng.project.espresso;


import android.os.SystemClock;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.util.TreeIterables;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.concurrent.TimeoutException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;

public class EspressoUtils {

    static final int UI_TEST_TIMEOUT = 10000;
    static final int FIREBASE_DURATION = 3000;
    static final int TIME_CHALLENGE_DURATION = 15000;
    static final int MOCK_LOCATION_DURATION = 5000;

    public static ViewAction waitForMatch(final Matcher<View> aViewMatcher, final long timeout) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Waiting for view matching " + aViewMatcher;
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadUntilIdle();

                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + timeout;

                do {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                        if (aViewMatcher.matches(child)) {
                            // found
                            return;
                        }
                    }


                    uiController.loopMainThreadForAtLeast(50);
                } while (System.currentTimeMillis() < endTime);

                //The action has timed out.
                throw new PerformException.Builder()
                        .withActionDescription(getDescription())
                        .withViewDescription("")
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }

    public static void tryIsDisplayed(final Matcher<View> viewMatcher, long timeout) {
        final long startTime = System.currentTimeMillis();
        final long endTime = startTime + timeout;

        do {
            try {
                //onView(viewMatcher).perform(click());
                onView(viewMatcher).check(matches(isDisplayed()));
                return;
            } catch (Exception e) {
                SystemClock.sleep(500);
            }
        } while (System.currentTimeMillis() < endTime);

        //Try one last time and throw the Exception
        onView(viewMatcher).check(matches(isDisplayed()));
    }
}
