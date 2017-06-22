package ch.ihl.runnest;

import org.junit.Assert;
import org.junit.Test;

import ch.ihl.runnest.Fragments.RunFragments.RunningMapFragment;

public class RunningMapTest {

    @Test
    public void defaultConstructorWork(){

        RunningMapFragment testFragment = null;

        testFragment = new RunningMapFragment();

        Assert.assertNotEquals(null, testFragment);
    }

}
