package ch.epfl.sweng.project;

import org.junit.Assert;
import org.junit.Test;

import ch.epfl.sweng.project.Fragments.RunFragments.RunningMapFragment;

public class RunningMapTest {

    @Test
    public void defaultConstructorWork(){

        RunningMapFragment testFragment = null;

        testFragment = new RunningMapFragment();

        Assert.assertNotEquals(null, testFragment);
    }

}
