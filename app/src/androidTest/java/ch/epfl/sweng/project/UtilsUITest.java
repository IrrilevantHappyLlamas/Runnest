package ch.epfl.sweng.project;

import junit.framework.Assert;

import org.junit.Test;

import ch.epfl.sweng.project.Model.Track;

public class UtilsUITest {

    @Test(expected = IllegalArgumentException.class)
    public void timeToStringThrowException() {
        UtilsUI.timeToString(-1, false);
    }

    @Test
    public void timeToStringWithoutHoursDoNotShowHoursIfNotNecessary() {
        String timeString = UtilsUI.timeToString(72, false);
        String checkString = "01:12";

        Assert.assertEquals(checkString, timeString);
    }

    @Test
    public void timeToStringWithoutHoursShowHoursIfNecessary() {
        String timeString = UtilsUI.timeToString(3672, false);
        String checkString = "01:01:12";

        Assert.assertEquals(checkString, timeString);
    }

    @Test
    public void timeToStringWorkCorrectlyWithHours() {
        String timeString = UtilsUI.timeToString(72, true);
        String checkString = "00:01:12";

        Assert.assertEquals(checkString, timeString);
    }

    @Test(expected = IllegalArgumentException.class)
    public void recapDisplayTrackSetupUIThrowException() {
        UtilsUI.recapDisplayTrackSetupUI(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void recapDisplayTrackThrowExceptionWithNullTrack() {
        UtilsUI.recapDisplayTrack(null, null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void displayTrackThrowExceptionWithNullMap() {
        UtilsUI.recapDisplayTrack(new Track(), null, 0);
    }
}
