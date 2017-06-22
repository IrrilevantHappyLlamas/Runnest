package ch.ihl.runnest;

import android.location.Location;

import org.junit.Assert;
import org.junit.Test;

import ch.ihl.runnest.Model.CheckPoint;

/**
 * Test suite for CheckPoint class
 */
@SuppressWarnings("MagicNumber")
public class CheckPointTest {

    @Test(expected = IllegalArgumentException.class)
    public void constructorWithLocationThrowsIllegalArgument() {
        new CheckPoint(null);
    }

    @Test
    public void correctLocationEncapsulation() {
        Location location = new Location("test");
        location.setLatitude(50);
        location.setLongitude(40);
        location.setAltitude(30);

        CheckPoint toTest = new CheckPoint(location);

        location.setLatitude(10);
        location.setLongitude(20);

        Assert.assertEquals(50, toTest.getLatitude(), 0);
        Assert.assertEquals(40, toTest.getLongitude(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWitCoordinatesThrowsIllegalArgumentOnLatitude() {
        new CheckPoint(100, 120);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWitCoordinatesThrowsIllegalArgumentOnLongitude() {
        new CheckPoint(0, 200);
    }

    @Test(expected = IllegalArgumentException.class)
    public void computeDistanceThrowsIllegalArgument() {
        new CheckPoint(0, 0).distanceTo(null);
    }

    @Test
    public void computeDistance() {
        Location location1 = new Location("test");
        location1.setLatitude(50);
        location1.setLongitude(40);

        Location location2 = new Location("test");
        location2.setLatitude(60);
        location2.setLongitude(50);

        CheckPoint toTest1 = new CheckPoint(location1);
        CheckPoint toTest2 = new CheckPoint(location2);

        Assert.assertEquals(location1.distanceTo(location2), toTest1.distanceTo(toTest2), 0.0f);
    }

    @Test
    public void distanceToSamePointIsZero() {
        Location location1 = new Location("test");
        location1.setLatitude(50);
        location1.setLongitude(40);

        CheckPoint toTest1 = new CheckPoint(location1);
        CheckPoint toTest2 = new CheckPoint(location1);

        Assert.assertEquals(0, toTest1.distanceTo(toTest2), 0.0f);
    }
}
