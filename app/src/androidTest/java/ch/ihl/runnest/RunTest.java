package ch.ihl.runnest;

import android.os.SystemClock;

import org.junit.Assert;
import org.junit.Test;

import ch.ihl.runnest.Model.Run;
import ch.ihl.runnest.Model.Track;

/**
 * Test suite for Run class
 */
public class RunTest {

    @Test
    public void constructorsWork() {
        Run testRun1 = new Run();

        Assert.assertFalse(testRun1.isRunning());
        Assert.assertEquals("tmp", testRun1.getName());
        Assert.assertNotNull(testRun1.getTrack());
        Assert.assertEquals(0, testRun1.getDuration());
        Assert.assertEquals(-1, testRun1.getId());

        String name = "test_name";
        Run testRun2 = new Run(name);

        Assert.assertFalse(testRun2.isRunning());
        Assert.assertEquals(name, testRun2.getName());
        Assert.assertNotNull(testRun2.getTrack());
        Assert.assertEquals(0, testRun2.getDuration());
        Assert.assertEquals(-1, testRun2.getId());

        long id = 1234;
        Run testRun3 = new Run(name, id);
        Assert.assertEquals(id, testRun3.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWithNameThrowsIllegalArgumentOnEmpty() {
        new Run("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWithNameThrowsIllegalArgumentOnNull() {
        String nullString = null;
        new Run(nullString);
    }

    @Test
    public void copyConstructorWorks() {
        Run testRun = new Run("test");

        testRun.start();
        testRun.update(TrackTest.buildCheckPoint(1, 1));
        testRun.update(TrackTest.buildCheckPoint(2, 2));

        Run testRun2 = new Run(testRun);

        Assert.assertEquals(testRun.getName(), testRun2.getName());
        Assert.assertEquals(testRun.getTrack().getDistance(), testRun2.getTrack().getDistance(), 0);
        Assert.assertEquals(testRun.getDuration(), testRun2.getDuration(), 0);
        Assert.assertEquals(testRun.getTrack().getTotalCheckPoints(), testRun2.getTrack().getTotalCheckPoints());

        Assert.assertFalse(testRun2.isRunning());

        testRun.update(TrackTest.buildCheckPoint(3, 3));
        testRun.stop();

        Assert.assertNotEquals(testRun.getTrack().getTotalCheckPoints(), testRun2.getTrack().getTotalCheckPoints());
    }

    @Test(expected = IllegalArgumentException.class)
    public void copyConstructorThrowsIllegalArgumentOnNull() {
        Run nullRun = null;
        new Run(nullRun);
    }

    @Test
    public void cannotStopOrUpdateNotStartedRun() {
        Run testRun1 = new Run();

        Assert.assertFalse(testRun1.stop());
        Assert.assertFalse(testRun1.update(TrackTest.buildCheckPoint(1, 1)));
        Assert.assertFalse(testRun1.isRunning());
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateThrowsIllegalArgument() {
        new Run().update(null);
    }

    @Test
    public void correctlyUpdatesStartedRun() {
        Run testRun1 = new Run();

        testRun1.start();

        Assert.assertTrue(testRun1.update(TrackTest.buildCheckPoint(1, 1)));
        Assert.assertTrue(testRun1.isRunning());
        Assert.assertTrue(testRun1.update(TrackTest.buildCheckPoint(2, 2)));

        Track runTrack = testRun1.getTrack();

        Assert.assertEquals(2, runTrack.getTotalCheckPoints(), 0);
        Assert.assertEquals(2, runTrack.getLastPoint().getLatitude(), 0);
        Assert.assertEquals(2, runTrack.getLastPoint().getLongitude(), 0);

    }

    @Test
    public void stopWorks() {
        Run testRun2 = new Run();

        testRun2.start();

        Assert.assertTrue(testRun2.update(TrackTest.buildCheckPoint(1, 1)));
        Assert.assertTrue(testRun2.isRunning());

        Assert.assertTrue(testRun2.stop());
        Assert.assertFalse(testRun2.isRunning());
    }


    @Test(expected = IllegalArgumentException.class)
    public void setDurationThrowsIllegalArgument() {
        new Run().setDuration(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setTrackThrowsIllegalArgument() {
        new Run().setTrack(null);
    }

    @Test
    public void canNotStartRunIfAlreadyStarted() {
        Run testRun1 = new Run();
        testRun1.start();
        Assert.assertTrue(testRun1.update(TrackTest.buildCheckPoint(1, 1)));

        Assert.assertFalse(testRun1.start());
    }

    @Test
    public void startedRunDurationIsNotZero() {
        Run testRun1 = new Run();
        testRun1.start();

        SystemClock.sleep(2000);

        Assert.assertNotEquals(0, testRun1.getDuration());
    }

    @Test
    public void setIdWorks() {
        Run run = new Run();
        Assert.assertEquals(-1, run.getId());
        long id = 123;
        run.setId(id);
        Assert.assertEquals(id, run.getId());
    }
}
