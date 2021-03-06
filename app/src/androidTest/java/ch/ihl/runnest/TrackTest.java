package ch.ihl.runnest;

import android.location.Location;

import junit.framework.Assert;

import org.junit.Test;

import java.util.List;

import ch.ihl.runnest.Model.CheckPoint;
import ch.ihl.runnest.Model.Track;

/**
 * Test suite for Track class
 */
@SuppressWarnings("MagicNumber")
public class TrackTest {

    public static CheckPoint buildCheckPoint(double lat, double lon) {
        Location location = new Location("test");
        location.setLatitude(lat);
        location.setLongitude(lon);
        return new CheckPoint(location);
    }

    @Test
    public void correctEmptyConstruction() {
        Track testTrack = new Track();

        Assert.assertEquals(0, testTrack.getDistance(), 0);
        Assert.assertEquals(0, testTrack.getTotalCheckPoints(), 0);
        Assert.assertNull(testTrack.getLastPoint());
        Assert.assertNotNull(testTrack.getCheckpoints());
    }

    @Test
    public void correctConstructionFromACheckPoint() {
        Track testTrack = new Track(buildCheckPoint(50, 50));

        Assert.assertEquals(0, testTrack.getDistance(), 0);
        Assert.assertEquals(1, testTrack.getTotalCheckPoints(), 0);
        Assert.assertNotNull(testTrack.getCheckpoints());
        Assert.assertEquals(50, testTrack.getLastPoint().getLatitude(), 0);
        Assert.assertEquals(50, testTrack.getLastPoint().getLongitude(), 0);
    }


    @Test
    public void correctCopyConstructor() {
        Track testTrack1 = new Track(buildCheckPoint(1, 1));
        testTrack1.add(buildCheckPoint(2, 2));
        testTrack1.add(buildCheckPoint(3, 3));

        Track testTrack2 = new Track(testTrack1);
        float dist = testTrack1.getDistance();

        Assert.assertEquals(dist, testTrack2.getDistance(), 0);
        Assert.assertEquals(3, testTrack2.getTotalCheckPoints(), 0);

        testTrack1.add(buildCheckPoint(4, 4));

        Assert.assertEquals(dist, testTrack2.getDistance(), 0);
        Assert.assertEquals(3, testTrack2.getTotalCheckPoints(), 0);
    }

    @Test
    public void addCorrectlyUpdatesTrack() {
        CheckPoint c1 = buildCheckPoint(50, 50);
        CheckPoint c2 = buildCheckPoint(51, 51);

        Track testTrack = new Track(c1);
        testTrack.add(c2);

        Assert.assertEquals(2, testTrack.getTotalCheckPoints());
        Assert.assertEquals(51, testTrack.getLastPoint().getLatitude(), 0);
        Assert.assertEquals(51, testTrack.getLastPoint().getLongitude(), 0);
        Assert.assertEquals(c2.distanceTo(c1), testTrack.getDistance());
    }

    @Test
    public void correctGetAllCheckpoints() {
        Track testTrack = new Track(buildCheckPoint(1, 1));
        testTrack.add(buildCheckPoint(2, 2));
        testTrack.add(buildCheckPoint(3, 3));
        testTrack.add(buildCheckPoint(4, 4));
        List<CheckPoint> checkpoints = testTrack.getCheckpoints();
        Assert.assertEquals(4, checkpoints.size(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addThrowsIllegalArgument() {
        new Track().add(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void copyConstructorThrowsIllegalArgumentOnNull() {
        Track nullTrack = null;
        new Track(nullTrack);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIllegalArgumentOnNull() {
        CheckPoint nullCheckpoint = null;
        new Track(nullCheckpoint);
    }

    @Test
    public void getLastPointWorks() {
        Track testTrack = new Track(buildCheckPoint(90, 90));

        Assert.assertEquals(90.0, testTrack.getLastPoint().getLatitude());
        Assert.assertEquals(90.0, testTrack.getLastPoint().getLongitude());
    }

    @Test
    public void getLastPointWorksForEmptyTrack() {
        Assert.assertTrue(new Track().getLastPoint() == null);
    }
}
