package ch.epfl.sweng.project;

import junit.framework.Assert;

import org.junit.Test;

import ch.epfl.sweng.project.Model.Profile;
import ch.epfl.sweng.project.Model.Run;

public class ProfileTest {
    @Test
    public void getterWorks() {
        Profile profile = new Profile(null);
        Assert.assertEquals("No User", profile.getId());
        Assert.assertEquals("no.user@invalid.null", profile.getEmail());
        Assert.assertEquals("No User", profile.getFamilyName());
        Assert.assertEquals("No User", profile.getName());
        Assert.assertEquals("", profile.getPhotoUrl());
        Assert.assertEquals(0.0f, profile.getTotalDistance());
        Assert.assertEquals("no_dot_user_at_invalid_dot_null", profile.getFireBaseMail());
    }

    @Test
    public void canAddRun() {
        Profile profile = new Profile(null);
        Assert.assertEquals(0.0f, profile.getTotalDistance());

        Run testRun = new Run();
        testRun.start();
        Assert.assertTrue(testRun.update(TrackTest.buildCheckPoint(1, 1)));
        Assert.assertTrue(testRun.isRunning());
        Assert.assertTrue(testRun.update(TrackTest.buildCheckPoint(2, 2)));
        Assert.assertTrue(testRun.stop());

        float totalDistance = testRun.getTrack().getDistance();

        profile.addRun(testRun);
        Assert.assertEquals(totalDistance, profile.getTotalDistance());
    }
}
