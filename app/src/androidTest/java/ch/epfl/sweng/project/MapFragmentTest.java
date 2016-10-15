package ch.epfl.sweng.project;

import org.junit.Assert;
import org.junit.Test;

import ch.epfl.sweng.project.Fragments.MapFragment;

public class MapFragmentTest {

    @Test
    public void defaultConstructorWork(){

        MapFragment testFragment = null;

        testFragment = new MapFragment();

        Assert.assertNotEquals(null, testFragment);
    }

}
