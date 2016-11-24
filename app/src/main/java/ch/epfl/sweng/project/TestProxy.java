package ch.epfl.sweng.project;

import android.os.SystemClock;

import java.util.logging.Handler;

import ch.epfl.sweng.project.Model.ChallengeProxy;
import ch.epfl.sweng.project.Model.CheckPoint;

/**
 * Challenge proxy for test sessions.
 */
public class TestProxy implements ChallengeProxy {

    private Handler handler = null;

    private double lat;
    private double lon;

    public TestProxy(Handler handler) {
        this.handler = handler;
        lat = 45.0;
        lon = 45.0;
    }

    private void sendNextPoint() {

        for(int i = 0; i < 2; ++i) {
            handler.OnNewDataHandler(new CheckPoint(lat, lon));
            SystemClock.sleep(500);
            lat += 0.01;
            lon += 0.01;
        }
    }

    @Override
    public void putData(CheckPoint checkPoint) {

    }

    @Override
    public void startChallenge() {
        /*
        for(int i = 0; i < 2; ++i) {
            SystemClock.sleep(2000);
            handler.OnNewDataHandler(new CheckPoint(lat, lon));
            lat += 0.01;
            lon += 0.01;
        }
        */
    }

    @Override
    public void deleteChallenge() {
    }

    @Override
    public void imReady() {
        handler.isReadyHandler();

    }

    @Override
    public void imFinished() {
        handler.isFinished();
    }
}
