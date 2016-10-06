package ch.epfl.sweng.project;

import android.location.Location;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test suite for CheckPoint class
 *
 * @author Tobia Albergoni
 */
public class CheckPointTest {

    @Test
    public void checkPoint_correctLocationEncapsulation() {
        Location location = new Location("test");
        location.setLatitude(50);
        location.setLongitude(40);
        location.setTime(1000);

        CheckPoint toTest = new CheckPoint(location);

        location.setLatitude(10);
        location.setLongitude(20);
        location.setTime(100);

        Assert.assertEquals(50, toTest.getLatitude(), 0);
        Assert.assertEquals(40, toTest.getLongitude(), 0);
        Assert.assertEquals(1000, toTest.getTime(), 0);
    }

    @Test
    public void checkPoint_computeDistance() {
        Location location1 = new Location("test");
        location1.setLatitude(50);
        location1.setLongitude(40);
        location1.setTime(1000);

        Location location2 = new Location("test");
        location2.setLatitude(60);
        location2.setLongitude(50);
        location2.setTime(2000);

        CheckPoint toTest1 = new CheckPoint(location1);
        CheckPoint toTest2 = new CheckPoint(location2);

        Assert.assertEquals(location1.distanceTo(location2), toTest1.distanceTo(toTest2), 0.0f);
    }

    @Test
    public void checkPoint_distanceToSamePointIsZero() {
        Location location1 = new Location("test");
        location1.setLatitude(50);
        location1.setLongitude(40);
        location1.setTime(1000);

        CheckPoint toTest1 = new CheckPoint(location1);
        CheckPoint toTest2 = new CheckPoint(location1);

        Assert.assertEquals(0, toTest1.distanceTo(toTest2), 0.0f);
    }
}
