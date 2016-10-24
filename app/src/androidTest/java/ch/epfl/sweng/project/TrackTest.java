package ch.epfl.sweng.project;

import android.location.Location;

import junit.framework.Assert;

import org.junit.Test;

import java.util.List;

import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Track;

/**
 * Test suite for Track class
 *
 * @author Tobia Albergoni
 */
@SuppressWarnings("MagicNumber")
public class TrackTest {

    public static CheckPoint buildCheckPoint(double lat, double lon, long time) {
        Location location = new Location("test");
        location.setLatitude(lat);
        location.setLongitude(lon);
        location.setTime(time);
        return new CheckPoint(location);
    }

    public static CheckPoint buildCheckPoint(double lat, double lon, long time, double altitude) {
        Location location = new Location("test");
        location.setLatitude(lat);
        location.setLongitude(lon);
        location.setTime(time);
        location.setAltitude(altitude);
        return new CheckPoint(location);
    }

    @Test
    public void correctEmptyConstruction() {
        Track testTrack = new Track();

        Assert.assertEquals(0, testTrack.getDistance(), 0);
        Assert.assertEquals(0, testTrack.getTotalCheckPoints(), 0);
        Assert.assertEquals(0, testTrack.getDuration(), 0);
    }

    @Test
    public void correctConstruction() {
        Track testTrack = new Track(buildCheckPoint(50, 50, 1000));

        Assert.assertEquals(0, testTrack.getDistance(), 0);
        Assert.assertEquals(1, testTrack.getTotalCheckPoints(), 0);
        Assert.assertEquals(0, testTrack.getDuration(), 0);

        Assert.assertEquals(50, testTrack.getLastPoint().getLatitude(), 0);
        Assert.assertEquals(50, testTrack.getLastPoint().getLongitude(), 0);
        Assert.assertEquals(1, testTrack.getLastPoint().getTime(), 0);
    }

    @Test
    public void legitAddCorrectlyUpdatesTrack() {
        CheckPoint c1 = buildCheckPoint(50, 50, 1000);
        CheckPoint c2 = buildCheckPoint(51, 51, 2000);

        Track testTrack = new Track(c1);

        Assert.assertTrue(testTrack.add(c2));

        Assert.assertEquals(2, testTrack.getTotalCheckPoints());
        Assert.assertEquals(1, testTrack.getDuration());
        Assert.assertEquals(c2.distanceTo(c1), testTrack.getDistance());
    }

    @Test
    public void incoherentAddFails() {
        CheckPoint c1 = buildCheckPoint(50, 50, 2000);
        CheckPoint c2 = buildCheckPoint(51, 51, 1000);

        Track testTrack = new Track(c1);

        Assert.assertFalse(testTrack.add(c2));
    }

    @Test
    public void correctEmptyConstructor() {
        Track testTrack = new Track();

        Assert.assertEquals(0, testTrack.getTotalCheckPoints());
        Assert.assertEquals(0.f, testTrack.getDistance());
        Assert.assertEquals(0, testTrack.getDuration());
        Assert.assertEquals(null, testTrack.getLastPoint());
    }

    @Test
    public void correctCopyConstructor() {
        Track testTrack1 = new Track(buildCheckPoint(1, 1, 1000));
        testTrack1.add(buildCheckPoint(2, 2, 2000));
        testTrack1.add(buildCheckPoint(3, 3, 3000));

        Track testTrack2 = new Track(testTrack1);
        float dist = testTrack1.getDistance();

        Assert.assertEquals(dist, testTrack2.getDistance(), 0);
        Assert.assertEquals(3, testTrack2.getTotalCheckPoints(), 0);
        Assert.assertEquals(2, testTrack2.getDuration(), 0);

        testTrack1.add(buildCheckPoint(4, 4, 4));

        Assert.assertEquals(dist, testTrack2.getDistance(), 0);
        Assert.assertEquals(3, testTrack2.getTotalCheckPoints(), 0);
        Assert.assertEquals(2, testTrack2.getDuration(), 0);
    }

    @Test
    public void correctGetAllCheckpoints() {
        Track testTrack = new Track(buildCheckPoint(1, 1, 1));
        testTrack.add(buildCheckPoint(2, 2, 2));
        testTrack.add(buildCheckPoint(3, 3, 3));
        testTrack.add(buildCheckPoint(4, 4, 4));
        List<CheckPoint> checkpoints = testTrack.getCheckpoints();
        Assert.assertEquals(4, checkpoints.size(), 0);
    }

    @Test
    public void getUphillReturnCorrectValue() {
        CheckPoint cp1 = buildCheckPoint(50, 50, 1000);
        CheckPoint cp2 = buildCheckPoint(51, 51, 2000, 157);
        CheckPoint cp3 = buildCheckPoint(51, 51, 3000, 100);
        CheckPoint cp4 = buildCheckPoint(53, 53, 4000, 200);

        Track testTrack3 = new Track(cp1);
        Assert.assertTrue(testTrack3.add(cp2));
        Assert.assertTrue(testTrack3.add(cp3));
        Assert.assertTrue(testTrack3.add(cp4));

        Assert.assertEquals(157.0, cp2.getAltitude());
        Assert.assertEquals(100.0, cp3.getAltitude());
        Assert.assertEquals(200.0, cp4.getAltitude());
        Assert.assertEquals(257.0, testTrack3.getUphill());
        Assert.assertEquals(-57.0, testTrack3.getDownhill());

    }

}
