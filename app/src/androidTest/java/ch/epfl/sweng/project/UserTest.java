package ch.epfl.sweng.project;

import android.net.Uri;
import android.os.Parcel;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

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

        testUser.loginStatus();
        testUser.logoutStatus();

        Assert.assertEquals("Test User", testUser.getId());
        Assert.assertEquals("Test User", testUser.getEmail());
        Assert.assertEquals("Test User", testUser.getFamilyName());
        Assert.assertEquals("Test User", testUser.getName());
        Assert.assertEquals("", testUser.getPhotoUrl());
        Assert.assertEquals("6VauzC82b6YoNfRSo2ft4WFqoCu1", testUser.getFirebaseId());
        Assert.assertTrue(testUser.isLoggedIn());

    }

    @Test(expected = IllegalArgumentException.class)
    public void authUserWithNullAccountThrowsException() {
        new AuthenticatedUser(null);
    }

    @Test
    public void gettersWorkForAuthUser() {

        try {
            GoogleSignInAccount googleUser = GoogleSignInAccount.zzfw(" ");
            User testUser = new AuthenticatedUser(googleUser);
            testUser.getId();
            testUser.getEmail();
            testUser.getFamilyName();
            testUser.getName();
            testUser.getPhotoUrl();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
