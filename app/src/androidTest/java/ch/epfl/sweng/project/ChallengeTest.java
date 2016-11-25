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
        Run myRun = createTestRun();
        Run opponentRun = createTestRun();
        Challenge challenge = new Challenge(null, myRun, opponentRun);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyOpponentNameThrowsException() {
        Run myRun = createTestRun();
        Run opponentRun = createTestRun();
        Challenge challenge = new Challenge("", myRun, opponentRun);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullMyRunThrowsException() {
        Challenge challenge = new Challenge("someone", null, createTestRun());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullOpponentRunThrowsException() {
        Challenge challenge = new Challenge("someone", createTestRun(), null);
    }

    @Test
    public void getters() {
        String opponentName = "someone";
        Run myRun = createTestRun();
        Run opponentRun = createTestRun();
        Challenge challenge = new Challenge(opponentName, myRun, opponentRun);

        Assert.assertTrue(opponentName.equals(challenge.getOpponentName()));
        Assert.assertEquals(myRun.getDuration(), challenge.getMyRun().getDuration());
        Assert.assertEquals(opponentRun.getDuration(), challenge.getOpponentRun().getDuration());
    }

    @Test
    public void setId() {
        String opponentName = "someone";
        Run myRun = createTestRun();
        Run opponentRun = createTestRun();
        Challenge challenge = new Challenge(opponentName, myRun, opponentRun);

        long id = 1234;
        challenge.setId(id);
        Assert.assertEquals(id, challenge.getId());
    }
}
