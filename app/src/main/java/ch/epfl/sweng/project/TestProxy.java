package ch.epfl.sweng.project;

import ch.epfl.sweng.project.Model.ChallengeProxy;
import ch.epfl.sweng.project.Model.CheckPoint;

/**
 * Challenge proxy for test sessions.
 */
public class TestProxy implements ChallengeProxy {

    private Handler handler = null;

    public TestProxy(Handler handler) {
        this.handler = handler;
    }


    @Override
    public void putData(CheckPoint checkPoint) {
        handler.hasNewData(checkPoint);
    }

    @Override
    public void startChallenge() {

    }

    @Override
    public void imReady() {
        handler.isReady();
    }

    @Override
    public void imFinished() {
        handler.isFinished();
    }

    @Override
    public void abortChallenge() {

    }

    @Override
    public void deleteChallenge() {

    }
}
