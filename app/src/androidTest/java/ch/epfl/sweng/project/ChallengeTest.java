package ch.epfl.sweng.project;

import org.junit.Assert;
import org.junit.Test;

import ch.epfl.sweng.project.Model.Challenge;
import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Run;
import ch.epfl.sweng.project.Model.Track;

public class ChallengeTest {
    private Run createTestRun() {
        Track testTrack = new Track();
        testTrack.add(new CheckPoint(2,2));

        Run testRun = new Run("test");
        testRun.setTrack(testTrack);
        testRun.setDuration(10);

        return testRun;
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullOpponentNameThrowsException() {
        Challenge challenge = new Challenge(null, Challenge.Type.DISTANCE, 100, Challenge.Result.WON, createTestRun(), createTestRun());
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyOpponentNameThrowsException() {
        Challenge challenge = new Challenge("", Challenge.Type.TIME, 100, Challenge.Result.LOST, createTestRun(), createTestRun());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullMyRunThrowsException() {
        Challenge challenge = new Challenge("someone", Challenge.Type.TIME, 100, Challenge.Result.ABORTED_BY_OTHER, null, createTestRun());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullOpponentRunThrowsException() {
        Challenge challenge = new Challenge("someone", Challenge.Type.TIME, 100, Challenge.Result.ABORTED_BY_ME, createTestRun(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullTypeThrowsException() {
        Challenge challenge = new Challenge("someone", null, 100, Challenge.Result.WON, createTestRun(), createTestRun());
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeGoalThrowsException() {
        Challenge challenge = new Challenge("someone", Challenge.Type.DISTANCE, -100, Challenge.Result.WON, createTestRun(), createTestRun());
    }

    @Test(expected = IllegalArgumentException.class)
    public void zeroGoalThrowsException() {
        Challenge challenge = new Challenge("someone", Challenge.Type.DISTANCE, 0, Challenge.Result.WON, createTestRun(), createTestRun());
    }

    @Test
    public void getters() {
        String opponentName = "someone";
        Challenge.Type type = Challenge.Type.DISTANCE;
        double goal = 123;
        Challenge.Result result = Challenge.Result.WON;
        Run myRun = createTestRun();
        Run opponentRun = createTestRun();
        Challenge challenge = new Challenge(opponentName, type, goal, result, myRun, opponentRun);

        Assert.assertTrue(opponentName.equals(challenge.getOpponentName()));
        Assert.assertTrue(type == challenge.getType());
        Assert.assertEquals(goal, challenge.getGoal(), 0);
        Assert.assertTrue(result == challenge.getResult());
        Assert.assertEquals(myRun.getDuration(), challenge.getMyRun().getDuration());
        Assert.assertEquals(opponentRun.getDuration(), challenge.getOpponentRun().getDuration());
        Assert.assertTrue(challenge.isWon());
    }

    @Test
    public void setters() {
        String opponentName = "someone";
        Run myRun = createTestRun();
        Run opponentRun = createTestRun();
        Challenge challenge = new Challenge(opponentName, Challenge.Type.TIME, 100, Challenge.Result.LOST, myRun, opponentRun);
        Assert.assertFalse(challenge.isWon());

        long id = 1234;
        challenge.setId(id);
        Assert.assertEquals(id, challenge.getId());
    }
}
