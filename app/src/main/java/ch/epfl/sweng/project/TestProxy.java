package ch.epfl.sweng.project;

import ch.epfl.sweng.project.Model.ChallengeProxy;
import ch.epfl.sweng.project.Model.CheckPoint;

/**
 * Challenge proxy for test sessions.
 */
public class TestProxy implements ChallengeProxy {

    private Handler handler = null;
    private android.os.Handler runnableHandler = new android.os.Handler();
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            sendNextPoint();
            runnableHandler.postDelayed(runnableCode, 1000);
        }
    };

    private double lat;
    private double lon;

    private boolean terminated = false;

    public TestProxy(Handler handler) {
        this.handler = handler;
        lat = 45.0;
        lon = 45.0;
    }

    private void sendNextPoint() {
        /*
        if(!terminated) {
            handler.hasNewData(new CheckPoint(lat, lon));
            lat += 0.05;
            lon += 0.05;
        }
        */
    }

    @Override
    public void putData(CheckPoint checkPoint) {

    }

    @Override
    public void startChallenge() {

    }

    @Override
    public void imReady() {
        handler.isReady();
        runnableHandler.post(runnableCode);
    }

    @Override
    public void imFinished() {
        handler.isFinished();
    }

    @Override
    public void abortChallenge() {

    }
}
