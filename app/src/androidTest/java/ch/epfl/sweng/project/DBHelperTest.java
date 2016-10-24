package ch.epfl.sweng.project;

import android.content.Context;
import android.location.Location;
import android.support.test.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Run;
import ch.epfl.sweng.project.Model.Track;

public class DBHelperTest {
    private DBHelper dbHelper;

    @Before
    public void setUp() {
        Context testContext = InstrumentationRegistry.getTargetContext();
        dbHelper = new DBHelper(testContext);
    }

    @Test
    public void canInsertNewEffort() {
        Track testTrack = new Track();
        testTrack.add(new CheckPoint(2,2,0));
        testTrack.add(new CheckPoint(2,3,0));
        testTrack.add(new CheckPoint(2,4,0));

        Run testRun = new Run("test");
        testRun.setTrack(testTrack);

        boolean isInserted = dbHelper.insert(testRun);
        Assert.assertTrue(isInserted);
    }

    @Test
    public void canRetrieveData() {
        List<Run> efforts = dbHelper.fetchAllEfforts();
        Assert.assertTrue(!efforts.isEmpty());
    }

    @Test
    public void lastAddedIsLastRetrieved() {
        List<Run> efforts = dbHelper.fetchAllEfforts();
        Run lastRun = efforts.get(efforts.size() - 1);
        String name = lastRun.getName();
        Assert.assertEquals("test", name);
        Track track = lastRun.getTrack();
        Assert.assertEquals(3, track.getTotalCheckPoints());
        Assert.assertEquals(222504, track.getDistance(), 1);

        //TODO: adapt this test
        //Assert.assertEquals(2, lastRun.getDuration(), 0);
    }
}