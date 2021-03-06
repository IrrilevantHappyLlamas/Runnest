package ch.ihl.runnest;

import org.junit.Test;

import ch.ihl.runnest.Model.ChallengeProxy;
import ch.ihl.runnest.Model.CheckPoint;

/**
 * Test suite for TestProxy
 */
public class TestProxyTest {

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIllegalArgument() {
        new TestProxy(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putDataThrowsIllegalArgument() {
        new TestProxy(new ChallengeProxy.Handler() {
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
        }).putData(null);
    }
}
