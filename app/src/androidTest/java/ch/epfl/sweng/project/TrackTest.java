package ch.epfl.sweng.project;

import android.location.Location;

import junit.framework.Assert;

import org.junit.Test;

import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Track;

/**
 * Test suite for Track class
 *
 * @author Tobia Albergoni
 */
public class TrackTest {

    public static CheckPoint buildCheckPoint(double lat, double lon, long time) {
        Location location = new Location("test");
        location.setLatitude(lat);
        location.setLongitude(lon);
        location.setTime(time);
        return new CheckPoint(location);
    }

    @Test
    public void track_correctConstruction() {
        Track testTrack = new Track(buildCheckPoint(50, 50, 100));

        Assert.assertEquals(0, testTrack.getDistance(), 0);
        Assert.assertEquals(1, testTrack.getTotalCheckPoints(), 0);
        Assert.assertEquals(0, testTrack.getDuration(), 0);

        Assert.assertEquals(50, testTrack.getLastPoint().getLatitude(), 0);
        Assert.assertEquals(50, testTrack.getLastPoint().getLongitude(), 0);
        Assert.assertEquals(100, testTrack.getLastPoint().getTime(), 0);
    }

    @Test
    public void track_legitAddCorrectlyUpdatesTrack() {
        CheckPoint c1 = buildCheckPoint(50, 50, 100);
        CheckPoint c2 = buildCheckPoint(51, 51, 110);

        Track testTrack = new Track(c1);

        Assert.assertTrue(testTrack.add(c2));

        Assert.assertEquals(2, testTrack.getTotalCheckPoints());
        Assert.assertEquals(10, testTrack.getDuration());
        Assert.assertEquals(c2.distanceTo(c1), testTrack.getDistance());
    }

    @Test
    public void track_incoherentAddFails() {
        CheckPoint c1 = buildCheckPoint(50, 50, 100);
        CheckPoint c2 = buildCheckPoint(51, 51, 90);

        Track testTrack = new Track(c1);

        Assert.assertFalse(testTrack.add(c2));
    }

    @Test
    public void track_correctCopyConstructor() {
        Track testTrack1 = new Track(buildCheckPoint(1, 1, 1));
        testTrack1.add(buildCheckPoint(2, 2, 2));
        testTrack1.add(buildCheckPoint(3, 3, 3));

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

}
