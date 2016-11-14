package ch.epfl.sweng.project;

import org.junit.Test;

import ch.epfl.sweng.project.Firebase.FirebaseHelper;
import ch.epfl.sweng.project.Firebase.FirebaseProxy;
import ch.epfl.sweng.project.Model.ChallangeProxy;
import ch.epfl.sweng.project.Model.CheckPoint;

/**
 * Test suite for the firebase Proxy
 */
public class FirebaseProxyTest {

    private String LOCAL_USER = "testLocalUser";
    private String REMOTE_OPPONENT = "testRemoteOpponent";

    public FirebaseProxy createCorrectProxy() {
        return  new FirebaseProxy(LOCAL_USER, REMOTE_OPPONENT, new ChallangeProxy.Handler() {
            @Override
            public void OnNewDataHandler(CheckPoint checkPoint) {

            }

            @Override
            public void isReadyHandler() {

            }
        });
    }

    @Test
    public void constructorCorrectlyInstantiateChallenge() {
        createCorrectProxy();
    }

    @Test(expected = NullPointerException.class)
    public void constructorThrowsNullPointer() {
        new FirebaseProxy(LOCAL_USER, REMOTE_OPPONENT, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIllegalArgument() {
        new FirebaseProxy(LOCAL_USER, "", new ChallangeProxy.Handler() {
            @Override
            public void OnNewDataHandler(CheckPoint checkPoint) {

            }

            @Override
            public void isReadyHandler() {

            }
        });
    }

    @Test
    public void correctlyPutData() {
        FirebaseProxy proxy = createCorrectProxy();
        proxy.putData(new CheckPoint(100, 100));
    }

    @Test(expected = NullPointerException.class)
    public void putDataThrowsNullPointer() {
        FirebaseProxy proxy = createCorrectProxy();
        proxy.putData(null);
    }

    @Test
    public void triggerOnDataChange() {
        FirebaseProxy proxy = createCorrectProxy();
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.addChallengeCheckPoint(new CheckPoint(20, 20),
                LOCAL_USER + "_vs_" + REMOTE_OPPONENT,
                REMOTE_OPPONENT, 0);
    }

    @Test
    public void correctImReady() {
        FirebaseProxy proxy = createCorrectProxy();
        proxy.imReady();
    }

}
