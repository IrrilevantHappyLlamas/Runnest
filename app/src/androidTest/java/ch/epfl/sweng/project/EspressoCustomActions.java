package ch.epfl.sweng.project;


import android.os.SystemClock;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.util.TreeIterables;
import android.view.View;

import org.hamcrest.Matcher;

import java.util.concurrent.TimeoutException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;

public class EspressoCustomActions {

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
                SystemClock.sleep(250);
            }
        } while (System.currentTimeMillis() < endTime);

        //Try one last time and throw the Exception
        onView(viewMatcher).check(matches(isDisplayed()));
    }
}
