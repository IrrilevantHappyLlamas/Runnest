package ch.epfl.sweng.project;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import ch.epfl.sweng.project.Database.DBHelper;
import ch.epfl.sweng.project.Model.Challenge;
import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Run;
import ch.epfl.sweng.project.Model.Track;

public class DBHelperTest {
    private DBHelper dbHelper;

    private Run createTestRun() {
        Track testTrack = new Track();
        testTrack.add(new CheckPoint(2,2));
        testTrack.add(new CheckPoint(2,3));
        testTrack.add(new CheckPoint(2,4));

        Run testRun = new Run("test");
        testRun.setTrack(testTrack);
        testRun.setDuration(10);

        return testRun;
    }

    @Before
    public void setUp() {
        Context testContext = InstrumentationRegistry.getTargetContext();
        dbHelper = new DBHelper(testContext);
    }

    @Test
    public void canInsertNewRun() {
        boolean isInserted = dbHelper.insert(createTestRun());
        Assert.assertTrue(isInserted);
    }

    @Test
    public void canRetrieveRuns() {
        List<Run> runs = dbHelper.fetchAllRuns();
        Assert.assertFalse(runs.isEmpty());
    }

    @Test
    public void lastAddedIsLastRetrieved() {
        List<Run> runs = dbHelper.fetchAllRuns();
        Run lastRun = runs.get(runs.size() - 1);
        String name = lastRun.getName();
        Assert.assertEquals("test", name);
        Track track = lastRun.getTrack();
        Assert.assertEquals(3, track.getTotalCheckPoints());
        Assert.assertEquals(222504, track.getDistance(), 1);
        Assert.assertEquals(10, lastRun.getDuration(), 0);
    }

    @Test
    public void canDeleteRun() {
        List<Run> runs = dbHelper.fetchAllRuns();
        int initialNbRuns = runs.size();

        boolean isInserted = dbHelper.insert(createTestRun());
        Assert.assertTrue(isInserted);

        runs = dbHelper.fetchAllRuns();
        Assert.assertEquals(initialNbRuns + 1, runs.size());

        Run myRun = runs.get(0);
        dbHelper.delete(myRun);

        runs = dbHelper.fetchAllRuns();
        Assert.assertEquals(initialNbRuns, runs.size());
    }

    @Test
    public void canInsertNewChallenge() {
        Challenge challenge = new Challenge("someone", createTestRun(), createTestRun());
        Assert.assertTrue(dbHelper.insert(challenge));
    }

    @Test
    public void canRetrieveChallenges() {
        List<Challenge> challenges = dbHelper.fetchAllChallenges();
        Assert.assertFalse(challenges.isEmpty());
    }

    @Test
    public void canDeleteChallenge() {
        List<Challenge> challenges = dbHelper.fetchAllChallenges();
        int initialNbRuns = challenges.size();

        Challenge challenge = new Challenge("someone", createTestRun(), createTestRun());
        Assert.assertTrue(dbHelper.insert(challenge));

        challenges = dbHelper.fetchAllChallenges();
        Assert.assertEquals(initialNbRuns + 1, challenges.size());

        Challenge myChallenge = challenges.get(0);
        Assert.assertTrue(dbHelper.delete(myChallenge));

        challenges = dbHelper.fetchAllChallenges();
        Assert.assertEquals(initialNbRuns, challenges.size());
    }
}