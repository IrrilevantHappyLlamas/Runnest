package ch.ihl.runnest;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import ch.ihl.runnest.Database.DBHelper;
import ch.ihl.runnest.Model.Challenge;
import ch.ihl.runnest.Model.CheckPoint;
import ch.ihl.runnest.Model.Run;
import ch.ihl.runnest.Model.Track;

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

    @Test(expected = IllegalArgumentException.class)
    public void insertNullRunThrowsException() {
        Run run = null;
        dbHelper.insert(run);
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

    @Test(expected = IllegalArgumentException.class)
    public void deleteNullRunThrowsException() {
        Run run = null;
        dbHelper.delete(run);
    }

    @Test
    public void deleteInvalidIdRunReturnFalse() {
        Run run = createTestRun();

        Assert.assertFalse(dbHelper.delete(run));
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

    @Test(expected = IllegalArgumentException.class)
    public void insertNullChallengeThrowsException() {
        Challenge challenge = null;
        dbHelper.insert(challenge);
    }

    @Test
    public void canInsertNewChallenge() {
        Run run = createTestRun();
        Challenge challenge = new Challenge("someone", Challenge.Type.DISTANCE, 1.3, Challenge.Result.ABORTED_BY_OTHER, run, run);
        Assert.assertTrue(dbHelper.insert(challenge));
    }

    @Test
    public void canRetrieveChallenges() {
        List<Challenge> challenges = dbHelper.fetchAllChallenges();
        Assert.assertFalse(challenges.isEmpty());
    }

    @Test
    public void lastAddedIsLastRetrieveChallenges() {
        String opponentName = "someone";
        Challenge.Type type = Challenge.Type.DISTANCE;
        double goal = 1.3;
        Run run1 = createTestRun();
        Run run2 = createTestRun();

        Challenge challenge = new Challenge(opponentName, type, goal, Challenge.Result.LOST, run1, run2);
        Assert.assertTrue(dbHelper.insert(challenge));

        List<Challenge> challenges = dbHelper.fetchAllChallenges();
        Assert.assertFalse(challenges.isEmpty());

        Challenge last = challenges.get(challenges.size() - 1);
        Assert.assertEquals(opponentName, last.getOpponentName());
        Assert.assertEquals(type, last.getType());
        Assert.assertEquals(goal, last.getGoal(), 0);
        Assert.assertFalse(last.isWon());
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteNullChallengeThrowsException() {
        Challenge challenge = null;
        dbHelper.delete(challenge);
    }

    @Test
    public void deleteInvalidIdChallengeReturnFalse() {
        String opponentName = "someone";
        Challenge.Type type = Challenge.Type.DISTANCE;
        double goal = 1.3;
        Run run1 = createTestRun();
        Run run2 = createTestRun();
        Challenge challenge = new Challenge(opponentName, type, goal, Challenge.Result.LOST, run1, run2);

        Assert.assertFalse(dbHelper.delete(challenge));
    }

    @Test
    public void canDeleteChallenge() {
        List<Challenge> challenges = dbHelper.fetchAllChallenges();
        int initialNbRuns = challenges.size();

        Run run = createTestRun();
        Challenge challenge = new Challenge("someone", Challenge.Type.TIME, 156.57, Challenge.Result.WON, run, run);
        Assert.assertTrue(dbHelper.insert(challenge));

        challenges = dbHelper.fetchAllChallenges();
        Assert.assertEquals(initialNbRuns + 1, challenges.size());

        Challenge myChallenge = challenges.get(0);
        Assert.assertTrue(dbHelper.delete(myChallenge));

        challenges = dbHelper.fetchAllChallenges();
        Assert.assertEquals(initialNbRuns, challenges.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void upgradeThrowsExceptionWithNullDB() {
        dbHelper.onUpgrade(null, 0, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void upgradeThrowsExceptionWithIncoherentVersion() {
        dbHelper.onUpgrade(dbHelper.getDatabase(), 0, -1);
    }

    @Test
    public void upgrade() {
        dbHelper.onUpgrade(dbHelper.getDatabase(), 0, 1);
        Assert.assertTrue(dbHelper.fetchAllRuns().isEmpty());
    }
}