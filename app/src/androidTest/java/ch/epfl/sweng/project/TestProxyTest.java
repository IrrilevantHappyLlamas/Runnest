package ch.epfl.sweng.project;

import org.junit.Test;

import ch.epfl.sweng.project.Model.ChallengeProxy;
import ch.epfl.sweng.project.Model.CheckPoint;

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
