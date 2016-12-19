package ch.epfl.sweng.project;

import ch.epfl.sweng.project.Model.ChallengeProxy;
import ch.epfl.sweng.project.Model.CheckPoint;

/**
 * Challenge proxy for test sessions. It implements only the data transfer functions by delegating to the handler
 * passed during tests. It doesn't implement the behavior to leave or deleteMessage challenges, because they will only be
 * local with this proxy, and tests know when the fake challenge is over, so that behavior is not needed.
 */
public class TestProxy implements ChallengeProxy {

    private Handler handler = null;

    /**
     * Public constructor for the test proxy, which takes a Handler to which it delegates all the work.
     *
     * @param handler   A non null handler that implements the behavior of the TestProxy
     */
    public TestProxy(Handler handler) {

        if (handler == null) {
            throw new IllegalArgumentException("Handler for TestProxy can't be null");
        }

        this.handler = handler;
    }

    @Override
    public void putData(CheckPoint checkPoint) {

        if (checkPoint == null) {
            throw new IllegalArgumentException("Even in tests, a CheckPoint passed for a challenge can't be null");
        }

        handler.hasNewData(checkPoint);
    }

    @Override
    public void startChallenge() {}

    @Override
    public void imReady() {
        handler.isReady();
    }

    @Override
    public void imFinished() {
        handler.isFinished();
    }

    @Override
    public void abortChallenge() {}

    @Override
    public void deleteChallenge() {}
}
