package ch.epfl.sweng.project;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Unit tests!
 */
@RunWith(AndroidJUnit4.class)
public final class ExampleAndroidTest {
    // TODO replace this with your own tests.

    @Test
    public void onePlusOneIsTwo() {
        assertThat(MainActivity.add(1, 1), is(2));
    }

    @Test
    public void twoPlusTwoIsFour() {
        assertThat(MainActivity.add(2, 2), is(4));
    }

    @Test
    public void packageNameIsCorrect() {
        final Context context = InstrumentationRegistry.getTargetContext();
        assertThat(context.getPackageName(), is("ch.epfl.sweng.project"));
    }
}
