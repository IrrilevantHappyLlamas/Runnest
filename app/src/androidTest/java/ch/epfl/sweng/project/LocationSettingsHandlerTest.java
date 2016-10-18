package ch.epfl.sweng.project;

import org.junit.Test;

import ch.epfl.sweng.project.Fragments.NewRun.LocationSettingsHandler;

public class LocationSettingsHandlerTest {

    @Test(expected = IllegalArgumentException.class)
    public void constructorMustThrowExceptionWithNullArguments(){
        new LocationSettingsHandler(null, null);
    }
}