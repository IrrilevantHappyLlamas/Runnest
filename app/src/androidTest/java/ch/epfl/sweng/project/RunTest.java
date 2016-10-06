package ch.epfl.sweng.project;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test suite for Run class
 *
 * @author Tobia Albergoni
 */
public class RunTest {

    @Test
    public void run_constructorsWork() {
        Run testRun1 = new Run();

        Assert.assertFalse(testRun1.isRunning());
        Assert.assertEquals("temp", testRun1.getName());

        String name = "test_name";
        Run testRun2 = new Run(name);

        Assert.assertFalse(testRun2.isRunning());
        Assert.assertEquals(name, testRun2.getName());
    }

    @Test
    public void run_cannotStopOrUpdateNotStartedRun() {
        Run testRun1 = new Run();

        Assert.assertFalse(testRun1.stop());
        Assert.assertFalse(testRun1.update(TrackTest.buildCheckPoint(1, 1, 1)));
        Assert.assertFalse(testRun1.isRunning());
    }

    @Test
    public void run_correctlyUpdatesStartedRun() {
        Run testRun1 = new Run();

        Assert.assertTrue(testRun1.start(TrackTest.buildCheckPoint(1, 1, 1)));
        Assert.assertTrue(testRun1.isRunning());
        Assert.assertTrue(testRun1.update(TrackTest.buildCheckPoint(2, 2, 2)));

        Track runTrack = testRun1.getTrack();

        Assert.assertEquals(2, runTrack.getTotalCheckPoints(), 0);
        Assert.assertEquals(1, runTrack.getDuration(), 0);
        Assert.assertEquals(2, runTrack.getLastPoint().getTime(), 0);
        Assert.assertEquals(2, runTrack.getLastPoint().getLatitude(), 0);
        Assert.assertEquals(2, runTrack.getLastPoint().getLongitude(), 0);

    }

    @Test
    public void run_stopWorks() {
        Run testRun1 = new Run();

        Assert.assertTrue(testRun1.start(TrackTest.buildCheckPoint(1, 1, 1)));
        Assert.assertTrue(testRun1.isRunning());

        Assert.assertTrue(testRun1.stop());
        Assert.assertFalse(testRun1.isRunning());
    }
}
