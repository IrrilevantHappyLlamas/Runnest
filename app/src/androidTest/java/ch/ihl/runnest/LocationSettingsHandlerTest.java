package ch.ihl.runnest;

import org.junit.Test;

import ch.ihl.runnest.Fragments.RunFragments.LocationSettingsHandler;

public class LocationSettingsHandlerTest {

    @Test(expected = IllegalArgumentException.class)
    public void constructorMustThrowExceptionWithNullArguments(){
        new LocationSettingsHandler(null, null);
    }
}