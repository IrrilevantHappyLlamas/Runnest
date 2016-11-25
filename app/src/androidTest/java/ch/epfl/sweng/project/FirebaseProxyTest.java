package ch.epfl.sweng.project;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;

import org.junit.Test;

import ch.epfl.sweng.project.Firebase.FirebaseHelper;
import ch.epfl.sweng.project.Firebase.FirebaseProxy;
import ch.epfl.sweng.project.Model.ChallengeProxy;
import ch.epfl.sweng.project.Model.CheckPoint;

/**
 * Test suite for the firebase Proxy
 */
public class FirebaseProxyTest {

    private String LOCAL_USER = "testLocalUser";
    private String REMOTE_OPPONENT = "testRemoteOpponent";

    public FirebaseProxy createCorrectProxy(boolean owner) {

        String local = owner?LOCAL_USER:REMOTE_OPPONENT;
        String remote = owner?REMOTE_OPPONENT:LOCAL_USER;
        return  new FirebaseProxy(local, remote, new ChallengeProxy.Handler() {
            @Override
            public void OnNewDataHandler(CheckPoint checkPoint) {

            }

            @Override
            public void isReadyHandler() {

            }

            @Override
            public void isFinished() {

            }
        }, owner);
    }

    @Test
    public void constructorCorrectlyInstantiateChallenge() {
        FirebaseProxy proxyOwner = createCorrectProxy(true);
        createCorrectProxy(false);
        proxyOwner.deleteChallenge();

    }

    @Test(expected = NullPointerException.class)
    public void constructorThrowsNullPointer() {
        new FirebaseProxy(LOCAL_USER, REMOTE_OPPONENT, null, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIllegalArgument() {
        new FirebaseProxy(LOCAL_USER, "", new ChallengeProxy.Handler() {
            @Override
            public void OnNewDataHandler(CheckPoint checkPoint) {

            }

            @Override
            public void isReadyHandler() {

            }

            @Override
            public void isFinished() {

            }
        }, true);
    }

    @Test
    public void imReadyWorksAndTriggerCallback() {
        FirebaseProxy proxyOwner = createCorrectProxy(true);
        FirebaseProxy proxyNotOwner = createCorrectProxy(false);
        readyAndStartChallenge(proxyOwner, proxyNotOwner);
        proxyOwner.deleteChallenge();
    }

    private void readyAndStartChallenge(FirebaseProxy owner, FirebaseProxy slave) {
        owner.imReady();
        slave.imReady();
        owner.startChallenge();
        slave.startChallenge();
    }

    @Test
    public void correctlyPutDataAndTriggerCallback() {
        FirebaseProxy proxyOwner = createCorrectProxy(true);
        FirebaseProxy proxyNotOwner = createCorrectProxy(false);
        readyAndStartChallenge(proxyOwner, proxyNotOwner);
        proxyOwner.putData(new CheckPoint(100.0, 100.0));
        proxyNotOwner.putData(new CheckPoint(100.0, 100.0));
        proxyOwner.deleteChallenge();
    }

    @Test(expected = NullPointerException.class)
    public void putDataThrowsNullPointer() {
        FirebaseProxy proxy = createCorrectProxy(true);
        proxy.putData(null);
    }

    @Test
    public void imFinishWorksAndTriggersCallback() {
        FirebaseProxy proxyOwner = createCorrectProxy(true);
        FirebaseProxy proxyNotOwner = createCorrectProxy(false);
        readyAndStartChallenge(proxyOwner, proxyNotOwner);
        proxyNotOwner.imFinished();
        proxyOwner.imFinished();
    }

    @Test(expected = IllegalStateException.class)
    public void startChallengeThrowsIllegalState() {
        FirebaseProxy proxyOwner = createCorrectProxy(true);
        proxyOwner.deleteChallenge();
        proxyOwner.startChallenge();
    }

    @Test(expected = IllegalStateException.class)
    public void imReadyThrowsIllegalState() {
        FirebaseProxy proxyOwner = createCorrectProxy(true);
        proxyOwner.deleteChallenge();
        proxyOwner.imReady();
    }

    @Test(expected = IllegalStateException.class)
    public void imFinishedThrowsIllegalState() {
        FirebaseProxy proxyOwner = createCorrectProxy(true);
        proxyOwner.deleteChallenge();
        proxyOwner.imFinished();
    }

    @Test(expected = IllegalStateException.class)
    public void putDataThrowsIllegalState() {
        FirebaseProxy proxyOwner = createCorrectProxy(true);
        proxyOwner.deleteChallenge();
        proxyOwner.putData(new CheckPoint(100.0, 100.0));
    }


    @Test(expected = DatabaseException.class)
    public void onCancelledThrowsException() {
        FirebaseProxy proxyOwner = createCorrectProxy(true);
        proxyOwner.onCancelled(DatabaseError.fromException(new NullPointerException()));
    }

}
