package ch.ihl.runnest;

import android.os.SystemClock;

import org.junit.Assert;
import org.junit.Test;

import ch.ihl.runnest.Firebase.FirebaseProxy;
import ch.ihl.runnest.Model.ChallengeProxy;
import ch.ihl.runnest.Model.CheckPoint;

/**
 * Test suite for the firebase Proxy
 */
public class FirebaseProxyTest {

    private final String LOCAL_USER = "testLocalUser";
    private final String REMOTE_OPPONENT = "testRemoteOpponent";

    private FirebaseProxy createCorrectProxy(boolean owner) {

        String local = owner?LOCAL_USER:REMOTE_OPPONENT;
        String remote = owner?REMOTE_OPPONENT:LOCAL_USER;
        return  new FirebaseProxy(local, remote, new ChallengeProxy.Handler() {
            @Override
            public void hasNewData(CheckPoint checkPoint) {

            }

            @Override
            public void isReady() {

            }

            @Override
            public void isFinished() {

            }

            @Override
            public void hasLeft() {

            }

            @Override
            public void opponentInRoom() {

            }
        }, owner, "testID");
    }

    @Test
    public void constructorCorrectlyInstantiateChallenge() {
        createCorrectProxy(true);
        FirebaseProxy proxyNotOwner = createCorrectProxy(false);
        proxyNotOwner.abortChallenge();
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIllegalArgumentOnNull() {
        new FirebaseProxy(LOCAL_USER, REMOTE_OPPONENT, null, true, "testID");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIllegalArgumentOnEmpty() {
        new FirebaseProxy(LOCAL_USER, "", new ChallengeProxy.Handler() {
            @Override
            public void hasNewData(CheckPoint checkPoint) {

            }

            @Override
            public void isReady() {

            }

            @Override
            public void isFinished() {

            }

            @Override
            public void hasLeft() {

            }

            @Override
            public void opponentInRoom() {

            }
        }, true, "testID");
    }

    @Test
    public void checkForPreviousStateReadyTriggerCallback() {
        FirebaseProxy proxyOwner = createCorrectProxy(true);
        proxyOwner.imReady();
        createCorrectProxy(false);
    }

    @Test
    public void checkForPreviousStateAbortTriggerCallback() {
        FirebaseProxy proxyOwner = createCorrectProxy(true);
        proxyOwner.abortChallenge();
        createCorrectProxy(false);
    }

    @Test
    public void imReadyWorksAndTriggerCallback() {
        FirebaseProxy proxyOwner = createCorrectProxy(true);
        FirebaseProxy proxyNotOwner = createCorrectProxy(false);
        readyAndStartChallenge(proxyOwner, proxyNotOwner);
    }

    private void readyAndStartChallenge(FirebaseProxy owner, FirebaseProxy slave) {
        owner.imReady();
        slave.imReady();
        owner.startChallenge();
        slave.startChallenge();
    }

    @Test
    public void imReadyOnTerminatedChallenge() {
        FirebaseProxy proxyOwner = createCorrectProxy(true);
        proxyOwner.abortChallenge();
        proxyOwner.imReady();
    }

    @Test
    public void startChallengeOnTerminatedChallenge() {
        FirebaseProxy proxyOwner = createCorrectProxy(true);
        proxyOwner.abortChallenge();
        proxyOwner.startChallenge();
    }

    @Test
    public void correctlyPutDataAndTriggerCallback() {
        FirebaseProxy proxyOwner = createCorrectProxy(true);
        FirebaseProxy proxyNotOwner = createCorrectProxy(false);
        readyAndStartChallenge(proxyOwner, proxyNotOwner);
        proxyOwner.putData(new CheckPoint(90.0, 90.0));
        proxyNotOwner.putData(new CheckPoint(90.0, 90.0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void putDataThrowsIllegalArgument() {
        FirebaseProxy proxy = createCorrectProxy(true);
        proxy.putData(null);
    }

    @Test
    public void putDataOnTerminatedChallenge() {
        FirebaseProxy proxyOwner = createCorrectProxy(true);
        proxyOwner.abortChallenge();
        proxyOwner.putData(new CheckPoint(90.0, 90.0));
    }

    @Test
    public void imFinishWorksAndTriggersCallback() {
        FirebaseProxy proxyOwner = createCorrectProxy(true);
        FirebaseProxy proxyNotOwner = createCorrectProxy(false);
        readyAndStartChallenge(proxyOwner, proxyNotOwner);
        proxyNotOwner.imFinished();
        SystemClock.sleep(2000);
        proxyOwner.imFinished();
    }

    @Test
    public void imFinishedOnTerminatedChallenge() {
        FirebaseProxy proxyOwner = createCorrectProxy(true);
        proxyOwner.abortChallenge();
        proxyOwner.imFinished();
    }

    @Test
    public void abortChallengeWorksAndTriggersCallback() {
        FirebaseProxy proxyOwner = createCorrectProxy(true);
        FirebaseProxy proxyNotOwner = createCorrectProxy(false);
        readyAndStartChallenge(proxyOwner, proxyNotOwner);
        proxyNotOwner.abortChallenge();
    }

    @Test
    public void abortChallengeOnTerminatedChallenge() {
        FirebaseProxy proxyOwner = createCorrectProxy(true);
        proxyOwner.abortChallenge();
        proxyOwner.abortChallenge();
    }

    @Test
    public void generateChallengeNameWorks() {
        String challengeName = FirebaseProxy.generateChallengeName("A", "B", "0");

        Assert.assertTrue(challengeName.equals("A vs B 0"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateChallengeNameThrowsIllegalArgumentOnNull() {
        FirebaseProxy.generateChallengeName("A", "B", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateChallengeNameThrowsIllegalArgumentOnEmpty() {
        FirebaseProxy.generateChallengeName("A", "", "0");
    }

    @Test
    public void deleteChallengeWorks() {
        FirebaseProxy proxy = createCorrectProxy(true);
        proxy.deleteChallenge();
    }
}
