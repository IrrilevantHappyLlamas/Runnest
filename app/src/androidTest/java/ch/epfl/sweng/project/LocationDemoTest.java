package ch.epfl.sweng.project;

import org.junit.Assert;
import org.junit.Test;

import ch.epfl.sweng.project.Fragments.LocationDemo;

public class LocationDemoTest {

    @Test
    public void defaultConstructorWork(){

        LocationDemo testFragment = null;

        testFragment = new LocationDemo();

        Assert.assertNotEquals(null, testFragment);
    }

}
