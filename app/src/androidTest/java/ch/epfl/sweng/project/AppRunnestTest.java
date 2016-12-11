package ch.epfl.sweng.project;

import org.junit.Assert;
import org.junit.Test;

import ch.epfl.sweng.project.Model.TestUser;

/**
 * Test suite for AppRunnest
 */
public class AppRunnestTest {

    @Test(expected = IllegalArgumentException.class)
    public void setUserThrowsIllegalArgument() {
        new AppRunnest().setUser(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setApiClientThrowsIllegalArgument() {
        new AppRunnest().setApiClient(null);
    }

    @Test
    public void setUserWorks() {
        AppRunnest testApp = new AppRunnest();
        testApp.setUser(new TestUser());

        Assert.assertTrue(testApp.getUser().getName().equals("Test User"));
    }

    @Test
    public void unsetNetworkHandlerIsNull() {
        Assert.assertTrue(new AppRunnest().getNetworkHandler() == null);
    }

    @Test
    public void setTestSessionWorks() {
        AppRunnest testApp = new AppRunnest();

        Assert.assertFalse(testApp.isTestSession());
        testApp.setTestSession(true);
        Assert.assertTrue(testApp.isTestSession());
        testApp.setTestSession(false);
        Assert.assertFalse(testApp.isTestSession());
    }
}
