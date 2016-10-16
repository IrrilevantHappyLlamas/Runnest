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

    public static CheckPoint buildCheckPoint(double lat, double lon, long time) {
        Location location = new Location("test");
        location.setLatitude(lat);
        location.setLongitude(lon);
        location.setTime(time);
        return new CheckPoint(location);
    }

    @Before
    public void setUp() {
        Context testContext = InstrumentationRegistry.getTargetContext();
        dbHelper = new DBHelper(testContext);
    }

    @Test
    public void canInsertNewEffort() {
        Track testTrack = new Track();
        testTrack.add(buildCheckPoint(2, 2, 2));
        testTrack.add(buildCheckPoint(3, 3, 3));
        testTrack.add(buildCheckPoint(4, 4, 4));

        Run testRun = new Run("test");
        testRun.setTrack(testTrack);

        boolean isInserted = dbHelper.insert(testRun);
        Assert.assertTrue(isInserted);
    }

    @Test
    public void canRetrieveData() {
        List<Run> efforts = dbHelper.fetchAllEfforts();
        Assert.assertTrue(efforts.size() > 0);
    }

    @Test
    public void lastAddedIsLastRetrieved() {
        List<Run> efforts = dbHelper.fetchAllEfforts();
        Run lastRun = efforts.get(efforts.size() - 1);
        String name = lastRun.getName();
        Assert.assertEquals("test", name);
        Track track = lastRun.getTrack();
        Assert.assertEquals(3, track.getTotalCheckPoints());
    }
}