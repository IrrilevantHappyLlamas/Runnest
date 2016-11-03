package ch.epfl.sweng.project;

import org.junit.Assert;
import org.junit.Test;

import ch.epfl.sweng.project.Model.AuthenticatedUser;
import ch.epfl.sweng.project.Model.TestUser;
import ch.epfl.sweng.project.Model.User;

/**
 * Test suite for User classes
 */
public class UserTest {

    @Test
    public void instantiationAndGettersWorkForTestUser() {

        User testUser = new TestUser();

        Assert.assertEquals("Test User", testUser.getId());
        Assert.assertEquals("Test User", testUser.getEmail());
        Assert.assertEquals("Test User", testUser.getFamilyName());
        Assert.assertEquals("Test User", testUser.getName());
        Assert.assertEquals("", testUser.getPhotoUrl());
        Assert.assertEquals("6VauzC82b6YoNfRSo2ft4WFqoCu1", testUser.getFirebaseId());

    }

    @Test(expected = IllegalArgumentException.class)
    public void authUserWithNullAccountThrowsException() {
        new AuthenticatedUser(null);
    }
}
